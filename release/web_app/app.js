
/**
 * Module dependencies.
 */

var express = require('express');
var routes = require('./routes');
var results = require('./routes/results');
var http = require('http');
var path = require('path');

var app = express();

// all environments
app.set('port', 8080);
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');
app.use(express.favicon());
app.use(express.logger('dev'));
app.use(express.json());
app.use(express.urlencoded());
app.use(express.methodOverride());
app.use(app.router);
app.use(require('stylus').middleware(path.join(__dirname, 'public')));
app.use(express.static(path.join(__dirname, 'public')));
app.use('/hadoop_data',express.static(path.join(__dirname, 'hadoop_data')));
app.use(function(req,res){
	res.redirect('/');
});

// development only
if ('development' == app.get('env')) {
	app.use(express.errorHandler());
}

app.get('/', routes.index);
app.get('/results.html', results.graph);

http.createServer(app).listen(app.get('port'), function(){
	console.log('Servidor Grupo 5 esperando peticiones en puerto ' + app.get('port'));
});
