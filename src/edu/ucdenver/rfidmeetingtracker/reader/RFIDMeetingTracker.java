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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Main routine for reading from a pcProx RFID card reader.
 * Data is logged to a local file and sent to a central server.
 * 
 * @author Sarah Kreidler
 *
 */
public class RFIDMeetingTracker 
{
    // Poll the device every second to check for a new card 
    public static void main(String[] args) {
        // shared queue of attendee data
        // blocking queue of attendee data for processing
        BlockingQueue<AttendeeData> attendeeDataQueue = 
                new ArrayBlockingQueue<AttendeeData>(100);

        try {
            // thread to read the card data
            RFIDReaderThread readerThread = new RFIDReaderThread(attendeeDataQueue, "attendeeLog.csv");
            // thread to process card data
            RFIDDataProcessorThread processorThread = new RFIDDataProcessorThread(attendeeDataQueue);

            // fire up the reader and processor threads
            readerThread.start();
            processorThread.start();
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        } catch (IOException ie) {
            System.out.println(ie.getMessage());
        }
    }
}




