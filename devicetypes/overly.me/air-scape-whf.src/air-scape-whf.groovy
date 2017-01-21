/**
* AirScap Whole House Fan Device
*
* Control the speed of an installed fan.  Monitor the temperature from its three sensors and it ft^/min of air flow.
*
* Questions:
* 1. Timer have an upper limit?
*
* Todo:
* 1. Have state turn on/off with level
*
* Notes:
* 1. Command object have to return hub action
* 1. Your hub must be selected in the device
 */

preferences {
        input("ip", "string", title:"IP Address", description: "IP of Fan", defaultValue: "10.0.1.3", required: false, displayDuringSetup: true)
        input("port", "string", title:"Port", description: "Port of Fan", defaultValue: "80" , required: false, displayDuringSetup: true)
}

metadata {
    definition (name:"AirScape WHF", namespace:"overly.me", author:"timothy@overly.me") {
        capability "Refresh"
        capability "Switch"
        capability "Switch Level"
        capability "Polling"
        capability "Power Meter"

        attribute "timeRemaining", "number"
        attribute "atticTemperature", "number"
        attribute "outsideTemperature", "number"
        attribute "insideTemperature", "number"
        attribute "cfm", "number"

        command "levelUp"
		command "levelDown"
        command "addTime"
    }

	tiles(scale: 2) {

        standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: true) {
 		   	state "off", label: '${currentValue}', action: "switch.on", icon: "st.Lighting.light24", backgroundColor: "#ffffff"
    		state "on", label: '${currentValue}', action: "switch.off", icon: "st.Lighting.light24", backgroundColor: "#79b821"
		}

        valueTile("timer", "device.timeRemaining", width: 2, height: 2) {
			state "timeRemaining", label: 'Timer: ${currentValue}'
		}

        valueTile("power", "device.power", width: 2, height: 2) {
			state "power", label: 'Power: ${currentValue}'
		}

        valueTile("timer", "device.timeRemaining", width: 2, height: 2) {
			state "timeRemaining", label: 'Timer: ${currentValue}'
		}

        valueTile("cfm", "device.cfm", width: 2, height: 2) {
			state "cfm", label: 'CFM: ${currentValue}'
		}

        standardTile("refresh", "device.refresh", width: 2, height: 2) {
 		   	state "refresh", label: 'Refresh', action: "refresh", backgroundColor: "#ffffff"
		}

        valueTile("outsideTemperature", "device.outsideTemperature", width: 2, height: 2) {
			state "outsideTemperature", label: 'Outside Temp: ${currentValue}'
		}
        valueTile("atticTemperature", "device.atticTemperature", width: 2, height: 2) {
			state "atticTemperature", label: 'Attic Temp: ${currentValue}'
		}
        valueTile("insideTemperature", "device.insideTemperature", width: 2, height: 2) {
			state "insideTemperature", label: 'Inside Temp: ${currentValue}'
		}

        multiAttributeTile(name:"controlPanel", type:"generic", width:6, height:4) {
    		tileAttribute("device.level", key: "PRIMARY_CONTROL") {
        		attributeState "default", label:'${currentValue}', backgroundColors:[
            		[value: 0, color: "#444444"],
            		[value: 1, color: "#444466"],
            		[value: 2, color: "#444477"],
            		[value: 3, color: "#444488"],
            		[value: 4, color: "#444499"],
            		[value: 5, color: "#4444bb"],
                    [value: 6, color: "#4444dd"],
            		[value: 7, color: "#4444ff"]
        		]
    		}

            tileAttribute("device.timeRemaining", key: "SECONDARY_CONTROL") {
        		attributeState "timer", label:'Add Time', action:"addTime"
			}


    		tileAttribute("device.level", key: "VALUE_CONTROL") {
        		attributeState "VALUE_UP", action: "levelUp"
        		attributeState "VALUE_DOWN", action: "levelDown"
    		}
		}

        main "switch"
        details(["controlPanel", "timer", "power", "cfm","insideTemperature","atticTemperature", "outsideTemperature", "refresh"])
    }

}

def initialize() {
    state.levelAtOff = 0
    refresh()
}

def refresh(){
    sendEvent(name: "cfm", value: 2341)
    sendEvent(name: "power", value: 389)
    sendEvent(name: "timeRemaining", value: 0)
    sendEvent(name: "insideTemperature", value: 75.1)
    sendEvent(name: "atticTemperature", value: 130)
    sendEvent(name: "outsideTemperature", value: 90)
}

def levelUp(){
	log.info("airscape: levelUp")
    def level = device.latestValue("level") as Integer ?: 0
    if(level==0){
    	sendEvent(name: "switch", value: "on")
    }
	if (level < 7) {
        level = level + 1
        setLevel(level)
		return getSendCodeAction(1)
	}
}

def levelDown(){
	log.info("airscape: levelDown")
    def level = device.latestValue("level") as Integer ?: 0
	if (level > 0) {
		level = level - 1
        setLevel(level)
     	if(level==0){
        	state.levelAtOff = 1
    		sendEvent(name: "switch", value: "off")
    	}
     	return getSendCodeAction(3)
	}
}

def addTime(){
    def time = device.latestValue("timeRemaining") as Integer ?: 0
    time++
    sendEvent(name: "timeRemaining", value: time)
	return getSendCodeAction(2)
}

def on() {
	log.debug("airscape: on")
    sendEvent(name: "level", value: 1)
    sendEvent(name: "switch", value: "on")
    return getSendCodeAction(4)
}

def off() {
	log.debug("airscape: off")
    state.levelAtOff = device.latestValue("levelAtOff") as Integer ?: 1
    sendEvent(name: "switch", value: "off")
    sendEvent(name: "level", value: 0)
    return getSendCodeAction(4)
}

def setLevel(speed) {
	log.debug "setLevel: ${speed}"
	sendEvent(name: "level", value: speed)
}

def parse(description) {
	log.debug "parse"
    def msg = parseLanMessage(description)

    def headersAsString = msg.header // => headers as a string
    def headerMap = msg.headers      // => headers as a Map
    def body = msg.body              // => request body as a string
    def status = msg.status          // => http status code of the response
    log.debug(body)
}

def poll(){
  log.debug("polling airscape fan")
}

// gets the address of the device
private getHostAddress() {
    return "${ip}:${port}"
}

private setDeviceNetworkId(ip,port){
  	def iphex = convertIPtoHex(ip)
  	def porthex = convertPortToHex(port)
  	device.deviceNetworkId = "$iphex:$porthex"
  	log.debug "Device Network Id set to ${iphex}:${porthex}"
}

private String convertIPtoHex(ipAddress) {
    String hex = ipAddress.tokenize( '.' ).collect {  String.format( '%02x', it.toInteger() ) }.join()
    return hex
}

private String convertPortToHex(port) {
	String hexport = port.toString().format( '%04x', port.toInteger() )
    return hexport
}

private getSendCodeAction(code){
  // where 1=fan speed up, 2=timer hour add, 3=fan speed down, 4=fan off
  log.debug("sending fan code ${code}")

  //setDeviceNetworkId(ip, port)
  def request = [
    method: "GET",
    path: "/fanspd.cgi" + (code!=null ? "?dir=$code" : ""),
    headers: [
        HOST: getHostAddress()
    ]
  ]

  return new physicalgraph.device.HubAction(request)
}
private def title() {
    return "AirScape Whole House Fan - Copyright © 2017 Timothy Overly"
}
