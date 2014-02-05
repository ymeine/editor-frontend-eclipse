module.exports = function(grunt) {

	// ------------------------------ Definition of tasks using external modules
	
	var node_url_prefix = "http://nodejs.org/dist/v";
	var node_url_suffix = "/node.exe";
	
	var node_version = "0.10.31";
	var node_architecture = "32";
	
	var node_url = node_url_prefix + node_version + (node_architecture === "64" ? "/x64" : "") + node_url_suffix;

	grunt.initConfig({
		pkg: grunt.file.readJSON('package.json'),
		clean: {
			'bin': ['bin/'],

			'site': [
				'build/site/artifacts.jar',
				'build/site/content.jar',
				'build/site/logs.zip',
				'build/site/features/',
				'build/site/plugins/'
			],

			'package': 'editor-plugin.zip',

			'external': [
				'runtime/node.exe'
			]
		},
		curl: {
			node: {
				src: node_url,
      			dest: 'runtime/node.exe'
			},
		},

		execute: {
			'backend-build': {
				src: ['node_modules/editor-backend/scripts/build.js']
			}
		},

		compress: {
			'site': {
				options: {
					archive: 'editor-plugin.zip'
				},
				files: [
					{expand: true, cwd: 'build/site/', src: ['site.xml'], dest: '/'},
					{expand: true, cwd: 'build/site/features/', src: ['**'], dest: '/features'},
					{expand: true, cwd: 'build/site/plugins/', src: ['**'], dest: '/plugins'}
				]
			}
		}
	});

	grunt.loadNpmTasks('grunt-contrib-clean');
	grunt.loadNpmTasks('grunt-curl');
	grunt.loadNpmTasks('grunt-execute');
	grunt.loadNpmTasks('grunt-contrib-compress');



	// ------------------------------------------------- Custom tasks definition

	grunt.registerTask('configure-backend-port', 'Applies custom configurations', function(port) {
		// ------------------------------------------ Input arguments processing

		port = parseInt(port, 10);

		// ------------------------------------------------ Options modification

		var default_options = require("editor-backend/app/options");
		default_options.network.ports.prefered = port;
		var output = JSON.stringify(default_options);

		// ------------------------------------------------------- Result output

		var fs = require('fs');
		var pathlib = require('path');

		var basepath = pathlib.join(__dirname, "node_modules", "editor-backend", "app")

		// Remove the initial "options.js" file it it still exists (first time execution or after any npm install/update)

		var initialFile = pathlib.join(basepath, "options.js");
		if (fs.existsSync(initialFile)) {
			fs.unlink(initialFile);
		}

		// Writes new options in "options.json"

		fs.writeFileSync(pathlib.join(basepath, "options.json"), output);
	});



	// ----------------------------------------------- Compound tasks definition

	grunt.registerTask('prepare-site', ['curl:node', 'execute:backend-build', 'configure-backend-port:50000', 'clean:site']);
	// TODO Also remove the files as said in the doc
	grunt.registerTask('package-site', ['compress:site', 'clean:site'])

	// grunt.registerTask('default', ['clean', 'export']);
};
