You can install the app using .apk placed in the root of this repository. You need to define two parameters - phone number and server address.

__Phone number:__ This number will be dialed when the phone is disconnected from charger.

__Server address:__ Address where is accessible write.php script from WEB-UI directory. When phone is not connected to charger it sends data to this script and the data are stored in network.csv and gps.csv files depending on the source of position.

Application behaviour
---------------------
When application is started, a form will appear. Fill required data to the form and press __Set__ button. The data are stored, and they will automatically loaded next time. Before you run the monitoring service, you can check current configuration below the __Set__ button.

When service is started, notification appears in notification bar. The main application can be closed now and serevice waits for power disconnected event. When it occures it starts to send position data and calls the selected number.

You can find if the service is running using __State?__ button and stop stop it using __Stop__ button.

If the number is invalid, i.e. empty string, it does not affect data sending.

If the service is started when power is disconnected it does not call but it sends position data.

Collected data are fetched to map web page every 5 seconds and map data are not reloaded.

TODO
----
Test application on more phones and system versions.

Implement some verification if the phone call was accepted (or refused) or if it was disconnected after timeout. Make other attempts if the called person is not aware of the phone call.

Send device info with position to be able to distinguish two different devices.

Beautify web interface and add selectors for available data.

Make the app more configurable - what data should be sent and their resolution.

Known issues
------------
When there is just one location in csv files it is not shown on the map. Only lines between two points are shown in current implementation.

It takes about 5 seconds until the first data are fetched to the web application.



