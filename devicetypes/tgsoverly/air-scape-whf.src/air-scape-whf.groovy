/**
* AirScape Whole House Fan Device
*
* Control the speed of an installed fan.  Monitor the temperature from its three sensors and it ft^/min of air flow.
*
* Questions:
* 1. Timer have an upper limit?
*
* Notes:
* 1. Command object have to return hub action
* 1. Your hub must be selected in the device
 */
import groovy.util.XmlSlurper

preferences {
        input("ip", "string", title:"IP", description: "IP of Fan", required: true, displayDuringSetup: true)
        input("port", "string", title:"Port", description: "Port of Fan", defaultValue: "80" , required: false, displayDuringSetup: true)
}

metadata {
    definition (name:"Air Scape WHF", namespace:"tgsoverly", author:"timothy@overly.me") {
        capability "Refresh"
        capability "Polling"

        capability "Switch"
        capability "Switch Level"
        capability "Power Meter"

        //Basic measurment is the attic temperature, that is more useful because outside and and house temps are common elsewhere.
        capability "Temperature Measurement"

        attribute "timeRemaining", "number"
        attribute "outsideTemperature", "number"
        attribute "insideTemperature", "number"
        attribute "cfm", "number"

		//Note: naming this 'status' seemed to interfere with some hidden smartthings variable
		attribute "statusOfUpdate", "enum", ["updating", "ready"]

        command "levelUp"
		command "levelDown"
        command "addTime"
        command "maximum"

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
 		   	state "refresh", label:"Refresh", action: "refresh", backgroundColor: "#ffffff", icon: "st.secondary.refresh-icon"
		}

        valueTile("outsideTemperature", "device.outsideTemperature", width: 2, height: 2) {
			state "outsideTemperature", label: 'Outside: ${currentValue}'
		}
        valueTile("atticTemperature", "device.temperature", width: 2, height: 2) {
			state "temperature", label: 'Attic: ${currentValue}'
		}
        valueTile("insideTemperature", "device.insideTemperature", width: 2, height: 2) {
			state "insideTemperature", label: 'Inside: ${currentValue}'
		}

        standardTile("maximum", "device.level", width: 2, height: 2) {
 		   	state "level", label:"Max", action: "maximum", backgroundColor: "#ffffff", icon: "st.thermostat.thermostat-up"
		}

        multiAttributeTile(name:"controlPanel", type:"generic", width:6, height:4) {
    		tileAttribute("device.level", key: "PRIMARY_CONTROL") {
        		attributeState "default", label:'${currentValue}'
    		}

            tileAttribute("device.statusOfUpdate", key: "SECONDARY_CONTROL") {
        		attributeState "ready", label:'+ Timer', action:"addTime"
                attributeState "updating", label:'Updating...'
			}


    		tileAttribute("device.level", key: "VALUE_CONTROL") {
        		attributeState "VALUE_UP", action: "levelUp"
        		attributeState "VALUE_DOWN", action: "levelDown"
    		}
		}

        main "switch"
        details(["controlPanel", "timer", "power", "cfm","insideTemperature","atticTemperature", "outsideTemperature", "refresh", "maximum", "switch"])
    }

}

def installed() {
    log.debug "installed"
    initialize()
}

def updated() {
    log.debug "updated"
    initialize()
}

def initialize() {
    log.debug "initialize"
    state.levelAtOff = 0
    def level = device.latestValue("level") as Integer ?: 0
    log.debug "setting target level to ${level}"
    state.targetLevel = level
    refresh()
}

def refresh(){
  log.debug "refresh"
  setDeviceNetworkId()
  return getSendCodeAction()
}

def poll() {
  log.debug("airscape: polling status")
  return getSendCodeAction()
}

def maximum(){
	log.debug("airscape: maximum")

  sendEvent(name: "statusOfUpdate", value: "updating")

  return setLevel(100)
}

public setLevel(int targetLevel){
  targetLevel = Math.min(targetLevel, 100)
  targetLevel = Math.max(targetLevel, 0)
  state.targetLevel = targetLevel
  return getNextStep()
}

