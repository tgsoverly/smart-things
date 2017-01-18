/*
http://controllerURL/fanspd.cgi?dir=|1|2|3|4|
where 1=fan speed up, 2=timer hour add, 3=fan speed down, 4=fan off

For example, if you want to turn OFF a fan at IP 192.168.0.20 the command would be:

http://192.168.0.20/fanspd.cgi?dir=4
*/


var express = require('express')
var app = express()

app.get('/fanspd.cgi', function (req, res) {
  if(req.query.dir){
    res.send(req.query.dir);
  }else{
    res.send('Hello World!');
  }
})

app.listen(3000, function () {
  console.log('Example app listening on port 3000!')
})
