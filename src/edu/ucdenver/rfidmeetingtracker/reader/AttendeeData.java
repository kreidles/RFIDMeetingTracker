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

/**
 * POJO containing meeting attendee information
 * @author Sarah Kreidler
 *
 */
public class AttendeeData {
    // date/time stamp when card was swiped
    private Date cardSwipeDate;
    // attendee's ID number from RFID
    private int attendeeID;

    /**
     * Create an AttendeeData object
     * @param cardSwipeDate date/time of card swipe
     * @param attendeeID attendee ID read from RFID card
     */
    public AttendeeData(Date cardSwipeDate, int attendeeID) {
        this.cardSwipeDate = cardSwipeDate;
        this.attendeeID = attendeeID;
    }

    public Date getCardSwipeDate() {
        return cardSwipeDate;
    }

    public void setCardSwipeDate(Date cardSwipeDate) {
        this.cardSwipeDate = cardSwipeDate;
    }

    public int getAttendeeID() {
        return attendeeID;
    }

    public void setAttendeeID(int attendeeID) {
        this.attendeeID = attendeeID;
    }
    
    
}
