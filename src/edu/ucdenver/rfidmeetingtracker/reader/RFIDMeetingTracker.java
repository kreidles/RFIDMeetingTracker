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

import com.sun.jna.Library;
import com.sun.jna.Native;

public class RFIDMeetingTracker 
{
    // length of data buffer for card reads
    private static final short BUFSIZE = 8;
    // id information is in the last two bytes of the RFID
    private static final short ID_START_BYTE = 1;

    // load the native dll for interacting with the card reader
    // p.s. Thank you JNA developers for being awesome!
    public interface pcProxDLL extends Library {

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

    // Poll the device every second to check for a new card 
    public static void main(String[] args) {

        pcProxDLL pcProx = pcProxDLL.INSTANCE;

        short rc = pcProx.usbConnect();
        if(rc == 0)
        {
            System.out.println("Exit: No USB pcProx found.");
        }
        else
        {
            System.out.println("Found RFID reader: " + rc);

            int n = 120;
            short bits = 0;
            byte[] buffer = new byte[BUFSIZE];


            while(n-- > 0)
            {
                bits = pcProx.GetActiveID(buffer, BUFSIZE);
                if (bits > 0) {
                    StringBuffer idBuffer = new StringBuffer();
                    for(int i = ID_START_BYTE; i >= 0; i--) {
                        idBuffer.append(String.format("%02X", buffer[i]));
                    }

                    int id = Integer.parseInt(idBuffer.toString(), 16);
                    System.out.println("Formatted Buffer: " + id);

                } else {
                    System.out.println(n + ": No card found");
                }
                try
                {
                    Thread.sleep(250);
                }
                catch(InterruptedException e) {}
            }   
        }
    }
}




