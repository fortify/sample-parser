# Sample parser plugin (for SSC versions 17.10, 17.20 and Plugin API version 1.0, 1.1)

## Java plugin API
- All types of plugins are developed against plugin-api (current version is plugin-api-1.0.jar)
- Plugin API version 1.0 supports only Parser Plugin (`com.fortify.plugin.spi.ParserPlugin`) type of plugin. Bugtracker plugins support implementation is in progress
- The SPI a plugin can implement is in package `com.fortify.plugin.spi` of plugin-api library
- The API a plugin can use is in `com.fortify.plugin.api` of plugin-api library
- Sample parser plugin implements `com.fortify.plugin.spi.ParserPlugin`

## Plugin requirements
- Plugin has to be a single Java library (JAR)
- All plugin dependencies have to be extracted and packed inside Plugin JAR as individual classes. Including other JARs inside plugin JAR file is not supported
- Plugin has to implement one and only one of service provider interfaces in plugin-api/com.fortify.plugin.spi
 - Plugin API v1.0 supports only ParserPlugin
 - Plugin has to declare SPI implementation in `META-INF/services`; for parser plugin implementation it would be a `com.fortify.plugin.spi.ParserPlugin` file containing declaration of class which implements `com.fortify.plugin.spi.ParserPlugin`
- Plugin JAR has to contain plugin.xml manifest in root of JAR. See the description of the plugin manifest attributes below

## Plugin manifest file description
- Plugin manifest is an xml file which name has to be plugin.xml. Plugins that do not contain this file in the root of plugin jar file cannot be installed in SSC
- Plugin.xml schema is provided by plugin-api/schema/pluginmanifest-1.0.xsd schema file
- Description of the attributes that can be defined in the plugin.xml:
  - __Plugin id (id):__ unique plugin identifier defined by plugin developers. It can be any string that identifies plugin: development company domain name, primary package name or anything else. 
    Mandatory. Max length: 80 chars.

    Example of plugin ID definition:
    ```
    id="com.example.parser"
    ```
  - __Plugin API version (api-version):__ plugin API version that was used to develop the plugin. Plugin framework uses this value to check if deployed plugin is compatible with the current version of the framework itself. If api version defined in plugin manifest is not supported by plugin framework, plugin installation will be rejected.
    Mandatory. Max length is 8 chars. Format: dot separated numbers.

    Example of plugin API version definition:
    ```
    api-version="1.0"
    ```
  - __Plugin info (plugin-info):__ section contains generic attributes valid for all types of the plugins supported by SSC.
    - __Plugin name (name):__ meaningful name of the plugins that will be displayed in SSC UI and help users to identify the plugin.
      Mandatory. Max length: 40 chars. Allowed symbols: letters, space, hyphen, dot.
      Example of plugin name definition:
      ```
      <name>Sample parser plugin</name>
      ```
    - __Plugin version (version):__ version of the plugin. SSC performs plugin package validation using this value when plugin is installed and in some cases forbids installation of older versions of the plugin if newer version of the same plugin is already installed in SSC.
      Mandatory. Max length: 25 chars. Format: dot separated numbers.
      Example of plugin version definition:
      ```
      <version>10.5</version>
      ```
    - __Plugin data version (data-version):__ attribute helps SSC to understand if new version of the plugin produces data in the same format as previous version of the plugin. This is very important attribute used by SSC quite intensively and plugin developers should set value of this attribute very carefully.
      Value of this attribute must be changed in the new plugin version only if plugin output data format was changed and output data produced by new version of the plugin is not compatible with data produced by older version of the plugin.
      For example, parser plugin version 1.0 produces vulnerability with only 1 String attribute. Next version of this plugin (2.0) produces vulnerabilities that have 5 attributes. It means that vulnerabilities produced by these 2 versions of the plugin cannot be compared with each other and data versions of the plugins must be different.
      In addition, data version of the plugin must be changed if vulnerability view template definition was updated (see details about vulnerability view template below).
      If new version of the plugin produces output data in exactly the same way as previous version of the plugin, data versions must be the same for both plugins.
      SSC has a strict limitation about installing plugins with lower data version if the same plugin with higher data version is already installed in SSC.
      The general rule about setting this value should be this: if no changes in output data format, plugin data version must not be changed. In case any changes in output data format data version must be increased.
      Mandatory. Data version value must be valid integer number between 1 and Java max integer value.
      Example of plugin data version definition
      ```
      <data-version>1</data-version>
      ```
    - __Plugin vendor (vendor):__ basic information about company or individual that developed a plugin.
        - __Plugin vendor name (vendor : name):__ Name of the company or person that developed the plugin. Optional. Max length: 80 chars.
        - __Plugin vendor URL (vendor : url):__ Address of the WEB-site of the plugin developer company or person. Optional. Max length: 100 chars.

      Example of plugin vendor definition
      ```
      <vendor name="A vendor" url="https://plugin.dev.com/"/>
      ```

    - __Plugin description (description):__ description of the plugin. Any information useful for plugin users can be placed here.
      Optional. Max length: 500 chars.

      Example of plugin description definition
      ```
      <description>Parser description.</description>
      ```

    - __Plugin resources (resources):__ This section contains references to the resource files that can be included in the plugin package. Currently, only 2 types of resources are supported: localization files and images.
        - __Plugin localization files (resources : localization):__ collection of language sections that define languages supported by plugin. For parser plugins localization files must contain valid names of the vulnerability attributes that parser plugin can produce.
          Each language definition consist of HTML ISO language code and path to localization file located inside plugin package.
          There is a special language code "default". Language of this type will be used if SSC client requested language which localization is not included in the plugin package.
          If SSC client requests localization for a language not defined in plugin manifest and there is no default language defined, English language localization will be used as a default.
          Location attribute of the language section must contain full path to localization file inside plugin package.
          Localization file must be valid key value property file.

          Example of plugin localization definition
          ```
            <localization>
                <language id="default" location="/resources/sample_en.properties"/>
                <language id="en" location="/resources/sample_en.properties"/>
                <language id="ru" location="/resources/sample_ru.properties"/>
            </localization>
          ```

          Example of plugin localization file
          ```
            artifact=Artifact name
            artifactBuildDate=Artifact build date
            brief=Vulnerability description
            buildNumber=Build number
          ```

        - __Plugin images (resources : images):__ collection of images definitions provided by plugin. For each image definition image type (image : imageType attribute) and location (image : location attribute) must be be set.
          Current version of the framework supports only 2 types if images: __icon__ and <icon>logo</logo>.
          Location attribute must contain full path to the image file inside plugin package.
          Icons are displayed in the plugins list in SSC plugin management UI. Icon images also used to mark issues parsed by scan parser plugin to make it easier to distinguish those issues from the once parsed by native SSC parsers.
          The idea of logo image is to represent plugin developer logotype.
          Only one image of each type can be defined in this section.
          Both images must be PNG files with transparent background. Preferred resolution for icons is 50 x 50 pixels or less. Preferred resolution for logos is 225 x 50 or less.

          Example of plugin images definition
          ```
            <images>
                <image imageType="icon" location="/images/sample-icon.png"/>
                <image imageType="logo" location="/images/sample-logo.png"/>
            </images>
          ```

