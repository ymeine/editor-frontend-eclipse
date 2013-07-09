This project aims at providing a solution for source code edition services, decoupled from any user interface, with an effort of abstraction of the underlying models.

__This is a work in progress and only at a state of proof of concept.__

Thus this is for now concretely applied to specific things:

* Edition services modules (called _modes_):
	* JavaScript
	* HTML
	* [Aria Templates](http://ariatemplates.com), using the two above
* Clients implementations:
	* [Eclipse IDE](http://eclipse.org/)

# Introduction

Please read the `introduction.md` file if you never did it and don't know what the project is all about.

Please see the `documentation.md` file __before reading or WRITING any documentation__. This helps understanding the documentation, and is required to maintain it consistent while adding content.

# File system layout

* `.gitignore`: Git related file
* `bin`: folder containing the build, that contains both the Eclipse plugin and the backend for now

## Documentation

* `README.md`: this current file
* `introduction.md`: an introduction to the project
* `documentation.md`: a documentation about the documentation in this project

## Backend code

* `resources`: the sources of the backend

## Eclipse code

* `src`: the sources of the Eclipse plugin
* `build.properties`, `plugin.xml`, `META-INF`: files and folders contributing to the Eclipse plugin definition
* `.project`, `.classpath`, `.settings`: files related to the Eclipse project configuration

# Versioning

To version:

* Documentation
* `.gitignore`
* `build.properties`, `plugin.xml`, `META-INF`
* `src`
* `resources`

What might be versioned (should be reproducible but might differ between environments - so versioning could pollute more than help):

* `.project`, `.classpath`, `.settings`

To ignore:

* `bin`: generated content (from the sources)

# Documentation

As mentioned, the goal is to implement a generic solution to handle source code edition, whatever the language, whatever the UI used behind (i.e. the tool(s)).

## Architecture

We call the tools used to actually edit source code: ___frontends___. They provide the (G)UI.

We call the application serving source edition features (processing): the ___backend___.

A frontend is a client of this backend (server application), and they communicate through standard means.

Here is a quick description of the stack:

* __backend__: a Node.js based application, providing services used by editors and IDEs
* __API__: a classical programming interface, used by the JSON-RPC layer (which is the backend-side end-point of the _communication interface_)
* __communication interface__: [JSON](http://en.wikipedia.org/wiki/JSON)-[RPC](http://en.wikipedia.org/wiki/Remote_procedure_call) through [HTTP](http://en.wikipedia.org/wiki/Hypertext_Transfer_Protocol) (default listening port: 3000)
* __frontend__: any IDE or Editor with extension capability, using the backend through the communication interface

This project aims at providing everything but the last part: a frontend is a consumer of the project.

However, as this is a work in progress, and for some prioritary requirements, everything is integrated into a __single__ frontend project: an Eclipse plugin.

Later on, we could consider doing it for [Sublime Text](http://www.sublimetext.com/), [Notepad++](http://notepad-plus-plus.org/), [Cloud9](https://c9.io/), ...


# Contribute

I would first give an advice to apply everywhere: __READ CAREFULLY THE DOCS__.

## Environment

* [Node.js](http://nodejs.org/download/) - tested with latest version ([0.10.12](http://nodejs.org/dist/v0.10.12/node.exe) at the time of writing)
* Eclipse IDE - tested with latest version (Kepler at the time of writing)
	* [Java EE bundle](http://www.eclipse.org/downloads/packages/eclipse-ide-java-ee-developers/keplerr)
	* Install the plugin [Google GSON](http://code.google.com/p/google-gson/) from [Orbit repository](http://download.eclipse.org/tools/orbit/downloads/) ([latest](http://download.eclipse.org/tools/orbit/downloads/drops/R20130517111416/repository/) at the time of writing)
* Java SE - tested with latest version (1.7 at the time of writing)

Tested on Microsoft Windows 7 Enterprise 64-bit SP1.

## Setup

After cloning the repository, you will have to create the Eclipse project from the sources.

The project file should have the following properties:

* root: this current folder
* sources: `src`
* resources: `resources`, `META-INF`, `plugin.xml`
* target: should go in a `bin` folder, to be compliant with the versioning (ignoring)
* natures:
	* `org.eclipse.pde.PluginNature`
	* `org.eclipse.jdt.core.javanature`


Don't forget to update the project specific settings from the plugin definition: for dependencies essentially, importing from `Plug-in dependencies`.

The Java compliance should be set to the Java version corresponding to the one used (see previous section).

## Try

1. Open the Eclipse project
1. Launch the project as an `Eclipse application`

Then you can start editing files with the `.tpl` extension.

## Development

Please refer to the sub-folders for more details about specific development.

At this current level, you can work on the global architecture, as described in the documentation.

### Backlog

1. Review the architecture to externalize the backend
	* create separate projects or at least separate folders for the frontend and the backend
	* find a way to package the Eclipse frontend plugin with an embedded backend (remember that for test purposes, you can use the detection feature of the frontend, which finds an already running backend)
1. Clean Eclipse extension points
	* Use the `org.eclipse.ui.editors.documentProviders` extension point or not?

### Documentation

1. Complete the procedure to recreate the Eclipse Project in section `Contribute > Setup`
1. Review documentation
	1. Documentation of the documentation (meta)
		* I would prefer using a `Backlog` section inside the `Contribute` one (as I'm doing here) instead of a `TODO` section. I don't know about the `FIXME`, probably keep it.
		* finish writing it, especially the guidelines section

#### Wiki

Think about putting documentation files other than `README.md` ones into the wiki. Indeed, they seem to be more general files.

The `README.md` files are specific, and there can be only one per folder. A folder often being a module, this is logical to use them to describe the module specifically.

Other files might be for more general purposes, so consider putting them into the wiki.

### Performances of process interactions

Maybe the use of JSON-RPC through HTTP can be too heavy for very frequent and simple operations done while editing. I'm mainly thinking about the frequent update of the models (source, AST (graph) and so on) concerning content, positions, etc. while the user enters text.

Think about using a custom protocol built on top of lower-level ones (TCP for instance).

The following aspects can be improved:

* connection setup: keeping connected state (contrary to basic HTTP)
* protocol overhead: limit amount of data used only for the information transmission. HTTP is pure text and thus easy to read, debug, but it can be too much. Prefer binary, and a minimum amount of required data.
* serialization: limit verbosity, prefer binary over text (JSON is already better than XML), ...
* two ways sockets: rather than a client-server model, simply make the two entities communicate both ways

There are also other standard solutions like [CORBA](http://en.wikipedia.org/wiki/Common_Object_Request_Broker_Architecture) (but I'm not sure there is an available mapping for JavaScript in this case).

I ([ymeine](https://github.com/ymeine)) found recently (05 Jul 2013) an [article](http://dailyjs.com/2013/07/04/hbase/?utm_source=feedburner&utm_medium=feed&utm_campaign=Feed%3A+dailyjs+%28DailyJS%29) talking about [Thrift](https://thrift.apache.org/). The description at least corresponds exactly to what we want to do: provide services to clients whatever the system they use, and automatically deal with remote procedure calls and so on.
