/**
 */
metadata {
    definition (name:"AirScape WHF", namespace:"overly.me", author:"timothy@overly.me") {
        capability "Refresh"
        capability "Switch"
        capability "Switch Level"
        capability "Polling"

        attribute "levelAtOff", "number"

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
            		[value: 2, color: "#000044"],
            		[value: 3, color: "#000066"],
            		[value: 4, color: "#000088"],
            		[value: 5, color: "#0000aa"],
                    [value: 6, color: "#0000cc"],
            		[value: 7, color: "#0000ee"]
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

def levelUp(){
	log.info("airscape: levelUp")
    def level = device.latestValue("level") as Integer ?: 0
	if (level < 7) {
		level = level + 1
	}
	setLevel(level)
}

def levelDown(){
	log.info("airscape: levelDown")
    def level = device.latestValue("level") as Integer ?: 0
	if (level > 0) {
		level = level - 1
	}
	setLevel(level)
}

def on() {
	log.info("airscape: on")
}

def off() {
	log.info("airscape: off")
}

def setLevel(speed) {
	log.debug "setLevel: ${speed}"
	sendEvent(name: "level", value: speed)
}

private sendCode(code){
  // where 1=fan speed up, 2=timer hour add, 3=fan speed down, 4=fan off
  def result = new physicalgraph.device.HubAction(
    method: "GET",
    path: "/fanspd.cgi",
    headers: [
        HOST: "127.0.0.1"
    ],
    query: [dir: code]
  )
}
private def title() {
    return "AirScape Whole House Fan - Copyright © 2017 Timothy Overly"
}
