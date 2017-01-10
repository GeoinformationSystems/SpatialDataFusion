# Spatial Data Fusion
=================

This repository contains basic components and a Web-Client for the implementation of data fusion within Spatial Data Infrastructures. An approach to wrap this library by a Web Processing Service is available at https://github.com/cobweb-eu/wps-fusion.

## Structure

The components are written in Java. The Web-client uses JSF with Primefaces

* ``/fusion-model`` - Interface specifications for data and operations
* ``/fusion-impl`` - Data and operation implementation (uses GeoTools)
* ``/fusion-webapp`` - Web-Client based on Java Server Faces

For each implementation, a number of test units are provided. However, some of them refer to spatial data and data services that are not publicly available. They accordingly need to be ignored for testing.

Available operations are located under ``de.tudresden.gis.fusion.operation``. As operation input, the ``IData`` interface must be implemented.

## Installation

The project is built using ``Maven install``. The result are 4 .jar files that need to be referenced in the classpath by an implementing application. For extensions, the data nd operation interfaces must be implemented.

The data for unit tests is not uploaded. Moreover, there is no public triple store available for testing SPARQL. To prevent build failures, those tests need to be ignored.

To test the Webapp, a Servlet container (e.g. Tomcat) is required. 

## Libraries

* GeoTools for server-side geoprocessing (GNU Lesser General Public License (LGPL))
* JUnit for testing (Eclipse Public License 1.0)
* Apache Jena for RDF and SPARQL handling (Apache License, Version 2.0)
* Primefaces for client implementation (Apache License, Version 2.0)
* OpenLayers 3 for map rendering (BSD 2-Clause License)

## License

The software in the Spatial Data Fusion repository is licensed under The Apache Software License, Version 2.0

## Contact

Stefan Wiemann (stefan.wiemann@tu-dresden.de)