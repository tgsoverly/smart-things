/*
http://controllerURL/fanspd.cgi?dir=|1|2|3|4|
where 1=fan speed up, 2=timer hour add, 3=fan speed down, 4=fan off

For example, if you want to turn OFF a fan at IP 192.168.0.20 the command would be:

http://192.168.0.20/fanspd.cgi?dir=4
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
        currentTimer++
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
  var body = [{speed:currentSpeed}, {timer:currentTimer}, {temperature: 1000}];
  res.set('Content-Type', 'text/xml');
  res.send(xml({reponse: body}));
})

app.listen(3000, function () {
  console.log('Example app listening on port 3000!')
})
