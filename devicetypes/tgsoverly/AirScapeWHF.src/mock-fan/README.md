# Mock Fan

This is a simple node server that pretends to be an AirScape fan.  Might be useful for other developers.

## Usage

1. install nodejs if you don't have it and do the following this directory
1. `npm install`
1. `node index.js`

## Url

The server responds like a AirScape fan would do the the url: http://localhost:3000/fanspd.cgi.  It does the same operations as described in the [AirScape API](http://blog.airscapefans.com/archives/gen-2-controls-api).  The mock fan server responds with the XML'ish responses that an actual fan produces, it is suppose to be that way.
