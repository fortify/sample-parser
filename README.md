# Sample parser plugin (for SSC version 17.10 and Plugin API version 1.0)

## Java plugin API
- All types of plugins are developed against plugin-api (current version is plugin-api-1.0.jar)
- Plugin API version 1.0 supports only Parser Plugin (`com.fortify.plugin.spi.ParserPlugin`) type of plugin
- The SPI a plugin can implement is in package `com.fortify.plugin.spi` of plugin-api library
- The API a plugin can use is in `com.fortify.plugin.api` of plugin-api library
- Sample parser plugin implements `com.fortify.plugin.spi.ParserPlugin`

## Plugin requirements
- Plugin have to be a single library (JAR)
- All plugin dependencies have to be extracted and packed inside Plugin JAR
- Plugin has to implement one and only one of service provider interfaces in plugin-api/com.fortify.plugin.spi
 - Plugin API v1.0 supports only ParserPlugin
 - Plugin has to declare SPI implementation in `META-INF/services`; for parser plugin implementation it would be a `com.fortify.plugin.spi.ParserPlugin` file containing declaration of class which implements `com.fortify.plugin.spi.ParserPlugin`
- Plugin JAR has to contain plugin.xml manifest in root of JAR. See the description of the plugin manifest attributes below.

## Plugin manifest file description
- Plugin manifest is an xml file which name has to be plugin.xml. Plugins that do not contain this file in the root of plugin jar file cannot be installed in SSC.
- Plugin.xml schema is provided by plugin-api/schema/pluginmanifest-1.0.xsd.
- Description of the attributes that can be defined in the plugin.xml:
  - <b>Plugin id (id):</b> unique plugin identifier defined by plugin developers. It can be any string that identifies plugin: development company domain name, primary package name or anything else. Mandatory. Max length: 80 chars.
  - <b>Plugin API version (api-version):</b> plugin API version that was used to develop the plugin. Plugin framework uses this value to check if deployed plugin is compatible with the current version of the framework itself. If api version in plguin manifest is not supported by plugin framework, plugin installation will be rejected. Mandatory. Max length is 8 chars. Format: dot separated numbers (e.g. 1.0, 2.3.1).
  - <b>Plugin info (plugin-info):</b> section contains generic attributes valid for all types of the plugins supported by teh framework.
    - <b>Plugin name (name):</b> meaningful name of the plugins that will be displayed in SSC UI can help users to identify the plugin. Mandatory. Max length: 40 chars. Allowed symbols: letters, space, hyphen, dot.
    - <b>Plugin version (version):</b> version of the plugin. SSC performs some checks using this value when plugin is installed and in some cases forbids installation of older versions of the plugin if higher version is already installed in SSC. Mandatory. Max length: 25 chars. Format: dot separated numbers (e.g. 1.0, 2.3.1).
    - <b>Plugin data version (data-version):</b> attribute helps SSC to understand if new version of the plugin produces data in the same format as previous version of the plugin or not. This is very important attribute used by SSC quite intensively and plugin developers should set value of this attribute very carefully.
      Value of this attribute must be changed in the new plugin version only if plugin output data format was changed drastically and output data produced by new version of the plugin is not compatible with data produced by older version of the plugin.
      For example, parser plugin version 1.0 produces vulnerability with only 1 String attribute. Next version of this plugin (2.0) produces vulnerabilities that have 5 attributes. It means that vulnerabilities produced by these 2 versions of the plugin cannot be compared with each other and data versions of the plugins must be different.
      If new version of the plugin produces output data in exactly the same way as previous version of the plugin, data versions can be the same for both plugins.
      SSC has a strict limitation about installing plugins with lower data version if the same plugin with higher data version is already installed in SSC.
      The general rule about setting this value should be this: if no changes in output data format, plugin data version must not be changed. In case any changes in output data format data version must be increased.
      Mandatory. Data version value must be valid integer number between 1 and Java max integer value.
    - <b>Plugin vendor name (vendor : name):</b> Name of the company or person that developed the plugin. Optional. Max length: 80 chars.
    - <b>Plugin vendor URL (vendor : url):</b> Address of the WEB-site of the plugin developer company or person. Optional. Max length: 100 chars.
    - <b>Plugin description (description):</b> Short description of the plugin. Any information useful for plugin users can be placed here. Optional. Max length: 500 chars.
    - <b>Plugin resources (resources):</b> This section contains references to the resource files that can be included in the plugin package. Currently, only 2 types of resources are supported: localization files and images.
        - <b>Plugin localization files (resources : localization):</b> collection of language section that define languages supported by plugin. Each language definition consist of HTML ISO language code and path to localization file located incide plugin package.
          There is a special language code "default". Language of this type will be used if SSC client requested language which localization is not inclided in the plugin package.
          If SSC client requested not defined language and there is no default language defined, English language localization will be used as default.
          Location attribute of the language section must contain full path to localization file inside plugin package.
          Localization file must be valid key value property file.
        - <b>Plugin images (resources : localization):</b>
- Plugin can also provide localization resources as well as logo and icon and declare their location inside JAR in plugin.xml <resources> block
- Parser Plugin have to declare engine type in plugin manifest (plugin.xml/issue-parser/engine-type) and view template for SSC UI (plugin.xml/issue-parser/view-template)

## Plugin manifest file example
```xml
<?xml version="1.0" encoding="utf-8" ?>
<plugin xmlns="xmlns://www.fortifysoftware.com/schema/pluginmanifest-1.0.xsd"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="PluginDescriptor"
        id="com.example.parser" api-version="1.0">
    <plugin-info>
        <name>Sample parser plugin</name>
        <version><!--VERSION-->0.0<!--/VERSION--></version>
        <data-version>1</data-version>
        <vendor name="Sample vendor" url="https://sample-parser-plugin.example.com/"/>
        <description>Simple parser plugin implementation example.</description>
        <resources>
            <localization>
                <language id="default" location="/resources/sample_en.properties"/>
                <language id="cs" location="/resources/sample_cs.properties"/>
                <language id="en" location="/resources/sample_en.properties"/>
                <language id="ru" location="/resources/sample_ru.properties"/>
            </localization>
            <images>
                <image imageType="icon" location="/images/sample-icon.png"/>
                <image imageType="logo" location="/images/sample-logo.png"/>
            </images>
        </resources>
    </plugin-info>
    <issue-parser xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
        <engine-type>SAMPLE</engine-type>
        <supported-engine-versions>[2.2, 4.3]</supported-engine-versions>
        <view-template location="/viewtemplate/SampleTemplate.json">
            <description>Default sample vulnerability view template.</description>
        </view-template>
    </issue-parser>
</plugin>
```



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
