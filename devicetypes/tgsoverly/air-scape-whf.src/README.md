# AirScape Whole House Fan

AirScape makes a line of Whole House Fan's that are top of the line.  For the last several years they have included their controller 2.0 which allow for controlling of the fan through normal web calls.  This means that it is easy to have external systems update your fan.

AirScape has their own iOS and Android app's that allow your to control the fan from your phone.  This is great for most people, but there are a bunch of us out here that have smart home's and specifically the SmartThings hub to control our devices.

I wrote a "device type" for the SmartThings platform that transforms the AirScape web calls into something that SmartThings can understand.  This allows anyone with a SmartThings hub to use the fan either manually from the SmartThings app or automatically with any Routine or SmartApp that can make use of the exposed "capabilities" of this device type.

The capabilities that have been implemented are in this device type are:

1. Switch - Can trigger on/off.
1. Switch Level - Can set the level up/down.
1. Temperature Measurement - Can read the attic temperature.
1. Power Meter - Can tell you how much power the fan is using based on lookups.

# Installation

1. Log into your SmartThings online interface: https://graph.api.smartthings.com, this is the same that you use to log into the app on your phone/tablet.
1. Create a new Device Handler.  This is a template on how to control AirScape fans.
  1. Go to this (page)[https://raw.githubusercontent.com/tgsoverly/smart-things/master/devicetypes/tgsoverly/air-scape-whf.src/air-scape-whf.groovy]. Select all text (Ctrl-A or Cmd-A) and copy it (Ctrl-C or Cmd-A).
  1. Click the link "My Device Handlers" at the top of the page.
  1. Click the green "Create Device Handler" on the right side of the page.
  1. Click the "From Code" tab and paste your clipboard (Ctrl-V or Cmd-V).
  1. Click the "Create" button at the bottom.
1. Create at least one actual AirScape Devices.  You may have more than one fan.
  1. Click the link "My Devices" at the top of the page.
  1. Click the green "New Device" on the right side of the page.
  1. Fill in the form.  Give your device a name, the network id is required but can be anything.  Make sure you choose both a location and which hub is on the same network as the fan.
