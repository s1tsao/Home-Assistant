/*var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);

app.get('/', function(req, res){
  res.sendFile(__dirname + '/index.html');
});

io.on('connection', function(socket){
  console.log('a user connected');
});

http.listen(3000, function(){
  console.log('listening on *:3000');
});
*/
var wol = require('node-wol');
const io = require('socket.io')();
device_map = {"computer":'ac:37:43:dc:20:f1',"phone":'ac:37:43:dc:20:f1'};
io.on('connection', (client) => {
  client.on('subscribeToTimer', (interval) => {
    console.log('client is subscribing to timer with interval ', interval);
    setInterval(() => {
      client.emit('timer', new Date());
    }, interval);
  });
  client.on('wol', function(device){
    //wol.wake('ac:37:43:dc:20:f1');
    if(!(device in device_map)){
      console.log("device:"+device+" not configured");
    }
    else{
      wol.wake(device_map[device], function(error) {
      if(error) {
        // handle error 
        console.log("error waking up device");
        return;
      }
      });
      console.log("waking:"+device+":"+device_map[device]);
      //var magicPacket = wol.createMagicPacket('20:DE:20:DE:20:DE');
    }

  })
  client.on('config', function(device, mac_address){
    device_map[device]=mac_address;
    console.log("device:"+mac_address);
  });
});

const port = 8000;
io.listen(port);
console.log('listening on port ', port);