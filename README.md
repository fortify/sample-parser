# Sample parser plugin (for SSC version 17.10 and Plugin API version 1.0)

## Java API
- All types of plugins are developed against plugin-api (plugin-api-1.0.jar)
- Currently the API supports only Parser Plugin (`com.fortify.plugin.spi.ParserPlugin`) type of plugin
- The SPI a plugin can implement is in package `com.fortify.plugin.spi` of plugin-api library
- The API a plugin can use is in `com.fortify.plugin.api` of plugin-api library
- Sample parser plugin implements `com.fortify.plugin.spi.ParserPlugin`

## Plugin requirements
- Plugin have to be a single library (JAR)
- All plugin dependencies have to be packed inside Plugin JAR
 - Extract all dependencies to plugin JAR
- Plugin has to implement one and only one of service provider interfaces in plugin-api/com.fortify.plugin.spi
 - Plugin API v1.0 supports only ParserPlugin
 - Plugin has to declare SPI implementation in `META-INF/services`; for parser plugin implementation it would be a `com.fortify.plugin.spi.ParserPlugin` file containing declaration of class which implements `com.fortify.plugin.spi.ParserPlugin`
- Plugin JAR has to contain plugin.xml manifest in root of JAR
 - Plugin.xml schema is provided by plugin-api/schema/pluginmanifest-1.0.xsd
- Plugin can also provide localization resources as well as logo and icon and declare their location inside JAR in plugin.xml <resources> block
- Parser Plugin have to declare engine type in plugin manifest (plugin.xml/issue-parser/engine-type) and view template for SSC UI (plugin.xml/issue-parser/view-template)

## Plugin library build
- Plugin has to be built with all dependencies contained in plugin library
 - All dependencies has to be extracted, plugin class loader can't access JAR in JAR
- Example Gradle build is provided with the plugin

### Using the provided build

Sources includes Gradle wrapper that can be used to build the project. The wrapper will download Gradle distribution
on first run. The build also needs access to Maven Central repository for downloading some project dependencies.
Depending on your platform either `gradlew.bat` or `gradlew` scripts should be used.

The plugin library is built using `gradlew build` command. Produced plugin library artifact is created as
`build/libs/sample-parser-[version].jar`

The plugin version is configured inside `build.gradle` file by using project `version` property. The build
auto-raises the version with each build (time-based suffix will be added to the base version). This feature is
turned off when project is built with  `-Prelease` option to Gradle commandline. E.g. `gradlew -Prelease clean build`.

There is a pre-configured build for creating project files for IntelliJ Idea. Running `gradlew idea` will create
the project files.

## Installation to SSC
- SSC version 17.10 supports only basic installation of plugin through filesystem
- Plugin installation directory is given by VM system property `fortify.home` and plugin directory configuration property `plugin.dir` in `com.fortify.plugin.framework.properties` (`WEB-INF/plugin-framework/etc`)
 - By default the location for plugin installation is in `<user.home>/.fortify/plugin-framework/plugins`
- Before plugin is started, it is first transformed to OSGi bundle that can be started in SSC's plugin container
 - Transformed bundle is by default stored in `<user.home>/.fortify/plugin-framework/plugin-bundles`
    - Location can be modified by setting `fortify.home` VM system property and `plugin.bundles.dir` property in `com.fortify.plugin.framework.properties` (`WEB-INF/plugin-framework/etc`)
 - Transformation injects:
    - OSGi related properties to `META-INF/MANIFEST.MF` of plugin
    - OSGi blueprint contexts with necessary beans to `OSGI-INF/blueprint`
- Once plugin is successfully transformed, it is started in plugin container
- plugin container log should contain INFO record about plugin being successfully started, e.g.:
`org.apache.felix.fileinstall - 3.5.4 | Started bundle: file:<user.home>/.fortify/plugin-framework/plugin-bundles/com.example.parser.jar`
 - plugin container uses Felix file install to poll and install plugin bundles from filesystem location

## Scan artifact requirements
- Scan has be accompanied by `scan.info` metadata and packed into a ZIP file together
  - ZIP have to contain at least 2 entries
    1. /scan.info
    2. /raw.scan - name and location depends on parser implementation and how it retrieves entry from `com.fortify.plugin.api.ScanData` (e.g. `scanData.getInputStream(x -> x.endsWith(".json"))` retrieves file ending with `.json` extension)
- Optionally, 3rd party scan can be uploaded as a raw scan (not packed in ZIP with `scan.info`) but only through SSC REST API where call to REST API has to provide engine type as parameter of a call. Example:
  - retrieve file upload token; using for example admin user and password `curl --noproxy localhost -X POST -H "Content-Type: application/json" -u admin:password -T "uploadFileToken.json" http://localhost:8080/ssc/api/v1/fileTokens` where content of `uploadFileToken.json` is `{"fileTokenType": "UPLOAD"}`
  - upload scan with engine type parameter; using token retrieved in previous operation `curl --noproxy localhost -X POST --form files=@"security.csv" "http://localhost:8080/ssc/upload/resultFileUpload.html?mat=TOKEN_FROM_PREV_OPERATION&entityId=APLICATION_VERSION_ID&engineType=SAMPLE"` where engine type parameter matches engine type registered by parser plugin (`plugin.xml/plugin/issue-parser/engine-type`)

## `scan.info` metadata contract
- `scan.info` is a property file
  - SSC v17.10 can retrieve 2 properties from the file: `engineType` (STRING) and `scanDate` (STRING)
- `scan.info` file has to provide at least engineType property, designating scan producer, which will match engine type registered by parser plugin (`plugin.xml/plugin/issue-parser/engine-type`)
- `scan.info` can also provide `scanDate` property value in ISO-8601 format
  - if `scanDate` is not provided the parser plugin will be responsible to provide a meaningful scan date value for SSC operations

## Generating scan with random data
The sample plugin library can be also used as a generator for scans, which can be parsed by plugin itself. The usage is as follows:
- `java -cp sample-parser-[version].jar com.thirdparty.ScanGenerator output_scan.zip ISSUE_COUNT CATEGORY_COUNT LONG_TEXT_SIZE`
  - e.g. `java -cp b:\tmp\sample-plugin\sample-parser-1.0.412650.3098.jar com.thirdparty.ScanGenerator sample_scan.zip 50 10 500`

## Debugging
- Developer can follow an `ssc.log` and `plugin-framework.log` to monitor what is happening in SSC and plugin container.
  - `ssc.log` is by default located in application server log directory or can be configured by `ssc.log.path` VM system property
  - plugin container log is by default stored in `<user.home>/.fortify/plugin-framework/log` location and can be configured in `org.ops4j.pax.logging.cfg` (`WEB-INF/plugin-framework/etc`)
- SSC update its plugin registration if plugin version is incremented in `plugin.xml/plugin/plugin-info/version`
  - version could be incremented by build for seamless development

## FAQ
1. What is engine type
  - Designation of analyser that produced a scan, which is being uploaded to SSC and should be parsed by parser supporting the engine type
2. There is no parser to process a scan
  - engine type provided with scan is different to engine type provided by parser plugin or no plugin of specified engine type is registered with SSC
  - parser plugin registration failed - check plugin container logs and SSC logs for any errors
