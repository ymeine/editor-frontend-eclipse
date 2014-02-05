Here are tools to build, export and package the plugin in different formats.

# File system layout

* [`README.md`](./README.md): this current file
* [`feature`](./feature): an Eclipse project for an Eclipse Feature
* [`site`](./site): an Eclipse project for an Eclipse Update Site

# Versioning

To version: _everything_.





# Introduction

## Feature

A feature is just a collection of plugins, that the Eclipse platform knows how to install. Along with that it is useful for deployment since it can also hold other information — kind of metadata — such as a license, a copyright notice, a description, etc.

As long as we have only one plugin developed, the feature might seem superfluous. However, what we really want is to provide our tool through an Eclipse Update Site, which is really handy for the user. And the latter works only with features.

## Update site

An Eclipse Update Site is composed of two things:

* a set of components that can be served
* metadata about those components, in order to serve them _better_

More precisely, an Update Site is made to provide resources to extend the Eclipse Platform, and this is done with Eclipse plugins, which in this particular case need to be packaged into features.





# Use

There are three kind of things you can build:

* the plugin only
* the feature wrapping this plugin
* the update site that will serve this feature

(as you see there is a progression.)

If you want the final product — to deliver to clients — jump directly to the build of the [`site`](./site).

If you want to build the feature, please read the [corresponding documentation](./feature).

Otherwise, read the following subsection to build the plugin.

## Build the plugin

For now nothing is automated, you will have to use the _manual_ procedure, using the Eclipse IDE.