- __Scan vulnerability (issue) parser specific attributes (issue-parser):__ this section must be used to define parser plugin specific attributes and should not be included in manifest of plugins of any other types.
    - __Vulnerability source engine type (engine-type):__ this attribute is very important and helps SSC to coordinate work of different types of parser plugins installed in SSC and distinguish vulnerabilities parsed by different parser plugins.
      This attribute contain a meaningful name of the analyser (engine) that produces files that can be parsed by a parser. It is recommended to use public marketing analyser product names to define supported engine type of the plugin.
      Different plugin developers can develop plugins that can parse the same analysers scan results. So, using public analysers product names as engine types should help SSC to check if multiply plugins installed in SSC are not compatible with each other and are able to parse different types of analysis result files.
      One plugin can support only one engine type. If plugin developers need to parser that is able to parse results of different analysers, they should develop and release 2 plugins. Each of the plugins must support some specific engine type.
      It is also not recommended to change supported engine type without a strong reason. If it is really necessary to change name of the supported engine type, data version value must be at least increased. Another option is to release new plugin for new engine type.
      Mandatory. Max length: 80 chars. Format: upper cased latin characters / numbers separated by space, hyphen, dot, underscore or space.

      Examples of plugin engine type definition
      ```
        <engine-type>SAMPLE</engine-type>

        or

        <engine-type>BLACKDUCK</engine-type>

        or

        <engine-type>WEBINSPECT</engine-type>
      ```

    - __Supported engine versions (supported-engine-versions):__ descriptive field that shows which versions of a supported analyzer produces scan results that the parser plugin can parse.
      This is free form text that can contain comma separated versions, version range, some text comment.
      Not mandatory. Max length: 40 characters.
      Example of supported engine versions definition
      ```
        <supported-engine-versions>[2.2, 4.3]</supported-engine-versions>
      ```

    - __Vulnerability view template (view-template):__ definition of view template that is used by SSC to represent details of vulnerability parsed by a parser.
    View template is responsible for defining which attributes of a vulnerability will be represented in vulnerability details view and also defines where exactly vulnerability attribute values will be represented.
    Defining view templates makes sense for the plugins that can produce custom vulnerability attributes that do not exist in SSC data model.
    In this case SSC will not be able to generate UI to represent the custom attributes and defining vew template is the only way to tell SSC how vulnerability parsed by a plugin should be represented.
    There are few rules that SSC follows to generate UI from vew template. Understanding of these rules will help plugin developers to create valid template.
    Vulnerability details area consist of 3 vertical sub-areas (columns). Each of these columns can display 1 or more vulnerability attributes.
    Columns are defined from left to right. It is not allowed to define column's content with higher index and leave column with lower index empty.
    Columns can contain different number of attributes, as in the example below
    
        | Column1       | Column2       | Column3     |
        |:-------------:|:-------------:|:-----------:|
        | Attribute A   | Attribute D   | Attribute E |
        | Attribute B   | Attribute E   |             |
        | Attribute C   | Attribute F   |             |
        |               | Attribute G   |             |
    
        Vulnerability template contains only names of the attributes which values should be displayed. Actual values fo vulnerability attributes are taken from issue object returned by `/issueDetails/{id}`  REST service.
    This is high level structure of vulnerability template:
    
      ```javascript
        [
          [
             "Column 1 definition"
          ],
          [
             "Column 2 definition"
          ],
          [
             "Column 3 definition"
          ]
        ]
      ```
        Each second level array element contain definition of the column. This array can contain up to 3 elements. If array contains more than 3 elements, elements over the 3rd one are ignored.
        Column N definition has the following structure: 
        ```javascript
            {
              "type": " -- value -- ",
              "key": " -- value -- ",
              "templateId": " -- value -- ",
              "dataType": " -- value -- "
            }
        
        ```
        where `type` is type of the attribute field, `key` is name of the vulnerability attribute that must be displayed, `templateId` identifies the way how attribute values must be represented, `dataType` defines the exact type of the attribute value.
        
        Simple view template example is posted below.

      ```javascript
        [
          [
            {
              "type": "template",
              "key": "brief",
              "templateId": "COLLAPSE",
              "dataType": "string"
            },
            {
              "type": "fieldset",
              "htmlClass": "container-spacer-bottom",
              "items": [
                {
                  "type": "template",
                  "key": "fullFileName",
                  "templateId": "SIMPLE",
                  "dataType": "string"
                }
              ]
            },
            {
              "type": "template",
              "title": "Custom attributes",
              "templateId": "TITLEBOX",
              "items": [
                {
                  "type": "template",
                  "key": "customAttributes.buildServer",
                  "templateId": "SIMPLE",
                  "dataType": "string"
                }
              ]
            }
          ],
          [
            {
              "type": "template",
              "key": "customAttributes.text1",
              "templateId": "COLLAPSE",
              "dataType": "string"
            },
            {
              "type": "template",
              "key": "customAttributes.text2",
              "templateId": "COLLAPSE",
              "dataType": "string"
            }
          ]
        ]
      ```

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
 - All dependencies has to be extracted and included in plugin jar as individual packages and classes, plugin class loader can't access JARs in JAR. It is implemented in the Gradle build script provided with sample plugin.
