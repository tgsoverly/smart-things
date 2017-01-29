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

You will need:

1. Your SmartThings username and password to create this device, have those handy.
1. The IP address of your AirScape fan.  The easiest way is generally to get that information from your router.

If you are experienced in installing devices, the code is in step 1 below.  If you are new to this process, it involves two main steps.  First, creating what is called a "device handler", which is a blueprint on how to interact with a type of device. Second, creating at least one "device", which is actual instance of that type.

1. Go to this [page](https://raw.githubusercontent.com/tgsoverly/smart-things/master/devicetypes/tgsoverly/air-scape-whf.src/air-scape-whf.groovy). Select all text (Ctrl-A or Cmd-A) and copy it (Ctrl-C or Cmd-A).  
1. Go to the [create device handler page](https://graph.api.smartthings.com/ide/device/create) and paste the code that you copied in the "From Code" tab
1. Click the "Create" button at the bottom.
1. Go to the [create device page](https://graph.api.smartthings.com/device/create) and fill in the needed information
1. Fill in the form.
  1. Name: Anything you want
  1. Device Network Id: Put anything in here, it will be updated by the device
  1. Type: AirScape WHF (it will be at the bottom)
  1. Version: Published
  1. Location: The location that contains the fan. (you probably only have one)
  1. Hub: The hub that has network access to the fan. (you probably only have one)
1. Click the "Create" button at the bottom.
1. You will now have an AirScape Device on your mobile device.  The final step is to open that device, and click the settings button (small gear in the upper right).  Then input your IP address to the fan here.  You shouldn't need to change the port from 80.  Then click done and click the "Refesh" button to get your current fan status.

# Todos

1. Maybe have the device do a port scan for the fan.
