An Eclipse plugin client for [`ariatemplates/editor-backend`](https://github.com/ariatemplates/editor-backend).

# Current state

You can launch an Eclipse application with the plugin using __an external__ backend (see [procedure below](#setup)) and use it to edit files with `.tpl` and `.tml` extensions, using Aria Templates syntax.

# File system layout

* [`README.md`](./README.md): this current file
* [`.gitignore`](./.gitignore): Git related file
* [`package.json`](./package.json): npm package definition, to be able to embed the backend server and use other npm/Node.js tools
* `node_modules`: contains the npm modules listed in `package.json`
* [`statics`](./statics): folder containing some resources for development
* [`Gruntfile.js`](./Gruntfile.js): Grunt entry point, to run development tasks
* [`build`](./build): resources to export the product as a package (different formats supported)
* `bin`: folder containing the build of the plugin


Code:

* [`src`](./src): the sources of the Eclipse plugin
* [`build.properties`](./build.properties), [`plugin.xml`](./plugin.xml), [`META-INF`](./META-INF): Eclipse plugin definition
* [`.project`](./.project), [`.classpath`](./.classpath), [`.settings`](./.settings): Eclipse project definition/configuration

# Versioning

To ignore:

* `bin`: generated content (from the sources)
* `node_modules`: generated from `package.json`

To version: _everything else_.





# Installation

For the time being, there is no publicly available package.

This means you will need to follow the [contribution guide](#contribute) steps to install everything that is necessary to build yourself a package.





# Contribute

First of all: __READ CAREFULLY THE DOCS__.

Please have a look at the [documentation of the documentation](https://github.com/ariatemplates/editor-backend/blob/master/documentation.md) too (we follow the same rules as for the [backend project](https://github.com/ariatemplates/editor-backend)).



## Environment

This is the required environment — software and configuration — you will need to work on the project.

* [Eclipse IDE](http://www.eclipse.org) — 3.6 minimum / Preferably choose [Java EE bundle](http://www.eclipse.org/downloads/packages/eclipse-ide-java-ee-developers/keplersr1)
	* Plugin: [PDE](http://www.eclipse.org/pde/) — this is the tool used to develop Eclipse plugins, features and so on.
	* Bundle: [Google GSON](http://code.google.com/p/google-gson) from [Orbit repository](http://download.eclipse.org/tools/orbit/downloads) — this is a dependency of the project
	* In general, every other dependencies of the plugin should be installed if not already present. Please check the content of the file [`META-INF/MANIFEST.MF`](./META-INF/MANIFEST.MF), property `Require-Bundle`.
* [Java SE](http://www.oracle.com/technetwork/java/javase/downloads/index.html) — 6 minimum
* Node.js & npm, please refer to the [backend documentation](https://github.com/ariatemplates/editor-backend/#environment) which explains well how to do it
* [Git](http://git-scm.com) ([Windows package](http://git-scm.com/download/win))

Tested on Microsoft Windows 7 Enterprise 64-bit SP1.



## Setup

After [cloning](http://git-scm.com/docs/git-clone) the [repository](https://github.com/ariatemplates/editor-frontend-eclipse.git)

```bash
git clone https://github.com/ariatemplates/editor-frontend-eclipse.git
```

you will have to do some setup.

These are the things to do:

* [install the development tools and dependencies](#tools--dependencies), using npm
* [import the Eclipse project](#eclipse-project) into your workspace to work on the plugin
* optionally, if you want to use an external installation of the backend server, [install it globally](#external-backend)

### Tools & dependencies

We are mainly using npm/Node.js tools, so run the following command in this folder:

```bash
npm install
```

This will install a local version of the backend server inside the project, which can then be used by the plugin at runtime. This local installation is also required to export the plugin with an embedded backend.

This will also install [Grunt](http://gruntjs.com/) and grunt tasks, used to automate some development tasks. Those tasks are described below in this document.

### Eclipse project

Import the Eclipse project into your workspace:

1. Menu: `File` ⇨ `Import...`
1. Choose item `Existing Projects into Workspace` (under category `General`)
1. In `Select root directory` input field put the path to this current folder
1. In `Projects` list, check `AT Editor`
1. You are free to set some other settings, then validate by clicking the button `Finish`


### External backend

To use an external version of the backend, run this command:

```bash
npm install -g git+https://github.com/ariatemplates/editor-backend#version/x.x.x # Replace x.x.x by the version you want
```



## Build

Please refer to the detailed documentation in [`build`](./build).



## Try

__For now the plugin works only with an [external backend](#external-backend)__

1. Launch the external backend : run command ```editor-backend``` from anywhere and keep the shell running
1. Launch the Eclipse application
	1. Open the Eclipse Plugin project
	1. Launch the project as an Eclipse application
		1. Select the project and open the main menu `Run` or use the contextual menu of the project
			* select `Run As` to run it normally
			* select `Debug As` to run it in debug mode
		1. Choose `Eclipse Application`



## Development

Please refer to the subfolders of the project for details about corresponding modules specific development: every folder contains its own documentation and is likely to give some paths for contribution.

Also, please refer to the [GitHub issue](https://github.com/ariatemplates/editor-frontend-eclipse/issues), which constitute a kind of backlog.
