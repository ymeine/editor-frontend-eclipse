A frontend implementation for [`editor-backend`](https://github.com/ariatemplates/editor-backend) as an Eclipse plugin.

# Current development state

You can launch an Eclipse application with a plugin using the external backend (see [procedure below](#setup)) and use it to edit `.tpl` files: __despite the name of the extension only the HTML syntax will be supported inside!__ (this is due to old stories and limlitations of the backend itself...)

# File system layout

__Some of the files listed below might not appear for now, because they will be either generated or created specifically by you__

* [`.gitignore`](./.gitignore): Git related file
* `bin`: folder containing the build of the plugin

Documentation:

* [`README.md`](./README.md): this current file
* [`roadmap.md`](./roadmap.md): a roadmap for the project
* [`statics`](./statics)`: folder containing some tools for development

Code:

* [`src`](./src): the sources of the Eclipse plugin
* [`build.properties`](./build.properties), [`plugin.xml`](./plugin.xml), [`META-INF`](./META-INF): files and folders contributing to the Eclipse plugin definition
* `.project`, `.classpath`, `.settings`: files related to the Eclipse project configuration

# Versioning

What might be versioned (should be reproducible but might differ between environments - so versioning could pollute more than help):

* `.project`, `.classpath`, `.settings`

To ignore:

* `bin`: generated content (from the sources)

To version: _everything else_.

# Contribute

I would first give an advice to apply everywhere: __READ CAREFULLY THE DOCS__.

Please have a look at the [documentation of the documentation](https://github.com/ariatemplates/editor-backend/documentation.md) too.

## Environment

To be able to develop the project or even use the product you need to:

* Install Eclipse IDE - tested with latest version (Kepler at the time of writing)
	* Preferably choose [Java EE bundle](http://www.eclipse.org/downloads/packages/eclipse-ide-java-ee-developers/keplerr)
	* Install the plugin [Google GSON](http://code.google.com/p/google-gson/) from [Orbit repository](http://download.eclipse.org/tools/orbit/downloads/) ([latest](http://download.eclipse.org/tools/orbit/downloads/drops/R20130517111416/repository/) at the time of writing)
* Have a [Java SE](http://www.oracle.com/technetwork/java/javase/downloads/index.html) installation available - tested with latest version (7 at the time of writing)

Tested on Microsoft Windows 7 Enterprise 64-bit SP1.

## Setup

After cloning the [repository](https://github.com/ymeine/editors-frontend-eclipse.git), you will have to do some setup.

There are two items to setup: the Eclipse project and the backend.

### Backend

Please see the [backend project](https://github.com/ariatemplates/editor-backend)'s documentation for [setup procedure](https://github.com/ariatemplates/editor-backend#setup).

You can theoretically put it anywhere, since it communicates through network, but if you install it in a `resources` folder inside the root of this project, the plugin should be able to launch the backend automatically if not running already.

### Eclipse

Here is the full __detailed__ procedure to create the Eclipse project from the sources:

* Create a new project __inside this current folder__:
	* From the main menu `File>New>Other...`, select `Project` under category `General`
	* Give it any name you want
	* Uncheck the checkbox `Use default location`
	* Browse the file system to select this current folder
	* Click on `Finish`
* Add natures to the project:
	* open the generated `.project` file under the root folder of the project (i.e. this current folder): this file is in XML format
	* under the XML element `natures`, add natures by adding `nature` elements (example: `<natures><nature>org.eclipse.pde.PluginNature</nature></natures>`)
	* add the following natures:
		* `org.eclipse.pde.PluginNature`
		* `org.eclipse.jdt.core.javanature`
* Edit properties of the project:
	* Open properties of the project by choosing menu `Project>Properties` (or `Properties` from contextual menu of the project with right-click on it in)
	* Configure build path
		* Select `Java Build Path` on the left
		* Select tab `Source` on the right
			* Click on `Add Folder...`
			* Check:
				* `resources` (if you used it to install the backend)
				* `src` (if not already selected)
			* Note that the file `plugin.xml` and the folder `META-INF` don't have to be explicitely added
		* Select tab `Libraries`
			* Click on `Add Library...`
			* Select `Plug-in Dependencies`
			* Click `Next` then `Finish`
	* Add builders
		* open the `.project` file
		* under the element `buildSpec`, add builders by adding `buildCommand` elements, each of them containing two elements: `name` and `arguments` (example: `<buildSpec><buildCommand><name>org.eclipse.jdt.core.javabuilder</name><arguments></arguments></buildCommand></buildSpec>`)
		* add the following builders (just put the following in `name` elements):
			* `org.eclipse.jdt.core.javabuilder`
			* `org.eclipse.pde.ManifestBuilder`
			* `org.eclipse.pde.SchemaBuilder`

For simplicity, here is for the `.project` file the XML snippet resulting from the above procedure ( __this is not the whole file!!__ ):

```xml
<buildSpec>
	<buildCommand>
		<name>org.eclipse.jdt.core.javabuilder</name>
		<arguments>
		</arguments>
	</buildCommand>
	<buildCommand>
		<name>org.eclipse.pde.ManifestBuilder</name>
		<arguments>
		</arguments>
	</buildCommand>
	<buildCommand>
		<name>org.eclipse.pde.SchemaBuilder</name>
		<arguments>
		</arguments>
	</buildCommand>
</buildSpec>
<natures>
	<nature>org.eclipse.pde.PluginNature</nature>
	<nature>org.eclipse.jdt.core.javanature</nature>
</natures>
```

For the following, default values should be fine:

* the build target should go in a `bin` folder, to be compliant with the versioning - ignoring patterns.
* the Java compliance should be set to the Java version corresponding to the one used (see previous section).

## Try

* Launch the backend (see [project](https://github.com/ariatemplates/editor-backend#try))
* Launch the Eclipse application
	* Open the Eclipse project
	* Launch the project as an Eclipse application
		* Select the project and select menu `Run>Run`, or use the contextual menu of the project and select `Run As`
		* Choose `Eclipse Application`

Then you can start editing files with the `.tpl` extension under a new project.

## Development

Please refer to the subfolders of the project for details about corresponding modules specific development: every folder containing a documentation like this contains a section talking about contributions you can make to it.

Sections below discuss about development at the whole project scale.

__Please have a look at the [roadmap](./roadmap.md) too for a prioritization of what has to be done.__ It will link to specific documentations' sections (including some of below ones).

### Backend packaging

__The Eclipse plugin must be able to embed the code of the backend server and to launch and manage an instance of it properly.__

For now it's easier for the Eclipse plugin to communicate with an already running backend instance, that is launched externally.

There is still some work to do to enable the plugin to launch a backend packaged with it, especially because it is developed externally.

There are two kinds of environments to consider:

* __development__: just have a quick and dirty solution to make it work in development mode
* __production__: make it work in the context of a packaged plugin

__For development__, the sources of the server are available, and are located inside the project. Therefore to launch it ( __without hardcoding paths!!__ ), there is just a need to __resolve the relative paths inside the project__ (i.e. equivalent to programatically find the path of the project).

__For production__, the problem is different.

First, once installed, the plugin will reside in the Eclipse installation, among every other bundles. The thing would be - as it is for development - to __resolve the path of the plugin__ (but in this case it's not in the context of the project but the one of the Eclipse installation). Have a look at [`FileLocator.find`](http://help.eclipse.org/kepler/index.jsp?topic=%2Forg.eclipse.platform.doc.isv%2Freference%2Fapi%2Forg%2Feclipse%2Fcore%2Fruntime%2FFileLocator.html&anchor=find%28org.osgi.framework.Bundle,%20org.eclipse.core.runtime.IPath,%20java.util.Map%29): the `Bundle` required in this method can be taken from the [`Activator`](src/poc/Activator.java) class of this project.

But then there is a problem due to the fact a plugin is __packaged in an archive by default__. If the Java system works fine with binaries contained in archives, it is not the case of the _externally made_ server. It __relies on a standard file system access__.

For that there are two solutions:

* either finding a solution to execute the server in a virtual file system - personally I don't know how
* or finding a solution to extract the files of the archive for the server. See this [thread on stackoverflow](http://stackoverflow.com/questions/5622789/how-to-refer-a-file-from-jar-file-in-eclipse-plugin#answer-5660242) talking about plugin packaging: an option to avoid archiving would be available in case we wrap the plugin in an Eclipse feature.

NB: to bring files from the backend project into the Eclipse Plugin project without versioning issues, [Git Submodules](http://git-scm.com/book/en/Git-Tools-Submodules) can be used

### Plugin definition

__Clean Eclipse extension points.__

> Do we use the `org.eclipse.ui.editors.documentProviders` extension point or not?

We can manage without, as it is done for now, but maybe it's better for design purposes to use it.
