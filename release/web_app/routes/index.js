
var fs = require('fs');

/*
 * GET home page.
 */

exports.index = function(req, res){
	var files = fs.readdirSync('./hadoop_data');
	var results = [];
	var numRes = 0;
	for(var i = 0 ; i < files.length ; i++){
		var currentFile = files[i].toLowerCase();
		var jsonPos = currentFile.lastIndexOf('.json')
		if(jsonPos > 1)
			results[numRes++] = currentFile.slice(0,jsonPos);
	}
	res.render('index', { 'title': 'Consulta Grupo 5' , 'results':results});
};