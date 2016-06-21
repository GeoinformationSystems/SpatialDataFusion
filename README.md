# Spatial Data Fusion
=================

This repository contains basic components and a Web-Client for the implementation of data fusion within Spatial Data Infrastructures. An approach to wrap this library by a Web Processing Service is available at https://github.com/cobweb-eu/wps-fusion.

## Structure

The components are written in Java. The Web-client uses JSF with Primefaces

* ``/fusion-data`` - Interface specifications for data
* ``/fusion-data-impl`` - Data implementation (uses GeoTools)
* ``/fusion-operation`` - Interface specifications for operations  
* ``/fusion-operation-impl`` - Operation implementation (uses GeoTools)
* ``/fusion-webapp`` - Web-Client based on Java Server Faces

For each implementation, a number of test units are provided. However, some of them refer to spatial data and data services that are not publicly available. They accordingly need to be ignored for testing.

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