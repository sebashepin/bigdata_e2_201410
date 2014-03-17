
var fs = require('fs');

/*
 * GET home page.
 */

exports.index = function(req, res){
	var files = fs.readdirSync('./hadoop_data');
	console.log(files);
	res.render('index', { title: 'Consulta Grupo 5' },results:files);
};