- Example Gradle build `build.gradle` is provided with the plugin.
    Tasks supported by build script:
    - `gradle clean` clean up previous build results
    - `gradle build` build plugin binary. Produced plugin library artifact is created as `build/libs/sample-parser-[version].jar`
    - `gradle cleanIdea` task can be useful for IntelliJ Idea IDE users to cleaning up IDE work folder
    - `gradle idea` task can be used for IntelliJ Idea IDE users to generate IDE project files
- Sources includes Gradle wrapper that can be used to build the project. The wrapper will download Gradle distribution on first run. The build also needs access to Maven Central repository for downloading some project dependencies. Depending on your platform either `gradlew.bat` or `gradlew` scripts should be used.

## Setting up plugin working directory location
- Plugin installation directory is given by VM system property `fortify.home` and plugin directory configuration property `plugin.dir` in `com.fortify.plugin.framework.properties` (`WEB-INF/plugin-framework/etc`)
 - By default the location of plugin working directory is `<user.home>/.fortify/plugin-framework/`
- Plugin bundles folder located inside plugin working directory is used by SSC to keep temporary OSGI plugin bundles transformed from installed plugins
 - Transformed bundles are by default stored in `<user.home>/.fortify/plugin-framework/plugin-bundles`
 - Location can be modified by setting `fortify.home` VM system property and `plugin.bundles.dir` property in `com.fortify.plugin.framework.properties` (`WEB-INF/plugin-framework/etc`)

