/**
 */

preferences {
    input("confIpAddr", "string", title:"AirScape IP Address",
        required:true, displayDuringSetup:true)
}

metadata {
    definition (name:"AirScape Fan", namespace:"overly.me", author:"timothy@overly.me") {
        capability "Switch"
    }

    simulator {
        status "On"         : "simulator:true, state:'off'"
        status "Off"        : "simulator:true, state:'on'"
    }
}

def installed() {
    log.info title()
}

def on() {
}

def off() {
}

private def title() {
    return "AirScape Whole House Fan - Copyright © 2017 Timothy Overly"
}
