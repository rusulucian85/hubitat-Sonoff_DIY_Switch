# hubitat-Sonoff_DIY_Switch
Yet another sonoff diy driver, created from scratch using sonoff official API documentation.


Tested with MiniR1 device, working like a charm.


1. Power on;
2. Long press the button for 5 seconds to enter Compatible Pairing Mode (AP)
3. User tips: If the device has been paired with eWeLink APP, reset the device is necessary by long press the pairing button for 5 seconds, then press another 5 seconds for entering Compatible Pairing Mode (AP)
4. The LED indicator will blink continuously
5. From mobile phone or PC WiFi setting, an Access Point of the device named ITEAD-XXXXXXXXXX will be found, connect it with default password 12345678
6. Open the browser and access http://10.10.7.1/
7. Next, fill in WiFi SSID and password that the device would have connected with
8. Succeed, now the device is in DIY Mode.
9. Install this driver and you're good to go.
