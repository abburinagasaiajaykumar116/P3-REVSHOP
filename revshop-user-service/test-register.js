const http = require('http');

const data = JSON.stringify({
  name: "rocky",
  email: "rockytest123@gmail.com",
  password: "rocky123",
  role: "BUYER",
  businessDetails: null,
  securityQuestion: "your fav snack",
  securityAnswer: "kfc"
});

const options = {
  hostname: 'localhost',
  port: 8080, // Calling via API Gateway
  path: '/auth/register',
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Content-Length': data.length
  }
};

const req = http.request(options, (res) => {
  console.log(`STATUS: ${res.statusCode}`);
  console.log(`HEADERS: ${JSON.stringify(res.headers)}`);
  res.setEncoding('utf8');
  res.on('data', (chunk) => {
    console.log(`BODY: ${chunk}`);
  });
  res.on('end', () => {
    console.log('No more data in response.');
  });
});

req.on('error', (e) => {
  console.error(`problem with request: ${e.message}`);
});

req.write(data);
req.end();
