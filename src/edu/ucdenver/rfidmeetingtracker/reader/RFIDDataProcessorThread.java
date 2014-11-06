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

import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Thread which receives attendee data, writes it
 * to a local log file, and sends to the central repository
 * @author Sarah Kreidler
 *
 */
public class RFIDDataProcessorThread extends Thread {
    private static int STOP_REQUEST = Integer.MIN_VALUE;
    // blocking queue of attendee data for processing
    private BlockingQueue<AttendeeData> attendeeDataQueue = null;
    // flag to allow clean interrupt of the thread
    private boolean shuttingDown = false;
    private boolean terminated = false;

    /**
     * Create a new processor thread.
     * 
     * @param attendeeDataQueue shared queue of attendee information.
     */
    public RFIDDataProcessorThread(BlockingQueue<AttendeeData> attendeeDataQueue) {
        this.attendeeDataQueue = attendeeDataQueue;
    }
    
    /**
     * Shutdown the thread
     */
    public void softTerminate() {
        /* push a stop request onto the queue.
         * This allows any scheduled card reads to be processed prior to
         * the thread shutting down
         */
        attendeeDataQueue.add(new AttendeeData(new Date(), STOP_REQUEST));
    }

    /**
     * Add attendee data to the processing queue
     * @param data attendee data
     */
    public void addAttendee(AttendeeData data) {
        if (shuttingDown || terminated) return;
        try {
            attendeeDataQueue.put(data);
        } catch (InterruptedException iex) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Processor thread interrupted");
        }
    }

    /**
     * Read/process attendee data from the queue when it becomes
     * available
     */
    public void run() {
        try {
            AttendeeData newData = null;

            while(!terminated) {

                newData = attendeeDataQueue.take();
                if (newData.getAttendeeID() == STOP_REQUEST) {
                    terminated = true;
                } else {
                    System.out.println("Got item: " + newData.getCardSwipeDate() + " " + 
                            newData.getAttendeeID());
                    // TODO: connect to remote data store
                }
            }
        } catch (InterruptedException iex) {
            // no action needed
        } finally {
            terminated = true;
        } 
    }
}

