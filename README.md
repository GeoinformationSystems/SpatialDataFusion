SpatialDataFusion
=================

This repository contains basic components and a client for spatial data fusion within SDI.

## Structure

The components are written in Java. The client uses JSF with Primefaces

* ``/fusion-base`` - Abstract classes and interfaces  
* ``/fusion-impl`` - Implementation based on standard geoprocessing libraries for Java
* ``/fusion-jsf-client`` - Client based on Java Server Faces

## Java Libraries

* GeoTools for server-side geoprocessing (GNU Lesser General Public License (LGPL))
* JUnit for testing (Eclipse Public License 1.0)
* Apache Jena for RDF and SPARQL handling (Apache License, Version 2.0)
* Primefaces for client implementation (Apache License, Version 2.0)

## Javascript Libraries

* OpenLayers 3 for map rendering (BSD 2-Clause License)

## License

All projects in the SpatialDataFusion repository are licensed under The Apache Software License, Version 2.0

## Contact

Stefan Wiemann (stefan.wiemann@tu-dresden.de)