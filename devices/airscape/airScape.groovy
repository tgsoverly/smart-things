/**
 */

preferences {
    input("confIpAddr", "string", title:"AirScape IP Address",
        required:true, displayDuringSetup:true)
}

metadata {
    definition (name:"AirScape Fan", namespace:"me.overly", author:"timothy@overly.me") {
        capability "Switch"
    }

    simulator {
        status "On"         : "simulator:true, state:'off'"
        status "Off"        : "simulator:true, state:'on'"
    }
}

def installed() {
    log.info title()
    sendEvent([name:'status', value:'off', displayed:false])
}

def on() {
}

def off() {
}

private def title() {
    return "AirScape Fan Device Copyright © 2017 Timothy Overly"
}