def getNextStep() {
  def level = device.latestValue("level") as Integer ?: 0
  log.debug "getNextStep level ${level} target ${state.targetLevel}"

  if (state.targetLevel == null) {
    state.targetLevel = level
  }

  def roundedLevel = Math.ceil(level / 10.0)
  def roundedTarget = Math.ceil(state.targetLevel / 10.0)
  log.debug "getNextStep roundedLevel ${roundedLevel} roundedTarget ${roundedTarget}"
  if (roundedTarget == roundedLevel) {
    state.targetLevel = level
    return []
  }

  def code = roundedTarget > roundedLevel ? "1" : "3"
  return getSendCodeAction(code)
}

def levelUp(){
  log.info("airscape: levelUp ${device.latestValue('level')}")
  def level = device.latestValue("level") as Integer ?: 0
  return setLevel(level + 10)
}

def levelDown(){
  log.info("airscape: levelDown")
  def level = device.latestValue("level") as Integer ?: 0
  return setLevel(level - 10)
}

def addTime(){
	log.debug("airscape: addTime")
    sendEvent(name: "statusOfUpdate", value: "updating")
	return getSendCodeAction(2)
}

def on() {
	log.debug("airscape: on")
    sendEvent(name: "statusOfUpdate", value: "updating")
    state.targetLevel = 10
    return getSendCodeAction(1)
}

def off() {
	log.debug("airscape: off")
    sendEvent(name: "statusOfUpdate", value: "updating")
    state.levelAtOff = device.latestValue("levelAtOff") as Integer ?: 1
    state.targetLevel = 0
    return getSendCodeAction(4)
}

def parse(response) {
	log.debug "airscape: parse current level ${device.latestValue('level')}";
   	def msg = parseLanMessage(response)

	def events = []

    log.debug "status ${msg.status}"
	if (msg.status==200) {
      def body = msg.body
      def xml = new XmlSlurper().parseText(cleanResponse(msg.body))
      log.debug "response level ${xml.fanspd}"
      def fanPercent = xml.fanspd.toInteger() * 10
      events.add createEvent(name: "cfm", value: xml.cfm)
      events.add createEvent(name: "power", value: xml.power)
      events.add createEvent(name: "timeRemaining", value: xml.timeremaining)
      events.add createEvent(name: "insideTemperature", value: (xml.house_temp == -99 ? "N/A" : xml.house_temp))
      events.add createEvent(name: "temperature", value: xml.attic_temp)
      events.add createEvent(name: "outsideTemperature", value: (xml.oa_temp == -99 ? "N/A" : xml.oa_temp))
      def level = device.latestValue("level") as Integer ?: 0
      if (level == state.targetLevel) {
        state.targetLevel = fanPercent
      }
      events.add createEvent(name: "level", value: fanPercent)
      if (xml.fanspd.toInteger() > 0) {
	    events.add createEvent(name: "switch", value: "on")
      } else {
        events.add createEvent(name: "switch", value: "off")
      }
    } else {
    	log.error("error getting response from fan $msg")
    }

	//set the updating indicator back to add time
	events.add createEvent(name:"statusOfUpdate", value: "ready")
    events.add getNextStep()
    return events
}

// gets the address of the device
private getHostAddress() {
    return "${ip}:${port}"
}

public cleanResponse(String body){
  def regex = ~/(.*)<[^\/]/
  body = body.replaceAll(regex){all, prefix ->
    return all.replace(prefix, "")
  }

  regex = ~/\<server_response\>(.|\n|\r)*<\/server_response>\n/
  body = body.replaceAll(regex, "")

  return "<response>${body}</response>"
}

private getSendCodeAction(code=null){
  // where 1=fan speed up, 2=timer hour add, 3=fan speed down, 4=fan off
  log.debug("sending fan code ${code}")

  if (ip == null) {
      return null;
  }

  def request = [
    method: "GET",
    path: fanPath + (code!=null ? "?dir=$code" : ""),
    headers: [
        HOST: getHostAddress()
    ]
  ]

  return new physicalgraph.device.HubAction(request)
}

/**
* This is required.  If the device network id doesn't match the ip address of the fan, then the response from the fan won't be
* passed back to the device.  The easiest is just to set it to alway match the ip in the preferences.
*/
private setDeviceNetworkId() {
    log.debug "Trying to set device network ID with ${ip}:${port}"
    if (ip == null) {
        return null;
    }
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

private String getFanPath(){
  return "/fanspd.cgi"
}

private def title() {
    return "AirScape Whole House Fan - Copyright © 2017 Timothy Overly"
}
