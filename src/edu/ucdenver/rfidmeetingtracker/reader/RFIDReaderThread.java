/*
 * RFIDMeetingTracker is a simple windows service for storing
 * RFID swipe card data in order to track meeting attendance
 *
 * Copyright (C) 2014 Regents of the University of Colorado.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package edu.ucdenver.rfidmeetingtracker.reader;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * Main polling loop to read from RFID card reader
 * @author Sarah Kreidler
 *
 */
public class RFIDReaderThread extends Thread {

    // assumes the current class is called logger
    private final static Logger LOGGER = Logger.getLogger(RFIDReaderThread.class.getName()); 
    // blocking queue of attendee data for processing
    private BlockingQueue<AttendeeData> attendeeDataQueue = null;
    // length of data buffer for card reads
    private static final short BUFSIZE = 8;
    // id information is in the last two bytes of the RFID
    private static final short ID_START_BYTE = 1;
    // flag to allow clean shutdown of the reader thread
    private boolean terminated = false;

    private class RawFormatter extends Formatter {
        public String format(LogRecord record) {
            return record.getMessage() + "\n";
        }
    }
    
    // load the native dll for interacting with the card reader
    // p.s. Thank you JNA developers for being awesome!
    private interface pcProxDLL extends Library {

        pcProxDLL INSTANCE = (pcProxDLL) Native.loadLibrary("pcProxAPI.dll", pcProxDLL.class);
        /**
         * Connect to the card reader via USB
         * (note, the api does not contain a corresponding disconnect function)
         */
        short usbConnect(); 

        /**
         * Make the card reader beep
         * @param count number of beeps
         * @param longBeep if 1, the beeps will be longer in duration, else short
         */
        short BeepNow(int count, int longBeep); 

        /**
         * Read the current card ID 
         */
        short GetActiveID(byte[] buffer, short sizeOfByteBuffer);
    }

    /**
     * Create a new card reader thread
     * @param attendeeDataQueue
     */
    public RFIDReaderThread(BlockingQueue<AttendeeData> attendeeDataQueue, String pathToLogFile) 
    throws IOException {
        this.attendeeDataQueue = attendeeDataQueue;
        
        // set up logging
        LOGGER.setLevel(Level.INFO);
        FileHandler fh = new FileHandler(pathToLogFile, true);
        fh.setFormatter(new RawFormatter());
        LOGGER.addHandler(fh);
        
    }

    /**
     * Shutdown the reader thread
     */
    public void softTerminate() {
        terminated = true;
    }

    /**
     * Run the reader thread.  Connects to the
     * pcProx card reader and polls for cards every
     * 250ms.
     */
    public void run() throws RuntimeException {
        System.out.println(new Date() + ": Starting reader thread");
        try {
            // native dll for interacting with card reader
            pcProxDLL pcProx = pcProxDLL.INSTANCE;

            // connect to the device
            short rc = pcProx.usbConnect();
            if(rc == 0) {
                throw new RuntimeException("No USB pcProx found.");
            } else {
                System.out.println(new Date() + "Connected to card reader");
                // number of bits read from device
                short bits = 0;
                // buffer to hold card information
                byte[] buffer = new byte[BUFSIZE];
                // initial the last seen id number to an invalid value
                int lastID = Integer.MIN_VALUE;

                // start polling the card reader
                while(!terminated) {
                    bits = pcProx.GetActiveID(buffer, BUFSIZE);
                    if (bits > 0) {
                        /*
                         * The card data contains 8 bytes of information.
                         * The ID information is in the last two bytes.
                         */
                        StringBuffer idBuffer = new StringBuffer();
                        for(int i = ID_START_BYTE; i >= 0; i--) {
                            idBuffer.append(String.format("%02X", buffer[i]));
                        }
                        // parse the id number back into an integer
                        int id = Integer.parseInt(idBuffer.toString(), 16);

                        /*
                         *  add the card information to the processing queue.
                         *  Note, we get duplicate card information every polling cycle
                         *  if the attendee continues to hold the card on the reader.
                         *  Therefore, we only process the information when the card
                         *  changes. 
                         */
                        if (lastID != id) {
                            lastID = id;
                            AttendeeData data = new AttendeeData(new Date(), id);
                            // write to local log file
                            LOGGER.info(data.getCardSwipeDate() + "," + data.getAttendeeID());
                            // schedule for processing
                            attendeeDataQueue.add(data);
                        }

                    } else {
                        // clear the last seen ID
                        lastID = Integer.MIN_VALUE;
                    }
                    try {
                        Thread.sleep(250);
                    } catch(InterruptedException e) {
                        // no action needed
                    }
                }   
            }
        } catch(Exception e) {
            // no action needed
        }
    }
}