## Installation to SSC (for SSC 17.10)
- SSC version 17.10 supports only basic installation of plugin through filesystem. This way to install plugins is deprecated in SSC 17.20 in favor plugin installation through management UI.
- By default the location for plugin installation is in `<plugin-working-directory>/plugin-framework/plugins`
- SSC makes an attempt to automatically enable plugins after installation (make them fully available to SSC)
- Before plugin is enabled, it is first transformed to OSGi bundle that can be started in SSC's plugin container
 - Transformation injects:
    - OSGi related properties to `META-INF/MANIFEST.MF` of plugin
    - OSGi blueprint contexts with necessary beans to `OSGI-INF/blueprint`
- Once plugin is successfully transformed, it is started in plugin container
- Plugin container log should contain INFO record about plugin being successfully started, e.g.:
`org.apache.felix.fileinstall - 3.5.4 | Started bundle: file:<user.home>/.fortify/plugin-framework/plugin-bundles/com.example.parser.jar`
 - plugin container uses Felix file install to poll and install plugin bundles from filesystem location
- There are several validation steps performed by SSC when plugins are being installed or enabled. Plugin installation as well as plugin enable actions can be blocked by SSC if following conditions are met:
      - Plugin of lower version is being installed, but plugin of higher versions is already installed in SSC. Since plugins are developed by 3rd party developers, SSC does know any details about the logic implemented in plugins.
        In this case SSC assumes that higher versions of some plugin can produce data that will not be compatible with lower version of the plugins that can make SSC system unstable.
        If lower version of a plugin must be installed anyway, all higher versions of this plugin must be removed (uninstalled) from SSC.
      - Plugin of lower __data version__ can be installed in SSC, but cannot be enabled if plugin with the same ID but higher data version is already enabled. It means that if 2 plugins which ID is A are installed in SSC, only one of them can be enabled.

## Installation to SSC and enabling plugin (for SSC 17.20)
- SSC version 17.20 supports installation of plugin through plugin management UI (Administration - Plugins - Parsers)
  `Add` button should be used to install new plugin in SSC
- All installed plugins are disabled after installation. Disabled means that plugin is defined in SSC but cannot do any work and accept any requests from SSC
- To enable plugin click on plugin row in the plugins list and click `Enable` button
- Just after installation of a plugin it is first transformed to OSGi bundle that can be started in SSC's plugin container
- Plugin container log should contain INFO record about plugin being successfully installed or enabled (started), e.g.:
`org.apache.felix.fileinstall - 3.5.4 | Started bundle: file:<user.home>/.fortify/plugin-framework/plugin-bundles/com.example.parser.jar`
- There are several validation steps performed by SSC when plugins are being installed or enabled. Plugin installation as well as plugin enable actions can be blocked by SSC if following conditions are met:
      - Plugin of lower version is being installed, but plugin of higher versions is already installed in SSC. Since plugins are developed by 3rd party developers, SSC does know any details about the logic implemented in plugins.
        In this case SSC assumes that higher versions of some plugin can produce data that will not be compatible with lower version of the plugins that can make SSC system unstable.
        If lower version of a plugin must be installed anyway, all higher versions of this plugin must be removed (uninstalled) from SSC.
      - Plugin of lower __data version__ can be installed in SSC, but cannot be enabled if plugin with the same ID but higher data version is already enabled. It means that if 2 plugins which ID is A are installed in SSC, only one of them can be enabled.

## Uninstallation from SSC (for SSC 17.10)
- SSC version 17.10 supports only uninstallation of plugin through filesystem. This way to uninstall plugins is deprecated in SSC 17.20 in favor plugin uninstallation through management UI.
- To uninstall plugin from SSC plugin it is necessary
  - Delete plugin bundle file from `<plugin-bundles>` folder
  - Delete plugin package file from `<plugins>` folder

## Uninstallation from SSC (for SSC 17.20)
- Any plugin previous installed in SSC can be removed
  - Only disabled plugin can be removed. To disable a plugin select it in the plugins list and click Disable button
