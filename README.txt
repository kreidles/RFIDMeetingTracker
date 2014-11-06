RFID Meeting Tracker (version __VERSION__)
------------------------------------------

RFID Meeting Tracker is a service for tracking
meeting attendence via RFID cards.  The system reads RFID card information
from a pcProx card reader and logs it to a local file.

Future releases will integrate with data stores via HTTP.

SYSTEM REQUIREMENTS
-------------------

The RFID Meeting Tracker depends on pcProx SDK.  This product
can be purchased from pcProx at

https://www.rfideas.com/products/sdk/universal-enroll-sdk

Other requirements include
- Windows 7 or higher
- Java Runtime Environment 1.8.x or higher

CARD READER CONFIGURATION
-------------------------

By default, the pcProx card reader sends keystrokes to the target
workstation when a card is swiped.  It is recommended that this 
feature is disabled when using RFID Meeting Tracker.

SERVICE CONFIGURATION
-------------

The default path to the pcProx SDK is "C:\Program Files (x86)\RF IDeas\pcProxSDK\OS\Win64"

To change the path, edit the following line in Install-Remove-Service.bat

set RFID_TRACKER_PCPROX_DIR="C:\Program Files (x86)\RF IDeas\pcProxSDK\OS\Win64"

INSTALLATION
------------

The service can be installed as follows:

From the rfidmeetingtracker directory, run

# Install-Remove-Service.bat install

The service can be removed as follows:

# Install-Remove-Service.bat remove


