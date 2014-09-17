Backend related modules





# File system layout

- [`readme.md`](./readme.md): the main documentation file

Modules: 

- [`backend/`](./backend/): communication to the actual backend
- [`process_runner/`](./process_runner/): a utility to be able to launch the backend process if needed
- [`http/`](./http/): basic HTTP transport to communicate with the backend
- [`exception/`](./exception/): wrapper for exceptions sent by the actual backend





# Documentation

The main module is the [`backend`](./backend/). It will use either an existing backend process or launch and manage a new one by using the [`process_runner`](./process_runner/).

The communication is done by using the [`http`](./http/) transport, but in the future many others could be used instead, if the actual backend implements it. It would just require some little extra configuration steps, to choose the transport, and also to be able to detect external backend processes anyway.

The [`backend`](./backend/) is therefore the entry point for communication to the actual backend. It provides a singleton, suitable since usually only one backend process is needed. It also provides generic wrappers to the routes implemented by the backend, including the main one: the RPC (Remote Procedure Call). A few shortcut methods using RPC are available too, for services related to edition. The amount of such shortcuts is a pure API design concern, it should be balanced with data passed to the functions.

RPC expects a map as input, and returns a map as output, so that everything remains simple. When the response of the request is detected as being an error, a map is still returned, however it gets wrapped by the backend [`exception`](./exception/) for nicer handling.


----

Below is an old documentation of the `Backend` class, before refactoring. To be merged with the above content.

----

# Documentation

__The backend has the role to ease management of a backend as well as generic communication with it.__ It acts like a __bridge__.

Every service the backend will provide will have a bridge implemented in this class. For instance, from basic HTTP requests to RPC over it.

Please refer to the JavaDoc of the class itself for more information on what has been implemented, and also to the documentation of the backend project.

## Re-use

__If a backend is already available on port `3000`, the frontend is able to re-use it.__

That means we need to know if there is a server listening on port `3000` and _looking like_ a backend.

For that, the server provides a GUID identification system: on a GET request with a path built with a specific GUID, the backend sends another specific GUID as a response. So if the GUIDs match on the request, a backend instance has been found.

Concretely: if a HTTP GET Request on port 3000 with the URL path [`80d007698d534c3d9355667f462af2b0`](http://localhost:3000/80d007698d534c3d9355667f462af2b0) receives a response with content `e531ebf04fad4e17b890c0ac72789956`, the server is considered to be a backend.

__REFER TO THE [BACKEND DOCUMENTATION](https://github.com/ariatemplates/editor-backend/tree/master/app#guid-identification) TO BE SURE TO HAVE THE REAL (UP-TO-DATE) VALUES__

## Launch

The plugin needs to wait for the backend to be completely launched before starting using it. However, this is an asynchronous process. Here are some solutions:

- ___CURRENT SOLUTION___: polling with a simple ping request (until some timeout is reached , to avoid infinite try)
- by waiting for a request from the backend: this is too cumbersome, as this means choosing another convention for the port number, creating several connections, etc.





# Contribute

## Communication

__Handle every type of return values.__

From JSON requests, we should be able to handle return values other than objects too (like strings for errors), or at least if we consider we should only receive objects in case of success, use the status to check if there is an error instead.

However, the backend makes an effort to always return objects, even in case of errors (but for instance if you try a non-existing route, I guess the third-party server library used behind will not).

## Performances

__General performances issues have been discussed in the main documentation file of this project.__

This mainly concerns the communication part, which is all wrapped in the [`Backend`](./Backend.java) class.

If possible configure the JSON library used - here Google GSON - in order not to create Double for numbers but integers instead:

- register a type-adapter
- define explicit classes with proper field types and use it for deserialization
- ...

Otherwise study other libraries.
