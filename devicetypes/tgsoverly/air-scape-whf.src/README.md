# AirScape Whole House Fan

AirScape makes a line of Whole House Fan's that are top of the line.  For the last several years they have included their controller 2.0 which allow for controlling of the fan through normal web calls.  This means that it is easy to have external systems update your fan.

AirScape has their own iOS and Android app's that allow your to control the fan from your phone.  This is great for most people, but there are a bunch of us out here that have smart home's and specifically the SmartThings hub to control our devices.

I wrote a "device type" for the SmartThings platform that transforms the AirScape web calls to something that SmartThings can understand.  This allows anyone with a SmartThings hub to use the fan in any Routine or SmartApp that can make use of the exposed "capabilities" of this device type.

The capabilities that have been implemented are:

1. Switch - Can trigger on/off
1. Switch Level - Can set the level up/down
1. Temperature Measurement - Can read the attic temperature

# Installation
