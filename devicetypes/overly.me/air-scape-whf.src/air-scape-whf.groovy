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

        command "levelUp"
		command "levelDown"
    }

	tiles(scale: 2) {

        standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: true) {
 		   	state "off", label: '${currentValue}', action: "switch.on", icon: "st.Lighting.light24", backgroundColor: "#ffffff"
    		state "on", label: '${currentValue}', action: "switch.off", icon: "st.Lighting.light24", backgroundColor: "#79b821"
		}

        multiAttributeTile(name:"controlPanel", type:"generic", width:6, height:4) {
    		tileAttribute("device.level", key: "PRIMARY_CONTROL") {
        		attributeState "default", label:'${currentValue}', backgroundColors:[
            		[value: 0, color: "#000000"],
            		[value: 1, color: "#000022"],
            		[value: 2, color: "#111144"],
            		[value: 3, color: "#111166"],
            		[value: 4, color: "#222288"],
            		[value: 5, color: "#2222aa"],
                    [value: 6, color: "#3333cc"],
            		[value: 7, color: "#3333ee"]
        		]
    		}

    		tileAttribute("device.level", key: "VALUE_CONTROL") {
        		attributeState "VALUE_UP", action: "levelUp"
        		attributeState "VALUE_DOWN", action: "levelDown"
    		}
		}

        main "switch"
        details(["switch", "controlPanel"])
    }

}

def initialize() {
    state.levelAtOff = 0
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
