
var fs = require('fs');
var url = require('url');

/*
 * GET graph display
 */
exports.graph = function(req, res){
	var query = req.query;
	if(typeof query.resFile != 'undefined'){
		var resFileName = query.resFile;
		res.render('results', { title: 'Resultado Grupo 5','resFileName':resFileName, 'data_url':'/hadoop_data/'+resFileName+'.json'});
	}
	else{
		res.redirect('/');
	}

};