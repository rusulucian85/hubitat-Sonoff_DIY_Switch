/**
 * Sonoff DIY devices

 * MIT License

 * Copyright (c) 2021 Lucian Rusu

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 *  Change History:
 *
 *    Date        Who             What
 *    ----        ---             ----
 *    2021-07-31  Lucian Rusu   Original Creation (v1)
 * 
 */

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

metadata {
    definition (name: "Sonoff DIY WiFi", namespace: "sonoff", author: "Lucian Rusu", importUrl: "") {
        command "on"
        command "off"
        capability "SignalStrength"
        command "push"
	}
    
    preferences {
        input(name: "deviceIP", type: "string", title:"<b>Device IP Address</b>", description: "<p style= 'color:#ff0000; font-size:12px;'><i>Enter IP Address of your Sonoff device</i></p></br></br>", required: true, displayDuringSetup: true)
        input(name: "devicePort", type: "number", title:"<b>Device Port</b>", description: "<p style= 'color:#ff0000; font-size:12px;'><i>Enter Port of your Sonoff device (default: 8081)</i></p></br></br>", defaultValue: "8081", required: false, displayDuringSetup: true)  
        input(name: "PowerState", type: "enum", title: "<b>Power-on State</b>", options: ["on","off","stay"], description: "<p style= 'color:#ff0000; font-size:12px;'><i>on: the device is on when power supply is recovered </br>off: the device is off when power supply is recovered </br>stay: the device status keeps as the same as the state before power supply is gone</i></p>", defaultValue: "stay", required: false, displayDuringSetup: true)
        input name: "Inching", type: "bool", title: "<b>Pulse method</b>", defaultValue: false, description: "<p style= 'color:#ff0000; font-size:12px;'><i>on: activate the inching function </br>off: disable the inching function</i></p>", required: false
    }
    
}



def updated() {
    def params1 =
        [
            uri: "http://"+ deviceIP + ":" + devicePort + "/zeroconf/startup",
            body: [deviceid:"", data: [startup: PowerState]]
        ]
    
    
    def params2 =
        [
            uri: "http://"+ deviceIP + ":" + devicePort + "/zeroconf/pulse",
            body: [deviceid:"", data: [pulse: Inching ? "on":"off", pulseWidth: 1000]]
        ]
    
    
    
    try
    {
        def result
        httpPostJson(params1)  
        {resp ->
            if (resp.data)
            {   
                sendEvent(name: "settings", descriptionText: "update with: " + "IP: " + deviceIP + ", Port: " + devicePort + ", State: " + PowerState, value: " ", isStateChange: true)
            }
        }
    }
    catch (Exception e)
    {
        sendEvent(name: "settings",descriptionText: e.message, value: " ", isStateChange: true) 
    }
    
     try
    {
        def result
        httpPostJson(params2)  
        {resp ->
            if (resp.data)
            {   
                sendEvent(name: "settings", descriptionText: "update with: Inching: " + Inching, value: " ", isStateChange: true)
            }
        }
    }
    catch (Exception e)
    {
        sendEvent(name: "settings",descriptionText: e.message, value: " ", isStateChange: true) 
    }
    
    refresh()
}


def on() {
    def params =
        [
            uri: "http://"+ deviceIP + ":" + devicePort + "/zeroconf/switch",
            body: [deviceid:"", data: [switch: "on"]]
        ]
    
    try
    {
        def result
        httpPostJson(params)  
        {resp ->
            if (resp.data)
            {   
                sendEvent(name: "switch",descriptionText: resp.data, value: "on", isStateChange: true)
            }
        }
    }
    catch (Exception e)
    {
        sendEvent(name: "switch",descriptionText: e.message, value: "on", isStateChange: true) 
    }
}

def off() {
    def params =
        [
            uri: "http://"+ deviceIP + ":" + devicePort + "/zeroconf/switch",
            body: [deviceid:"", data: [switch: "off"]]
        ]
    try
    {
        def result
        httpPostJson(params)  
        {resp ->
            if (resp.data)
            {   
                sendEvent(name: "switch",descriptionText: resp.data, value: "off", isStateChange: true)
            }
        }
    }
    catch (Exception e)
    {
        sendEvent(name: "switch",descriptionText: e.message, value: "off", isStateChange: true) 
    }
}
    
   
def push(){
    sendEvent(name: "switch", value: "push", isStateChange: true)
    on()
    runIn(1,off)
    
}

def refresh(){
    //state.remove("xxx")

    def params =
        [
            uri: "http://"+ deviceIP + ":" + devicePort + "/zeroconf/info",
            contentType: "application/json",
            body: [deviceid:"", data:[]]
        ]
    try
    {
        def result
        httpPostJson(params)  
        {resp ->
            if (resp.data)
            {   
                j_output = JsonOutput.toJson(resp.data)
                j_parsed = new JsonSlurper().parseText(j_output)
                sendEvent(name: "switch", descriptionText: "switch", value: j_parsed.data.switch, isStateChange: true)
                sendEvent(name: "startup", descriptionText: "startup", value: j_parsed.data.startup, isStateChange: true)
                sendEvent(name: "pulse", descriptionText: "pulse", value: j_parsed.data.pulse, isStateChange: true)
                sendEvent(name: "pulseWidth", descriptionText: "pulseWidth", value: j_parsed.data.pulseWidth, isStateChange: true)
                
            }
        }
    }
    catch (Exception e)
    {
        sendEvent(name: "refresh", descriptionText: e.message, value: " ", isStateChange: true) 
    }
    runIn(30,refresh)
}
