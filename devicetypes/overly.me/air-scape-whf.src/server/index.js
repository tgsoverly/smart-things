/*
http://controllerURL/fanspd.cgi?dir=|1|2|3|4|
where 1=fan speed up, 2=timer hour add, 3=fan speed down, 4=fan off

For example, if you want to turn OFF a fan at IP 192.168.0.20 the command would be:

http://192.168.0.20/fanspd.cgi?dir=4

fanspd<fanspd>0</fanspd>
doorinprocess<doorinprocess>0</doorinprocess>
timeremaining<timeremaining>0</timeremaining>
macaddr<macaddr>60:CB:FB:99:99:0A</macaddr>
ipaddr<ipaddr>192.168.0.20</ipaddr>
model<model>2.5eWHF</model>
softver: <softver>2.14.1</softver>
interlock1:<interlock1>0</interlock1>
interlock2: <interlock2>0</interlock2>
cfm: <cfm>0</cfm>
power: <power>0</power>
inside:<house_temp>72</house_temp>
<DNS1>192.168.0.1</DNS1>
attic: <attic_temp>92</attic_temp>
OA: <oa_temp>81</oa_temp>
server response: <server_response>Posted
OK<br/></server_response>
DIP Switches: <DIPS>00000</DIPS>
Remote Switch:<switch2>1111</switch2>
Setpoint:<Setpoint>0</Setpoint>


*/


var express = require('express')
var app = express()
var xml = require('xml');

var currentSpeed = 0;
var currentTimer = 0;

app.get('/fanspd.cgi', function (req, res) {
  if(req.query.dir){
    console.log("dir "+req.query.dir);
    switch (req.query.dir) {
      case "1":
        if(currentSpeed<7){
          currentSpeed++;
        }
        break;
      case "2":
        currentTimer+=60;
        console.error("timer increased");
        break;
      case "3":
        if(currentSpeed>0){
          currentSpeed--;
        }
        break;
      case "4":
        currentSpeed = 0;
        break;
      default:
        console.error("not accepted");
    }
  }else{
    console.log("status return");
  }
  if(currentSpeed==0){
    currentTimer = 0;
  }
  var body = [
    {fanspd:currentSpeed}, 
    {timeremaining:currentTimer},
    {cfm: 2345.1},
    {power: 345.3},
    {attic_temp:123.3},
    {oa_temp:72.3},
    {house_temp:85.3}
  ];
  res.set('Content-Type', 'text/xml');
  res.send(xml({reponse: body}));
})

app.listen(3000, function () {
  console.log('Example app listening on port 3000!')
})
