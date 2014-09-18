An Eclipse plugin client for [`ariatemplates/editor-backend`](https://github.com/ariatemplates/editor-backend).





# Current state

You can launch an Eclipse application with the plugin using __an external__ backend (see [procedure below](#setup)) and use it to edit files with `.tpl` and `.tml` extensions, using Aria Templates syntax.




# File system layout

- [`readme.md`](./readme.md): the main documentation file

Project management: 

- [`.gitignore`](./.gitignore): ignore rules for Git
- [`package.json`](./package.json): npm package definition, to be able to embed the backend server and use other npm/Node.js tools

Development resources: 

- [`gruntfile.js`](./gruntfile.js): Grunt entry point, to run development tasks
- [`.project/`](./.project/), [`.classpath`](./.classpath), [`.settings`](./.settings): Eclipse project definition/configuration
- [`dev-res/`](./dev-res/): folder containing some resources for development
- `node_modules/`: modules installed by npm from `package.json`

Build / deployment: 

- [`build/`](./build/): resources used to export the product as a package (different formats supported)
- `runtime/`: folder containing various resources needed for runtime
- `bin/`: folder containing the build code of the plugin

Code:

- [`src/`](./src/): the sources of the Eclipse plugin
- [`build.properties`](./build.properties), [`plugin.xml`](./plugin.xml), [`META-INF/`](./META-INF/): Eclipse plugin definition





# Versioning

To ignore:

- `bin`: generated content (from the sources)
- `node_modules`: generated from [`package.json`](./package.json)





# Installation

For the time being, there is no publicly available package.

This means you will need to follow the [contribution guide](#contribute) steps to install everything that is necessary to build yourself a package.





# Contribute

First of all: __READ CAREFULLY THE DOCS__.

Please have a look at the [documentation of the documentation](https://github.com/ariatemplates/editor-backend/blob/master/documentation.md) too (we follow the same rules as for the [backend project](https://github.com/ariatemplates/editor-backend)).



## Environment

This is the required environment — software and configuration — you will need to work on the project.

- [Eclipse IDE](http://www.eclipse.org) — 3.6 minimum / Preferably choose [Java EE bundle](http://www.eclipse.org/downloads/)
	- Plugins for development:
		- [PDE](http://www.eclipse.org/pde/)
	- Plugins dependencies — required for the runtime of the developed plugin:
		- [Google GSON](http://code.google.com/p/google-gson) from [Orbit repository](http://download.eclipse.org/tools/orbit/downloads)
		- In general, every other dependencies of the plugin should be installed if not already present. Please check the content of the file [`META-INF/MANIFEST.MF`](./META-INF/MANIFEST.MF), property `Require-Bundle`. You can find a lot of these dependencies inside the [Orbit repository](http://download.eclipse.org/tools/orbit/downloads).
- [Java Standard Edition Development Kit](http://www.oracle.com/technetwork/java/javase/downloads/index.html) — [6](http://www.oracle.com/technetwork/java/javase/downloads/java-archive-downloads-javase6-419409.html) minimum (here are [all versions](http://www.oracle.com/technetwork/java/javase/archive-139210.html) except latest)
- Node.js & npm, please refer to the [backend documentation](https://github.com/ariatemplates/editor-backend/#environment) which explains well how to do it
- [Git](http://git-scm.com) ([Windows package](http://git-scm.com/download/win))

Tested on Microsoft Windows 7 Enterprise 64-bit SP1.



## Setup

After [cloning](http://git-scm.com/docs/git-clone) the [repository](https://github.com/ariatemplates/editor-frontend-eclipse.git)

```bash
git clone https://github.com/ariatemplates/editor-frontend-eclipse.git
```

you will have to do some setup.

These are the things to do:

- [install the development tools and dependencies](#tools--dependencies), using npm
- [import the Eclipse project](#eclipse-project) into your workspace to work on the plugin
- optionally, if you want to use an external installation of the backend server, [install it globally](#external-backend)

### Tools & dependencies

We are mainly using Node.js tools distributed through npm, so run the following command in this folder:

```bash
npm install
```

This will install a local version of the backend server inside the project, which can then be used by the plugin at runtime. This local installation is also required to export the plugin with an embedded backend.

This will also install [Grunt](http://gruntjs.com/) and grunt tasks, used to automate some development tasks. They are described below in this document.

### Eclipse project

Import the Eclipse project into your workspace:

1. menu: `File` ⇨ `Import...`
1. choose item `Existing Projects into Workspace` (under category `General`)
1. in `Select root directory` input field put the path to this current folder
1. in `Projects` list, check `AT Editor`
1. you are free to set some other settings, then validate by clicking the button `Finish`


### External backend

To use an external version of the backend, run this command:

```bash
npm install -g git+https://github.com/ariatemplates/editor-backend#version/x.x.x # Replace x.x.x by the version you want
```

Please check [`version/` branches](https://github.com/ariatemplates/editor-backend/branches/yours) to pick the proper one.


## Build

Please refer to the detailed documentation in [`build`](./build).



## Try

To launch the Eclipse application instance including the plugin: 

<!-- The step below is commented since it will require the backend to accept more easily commend line arguments, so that he port number can be changed to 50000 (current port used in the code) -->
<!-- 1. Optional: you can launch an independent external backend: run command ```editor-backend``` from anywhere and keep the shell running -->
1. open the Eclipse Plugin project
1. select the project and open the main menu `Run` or use the contextual menu of the project
	- select `Run As` to run it normally
	- select `Debug As` to run it in debug mode
1. choose `Eclipse Application`

You are free to configure more thoroughly this _launch configuration_, to change the associated workspace location, disable some plugins, etc.

## Development

Please refer to the subfolders of the project for details about corresponding modules specific development: every folder contains its own documentation and is likely to give some paths for contribution.

Also, please refer to the [GitHub issues](https://github.com/ariatemplates/editor-frontend-eclipse/issues), which constitute a kind of backlog.
