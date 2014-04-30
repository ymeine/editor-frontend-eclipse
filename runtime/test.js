console.log('Hello Java!');

require('fs').writeFileSync(__dirname + '/output', 'I created the file now: ' + new Date());