- When parser plugin is uninstalled, all the data parsed by this plugin is preserved in database
- Vulnerabilities of the engine type that was supported by uninstalled plugin cannot be parsed by SSC after plugin uninstallation
- Vulnerabilities that have been parsed by removed plugin can still be viewed in the list, but it will not be possible to view details of these vulnerabilities if issue view template was included in removed plugin package.
- If some plugin was uninstalled by mistake, it is possible to install it again without data loss.

## Scan artifact requirements
- Scan has be accompanied by `scan.info` metadata and packed into a ZIP file together
  - ZIP have to contain at least 2 entries
    1. /scan.info
    2. /raw.scan - name and location depends on parser implementation and how it retrieves entry from `com.fortify.plugin.api.ScanData` (e.g. `scanData.getInputStream(x -> x.endsWith(".json"))` retrieves file ending with `.json` extension)
- Optionally, 3rd party scan can be uploaded as a raw scan (not packed in ZIP with `scan.info`) but only through SSC REST API where call to REST API has to provide engine type as parameter of a call. Example:
  - retrieve file upload token; using for example admin user and password `curl --noproxy localhost -X POST -H "Content-Type: application/json" -u admin:password -T "uploadFileToken.json" http://localhost:8080/ssc/api/v1/fileTokens` where content of `uploadFileToken.json` is `{"fileTokenType": "UPLOAD"}`
  - upload scan with engine type parameter; using token retrieved in previous operation `curl --noproxy localhost -X POST --form files=@"security.csv" "http://localhost:8080/ssc/upload/resultFileUpload.html?mat=TOKEN_FROM_PREV_OPERATION&entityId=APPLICATION_VERSION_ID&engineType=SAMPLE"` where engine type parameter matches engine type registered by parser plugin (`plugin.xml/plugin/issue-parser/engine-type`)

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
1) What is scan?
   - Scan means a file of analyser specific format that contains analysis results and can be parsed by scan parser plugin.
2) What is vulnerability ID and what are the basic rules that plugin must follow to provide it?
   - SSC uses vulnerability ID quite intensively to track vulnerabilities status. For example, the ID is used to check if some vulnerability was **fixed** (if it is not presented in teh latest scan), **reintroduced** (previous scan did not contain some vulnerability, but the latest scan does), **updated** (both the latest and the previous to the latest scan contain some vulnerability) or **new** if vulnerability was found first time.
   - ID must be unique among vulnerability IDs in some specific scan. Scan file will be considered as incorrect and will not be processed if plugin provides more than 1 different vulnerabilities which IDs are the same
   - If the same vulnerability exists in different scans, ID of this vulnerability must be the same in different scans. If IDs are not consistent for the same issues in different scans, vulnerability status will not be calculated correctly and SSC users will not be able to see how many new issues are produced or old issues are fixed after processing of the latest scan
   - Some security analysers already produce IDs that can be passed to SSC by plugin without doing any additional processing
   - If analysers do not provide vulnerability identifiers in scan result files, parser plugin is responsible for generating this ID using some other set vulnerability attributes if they are unique for issues in one scan and the same for the same issues in different scans
3) How to release new version of the plugin if no changes have been done in custom vulnerability attributes definitions?
   - Make any necessary changes in plugin code
   - Increase __plugin version__ in plugin.xml descriptor
   - Since plugin's output format __was not changed__ and new version of the plugins produces custom attributes in exactly the same way as in previous version of the plugin, __data version__ of the plugin __must not be changed__
   - Plugin can be built and distributed to the users
4) How to release new version of the plugin if some changes have to be done in custom vulnerability attributes definitions or vulnerability view template? (e.g. changes in number, names or types of the attributes).
   - Enum class that implements `com.fortify.plugin.spi.VulnerabilityAttribute` interface and contains custom attributes definitions must be updated if any changes in custom attributes definitions are required.
     New attributes must be added there or existed attributes definitions must be modified
   - If any changes in vulnerability template are required to modify the way how vulnerabilities are represented in SSC UI, file which location is defined by issue-parser -> view-template section must be edited
   - If it is necessary, plugin localization files which locations are defined plugin-info -> resources -> localization sections must be modified
   - Increase __plugin version__ in plugin.xml descriptor
   - Increase __data version__ in plugin.xml descriptor. It will be indicator for SSC that new version of the plugin provides data in new format
   - Plugin can be built and distributed to the users
5) There is no parser to process a scan
  - engine type provided with scan is different to engine type provided by parser plugin or no plugin of specified engine type is registered with SSC
  - parser plugin registration failed - check plugin container logs and SSC logs for any errors