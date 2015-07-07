# Overview

This project packages an embedded eXist runtime into an OSGi bundle.  

When using eXist as an embeeded java object (not as a web app or stand alone server), many dependencies are required, and since the current eXist culture does not use OSGi or Maven, integration is not easy or maintainable.  Furthermore, the official instructions on embedding the runtime are incomplete, vague, and tedious.

The nice thing about OSGi is that all the eXist dependencies can be included yet isolated within the bundle; They will not interfere with the rest of the system, even if your project uses other versions of Saxon or Apache XMLRPC at the same time.

The bundle exports all of the org.xmldb.api and org.exist.xmldb packages. 

# Goals

We are hoping that the embedded eXist runtime will be faster than the http based one for server-side processing.  We are also hoping the eXist maintainers will:
 * *Get hip* to **OSGi** so that core features can be added at runtime instead of compile time.
 * Embrace **Maven** for handling dependency, build, and deployment needs.

# Usage

 * Add the bundle to your OSGi framework.  
 * From a client bundle, import the tv.twelvetone.exist.osgi, org.xmldb.api and org.exist.xmldb packages.
 * *exist_home* points to the directory where the configuration is read and log files are written to.  The config file contains the actual database location.

```
XmlDbUtil.startup(exist_home);
XmlDbUtil.test();
XmlDbUtil.shutdown();
```

This example will initialize the system while creating or reusing an existing database.  A collection called /db/test will be created and sample documents will be stored in it.  Then the sample documents will be printed to the system.out.  Finally the database will be closed.


# Disclaimer

This project is still working on an official API, and we **refactor often**; Plan on updating your client code when pulling down new updates.
