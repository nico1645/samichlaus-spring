# Samichlaus Creator Backend

REST API in Java with [Spring](https://spring.io/) for creating and managing short round trips for multiple Groups with Customers. Created for the non-profit organization [Samichlaus-Vereinigung Hergiswil](https://www.samichlaushergiswil.ch/) as a side-project of mine. It works with the [Samichlaus Creator Frontend](https://github.com/nico1645/samichlaus-js).

### Technologies used

- **Java 17**
- [Spring Boot and Spring Security](https://spring.io/)
- [Graphhopper](https://www.graphhopper.com/)
- [GeoTools](https://geotools.org/)
- [Apache Commons](https://commons.apache.org/)

## Getting Started

### Dependencies

Make sure you have a working installation of [Java 17](https://openjdk.org/install/) and [maven](https://maven.apache.org/install.html) on your system.

### Installing

Clone the github repo and enter the root directory.
```bash
git clone https://github.com/nico1645/samichlaus-spring.git
cd ./samichlaus-spring
```
Check that Java 17 and Maven are properly installed.
```bash
java -version
mvn -version
```
Install the Maven Dependencies specified in the [pom.xml](./pom.xml).
```bash
mvn install
```
Make sure to specify the environment variables given in [application.yml](./src/main/resources/application.yml). There are 3 different profiles choose depending on production, development or testing environment and set the necessary ENV variables.

### Running/Building server

Start dev server by running.
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=[replace with profile]
```
Building for production with.
```bash
mvn clean package
```

## Version History

* 0.2.0 (main branch)
    * Added Java Mail Server and API Enpoint
    * Bug fixes and other small improvements
    * See [release tags](https://github.com/nico1645/samichlaus-spring/tags)
* 0.1.x
    * Initial Release
    * Database definition for Samichlaus Schemas (Tour, Route, Customer, Address)
    * API Enpoint for CRUD operations on Tour, Route, Customer, Address
    * Service for calculating instances of VRPs
    * Service for creating Excel based on Tour
    * Graphhopper Service for distance calculations
    * CSVService for handling csv from client
    * See [release branch](https://github.com/nico1645/samichlaus-spring/tree/0.1.x)

## Authors

Name: Nico Bachmann
Email: [contact@famba.me](mailto:contact@famba.me)

## License

This project is licensed under the GNU GPL License - see the LICENSE file for details
