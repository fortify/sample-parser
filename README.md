# Sample parser plugin 
## Example of a plugin that can parse non-Fortify security scan results and import them into Fortify Software Security Center. 

## Java plugin API
- All types of plugins are developed against plugin-api (current version is plugin-api-1.0.1.jar)
- Plugin API version 1.0 supports only Parser Plugin (`com.fortify.plugin.spi.ParserPlugin`) type of plugin. 
- The SPI a plugin can implement is in package `com.fortify.plugin.spi` of plugin-api library
- The API a plugin can use is in `com.fortify.plugin.api` of plugin-api library
- Sample parser plugin implements `com.fortify.plugin.spi.ParserPlugin`

## Plugin requirements
- Plugin has to be a single Java library (JAR)
- All plugin dependencies have to be extracted and packed inside Plugin JAR as individual classes. Including other JARs inside plugin JAR file is not supported
- Plugin has to implement one and only one of service provider interfaces in plugin-api/com.fortify.plugin.spi
  - Plugin has to declare SPI implementation in `META-INF/services` for parser plugin implementation it would be a `com.fortify.plugin.spi.ParserPlugin` file containing declaration of class which implements `com.fortify.plugin.spi.ParserPlugin` interface
- Plugin JAR has to contain plugin.xml manifest in root of JAR. See the description of the plugin manifest attributes below

## Plugin Metadata Specification 
The [plugin metadata specification](https://github.com/FortifySaTPublish/plugin-api/blob/master/src/main/resources/schema/pluginmanifest-1.0.xsd "Plugin manifest XSD") defines the acceptable plugin metadata in a plugin jar. The plugin metadata specification is enforced by the plugin framework when new plugins are installed. The enforcement of the specification is an implementation detail of the plugin framework and can differ for different plugin types - however, currently for parser plugins, it is mainly enforced using a plugin manifest XML file (see below) but can also include additional validations implemented in code.  

## Plugin manifest file description
- Plugin manifest is an xml file whose name has to be "plugin.xml". Plugins that do not contain this file in the root of plugin jar file cannot be installed in SSC
- plugin.xml schema is provided by [plugin manifest XSD](https://github.com/FortifySaTPublish/plugin-api/blob/master/src/main/resources/schema "Plugin manifest XSD")
- Description of the attributes that can be defined in the plugin.xml: (The constraints listed for the various fields are meant to give a general idea of acceptable values and are not exhaustive. For the authoritative reference, consult the `pluginmanifest XSD` from the release that your plugin needs to be compatible with.) 
  - __Plugin id (id):__ unique plugin identifier defined by the plugin developer. It must satisfy a few properties - Uniqueness, Stability over time,  Compactness, and Readability (for logging/debugging purposes). We recommend that it be constructed in the following way: `(your domain name in reverse) + separator + (meaningful name such as build artifactID)`. Do not include any version information - that is specified separately below. 
    Mandatory. Max length: 80 chars.

    Example of plugin ID definition:
    ```
    id="com.example.parser.SampleParser"
    ```
  - __Plugin API version (api-version):__ plugin API version that was used to develop the plugin. Plugin framework uses this value to check if the deployed plugin is compatible with plugin API supported in current plugin framework. If api version defined in plugin manifest is not supported by plugin framework, plugin installation will be rejected.
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
    - __Plugin version (version):__ version of the plugin. SSC performs plugin package validation using this value when plugin is installed and forbids installation of older versions of the plugin if newer version of the same plugin is already installed in SSC. This check helps avoid certain human errors when installing/managing plugins. 
      Mandatory. Max length: 25 chars. Format: dot separated numbers.
      
      Example of plugin version definition:
      ```
      <version>10.5</version>
      ```
    - __Plugin data version (data-version):__ This attribute helps SSC to understand if new version of the plugin produces data in the same format as previous version of the plugin. This is very important attribute used by SSC quite intensively and plugin developers should set value of this attribute very carefully.
    
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
        - __Plugin vendor URL (vendor : url):__ Address of the website of the plugin developer company or person. Optional. Max length: 100 chars.

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
          Localization file must be valid key value property file. Only UTF-8 encoding of the localization files is supported.

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
          Current version of the framework supports only 2 types if images: __icon__ and __logo__.
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
      This attribute contain a meaningful name of the analyser (engine) that produces files that can be parsed by a parser. It is recommended that you use the public analyser product name to define the supported engine type of the plugin. 
      Different plugin developers can develop plugins that can parse the same analysers scan results. So, using public analysers product names as engine types should help SSC to check if multiple plugins installed in SSC are compatible with each other and are able to parse different types of analysis result files.
      __One plugin can support only one engine type.__
      If it is really necessary to change the supported engine type, a new plugin must be released for the new engine type and must have a different unique pluginId. 
      Mandatory. Max length: 80 chars. Format: uppercased latin characters / numbers separated by space, hyphen, dot, underscore or space.

      Examples of plugin engine type definition
      ```
        <engine-type>SAMPLE</engine-type>

        or

        <engine-type>BLACKDUCK</engine-type>

        or

        <engine-type>WEBINSPECT</engine-type>
      ```

    - __Supported engine versions (supported-engine-versions):__ descriptive field that shows which versions of a supported analyzer produces scan results that the parser plugin can parse. 
      Not mandatory but if specified it must be in the format specified by the XSD. Max length: 40 characters.
      Example of supported engine versions definition
      ```
        <supported-engine-versions>[2.2, 4.3]</supported-engine-versions>
      ```

    - __Vulnerability view template (view-template):__ definition of view template that is used by SSC to represent details of vulnerability parsed by a parser.
    View template is responsible for defining which attributes of a vulnerability will be represented in vulnerability details view and also defines where exactly vulnerability attribute values will be represented.
    All parser plugins must specify a view template - even if they only produce attributes already existing in SSC.  This is to avoid any inconsistency of content and dependency of plugin's view on the native view presented by a specific version of SSC. No issue details will be displayed if the viewtemplate is absent. 
    There are a few rules that SSC follows to generate UI from vew template. Understanding of these rules will help plugin developers to create valid template.
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
        The top array can contain up to 3 elements representing the 3 columns. If array contains more than 3 elements, elements over the 3rd one are ignored.
        Column N definition consists of 1 or more fields definition of the following structure:
        ```javascript
            {
              "type": " -- value -- ",
              "key": " -- value -- ",
              "templateId": " -- value -- ",
              "dataType": " -- value -- "
            }
        
        ```

        - `type` is type of the attribute field
        - `key` is name of the vulnerability attribute that must be displayed
        - `templateId` identifies the way how attribute values must be represented
        - `dataType` defines the exact type of the attribute value. This attribute is used to format value of vulnerability attribute

        Template rendering engine currently supports 4 types:
        - `SIMPLE` - simple section without any special styling
        - `COLLAPSE` - collapsible panel
        - `TITLEBOX` - this type of field will be displayed as a title
        - `PRIMARYTAG` - this type says that field should be rendered as a primary custom tag value. Please refer to SSC documentation about the custom tags

        List of supported data types: `string`, `date` and `float`.

        It is also possible to combine attributes into logical groups with common header. This construction should be used to do so.

        ```javascript
            {
              "type": "template",
              "templateId": "TITLEBOX",
              "title": "Details",
              "items": [
                {
                  "Field 1 definition"
                },
                {
                  "Field 2 definition"
                },
                {
                  "Field N definition"
                }
              ]
            }
        ```

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
<plugin xmlns="xmlns://www.fortify.com/schema/pluginmanifest-1.0.xsd"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="PluginDescriptor"
        id="com.example.parser" api-version="1.0">
    <plugin-info>
        <name>Sample parser plugin</name>
        <version>2.0</version>
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

## Setting up the plugin framework working directory location (SSC 17.20)
- The JVM system property `fortify.plugins.home` provides the plugin installation directory. 
- `fortify.plugins.home` is set by default to `<fortify.home>/plugin-framework/` or `<fortify.home>/<app-context>/plugin-framework/` if the plugin framework runs inside Software Security Center web application.
 - The default location of the plugin directory is `<fortify.plugins.home>/plugins`.
 
## Installing and enabling the plugin (for SSC 17.20)
- SSC version 17.20 supports plugin installation through the plugin management UI (Administration > Plugins).
- In this version, plugins are modeled in SSC with three primary states: "installed/disabled", "enabled", and "uninstalled/not present".  (It also models some transient and failure states, but these can be ignored for now.)
- In subsequent text, the terms "family of plugins" and "plugin family" refer to a set of plugins with small code variations that may differ in pluginVersion and/or dataVersion but are identified by the same pluginId. SSC 17.20 allows installation of multiple plugins belonging to the same family (with some restrictions). 
 Use the `Add` button to install a new plugin in SSC.
- All installed plugins are disabled after installation, in that the plugins are defined in SSC, but cannot do any work or accept any requests from SSC.
- To enable a plugin, select the plugin row in the Plugins list, and then click `Enable`.
- The plugin container log `<fortify.plugins.home>/log` should contain an INFO record about the plugin's successful installation or enablement (start). For example:
`org.apache.felix.fileinstall - 3.5.4 | Started bundle: file:<fortify.plugins.home>/plugins/com.example.parser.jar`
- SSC performs several validation steps when plugins are being installed or enabled. SSC can block plugin installation and  enablement if conditions such as the following exist:
      - Installing a plugin is not allowed if a plugin from the same family but later version is already installed in SSC. Because plugins are developed by 3rd-party developers, SSC has no access to details about the logic implemented in plugins.
        In this case, SSC assumes that later versions of some plugins can produce data that is incompatible with an earlier version of the plugins, resulting in SSC system instability.
        If you absolutely must install an earlier version of a plugin (for example, to roll back from a defective later version), remove the later version of the plugin, and then install the earlier version.
      - You cannot install an earlier __data version__ of a plugin in SSC.
      - To maintain consistency of information displayed in the Administration UI with the underlying pluginIds, SSC ensures that plugins in the same family have the same name and other identifying attributes (such as engineType). 
      - Only one plugin of a plugin family (sharing the same pluginId and name) can be enabled at a given time. 
      

## Disabling/Uninstalling from SSC (for SSC 17.20)
- You can remove plugin installed in SSC if the plugin is in the "disabled" state. 
  - To disable a plugin, select it in the Plugins list, and then click `Disable`.
  - To remove a plugin in the "disabled" state, view the plugin details, and then click `Remove`.
- When a parser plugin is disabled or uninstalled, SSC can no longer process new result files from the engine type that was supported by that plugin. 
- However, all the data previously parsed by the disabled or uninstalled plugin is preserved in the database and vulnerabilities that have been parsed by the plugin can still be viewed in the audit page listing. 
  - Further, if the plugin has been just __disabled__, the details of previously-parsed issues are still visible. 
  - However, if the plugin has also been __uninstalled__, the details of these vulnerabilities are not visible or available since the view template is also gone. 
- If a plugin is uninstalled by mistake, you can install it again without data loss.

## Scan artifact uploading requirements
- The scan result file must be accompanied by `scan.info` metadata and packed together into a ZIP file. 
  - The ZIP must contain at least two entries:
    - /scan.info
    - /raw.scan - name and location depend on parser implementation and how it retrieves entry from `com.fortify.plugin.api.ScanData` (for example, `scanData.getInputStream(x -> x.endsWith(".json"))` retrieves files that end with the `.json` extension)
- Optionally, you can upload 3rd-party scans as raw scans (not packed in ZIP with `scan.info`), but only through SSC REST API, where call to REST API has to provide the engine type as a call parameter. Example:
  - retrieve file upload token; using for example admin user and password `curl --noproxy localhost -X POST -H "Content-Type: application/json" -u admin:password -T "uploadFileToken.json" http://localhost:8080/ssc/api/v1/fileTokens` where content of `uploadFileToken.json` is `{"fileTokenType": "UPLOAD"}`
  - upload scan with engine type parameter; using token retrieved in previous operation `curl --noproxy localhost -X POST --form files=@"security.csv" "http://localhost:8080/ssc/upload/resultFileUpload.html?mat=TOKEN_FROM_PREV_OPERATION&entityId=APPLICATION_VERSION_ID&engineType=SAMPLE"` where engine type parameter matches engine type registered by the parser plugin (`plugin.xml/plugin/issue-parser/engine-type`)

## `scan.info` metadata contract
- `scan.info` is a property file
  - SSC can retrieve two properties from the file: `engineType` (STRING) and `scanDate` (STRING)
- The `scan.info` file must provide at least engineType property, designating scan producer, which will match engine type registered by parser plugin (`plugin.xml/plugin/issue-parser/engine-type`).
- The `scan.info` file can also provide the `scanDate` property value in ISO-8601 format.
  - If `scanDate` is not provided, the parser plugin is responsible for providing a meaningful scan date value for SSC operations.

## Generating scan with random data
The sample plugin library can also be used as a generator for scans that can be parsed by the plugin itself. The usage is as follows:
- `java -cp sample-parser-[version].jar com.thirdparty.ScanGenerator output_scan.zip ISSUE_COUNT CATEGORY_COUNT LONG_TEXT_SIZE`
  - For example, `java -cp b:\tmp\sample-plugin\sample-parser-1.0.412650.3098.jar com.thirdparty.ScanGenerator sample_scan.zip 50 10 500`

## Debugging
- A developer can follow `ssc.log` and `plugin-framework.log` to monitor what is happening in SSC and the plugin container.
  - `ssc.log` is, by default, located in the application server log directory or can be configured by the  `com.fortify.ssc.logPath` JVM system property.
  - The plugin container log is stored by default in the `<fortify.plugins.home>/log` directory and can be configured in `org.ops4j.pax.logging.cfg` (`WEB-INF/plugin-framework/etc`).

## FAQ
1) What is a scan?
   - A scan is a file in analyser-specific format that contains analysis results and can be parsed by a scan parser plugin.
  
2) What is the vulnerability ID and what are the basic rules that a plugin must follow to provide it?
   - SSC uses the vulnerability ID intensively to track vulnerability status. For example, the ID is used to determine whether some vulnerability was **fixed** (it is not present in the latest scan), **reintroduced** (the previous scan did not contain the vulnerability, but the latest scan does), **updated** (both the latest and the previous scans contain some vulnerability) or **new** if the vulnerability was found for the first time.
   - __ID must be unique__ among vulnerability IDs in a specific scan. The scan file is considered incorrect and is not processed if the plugin provides multiple vulnerabilities with the same ID.
   - If the same vulnerability exists in different scans, the ID of this vulnerability must be the same in the different scans. If IDs are not consistent for the same vulnerability in different scans, vulnerability status is not calculated correctly and SSC users cannot see how many new issues are produced or how many old issues are fixed after processing of the latest scan.
   - Some security analysers produce IDs that the plugin can pass to SSC without additional processing.
   - If analysers do not provide vulnerability identifiers in scan result files, the parser plugin is responsible for generating this ID using some other set of vulnerability attributes if they are unique for issues in one scan and the same for the same issues in different scans.
   
3) How to release new version of the plugin *if no changes have been made in custom vulnerability attributes definitions*?
   - Make any necessary changes to the plugin code.
   - Increase __plugin version__ in the plugin.xml descriptor.
   - Since the plugin's output format __was not changed__ and new version of the plugins produces custom attributes in exactly the same way as in the previous version of the plugin, __data version__ of the plugin __must not be changed__
   - The plugin can be built and distributed to users.
   
4) How to release new version of the plugin *if some changes have to be made to the custom vulnerability attribute definitions or to the vulnerability view template*? (changes in number, names or types of the attributes).
   - Enum class that implements the `com.fortify.plugin.spi.VulnerabilityAttribute` interface and contains custom attributes definitions must be updated if any changes to custom attributes definitions are required.
     New attributes must be added there or existing attributes definitions must be modified.
   - If changes to the vulnerability template are required to modify the way vulnerabilities are represented in the SSC UI, the file location is defined by issue-parser -> view-template section must be edited.
   - If necessary, plugin localization files whose locations are defined plugin-info -> resources -> localization sections must be modified.
   - Increase __plugin version__ in plugin.xml descriptor.
   - Increase __data version__ in plugin.xml descriptor. It will indicate to SSC that a new version of the plugin provides data in a new format.
   - The plugin can be built and distributed to users.
   
5) There is no parser to process a scan.
   - The engine type provided with the scan is different from the engine type provided by the parser plugin, or there is no installed/enabled plugin of the specified engine type in SSC.
   - Parser plugin registration failed - check the plugin container logs and SSC logs for errors.
  
6) Will my plugin developed for SSC/PluginFramework 17.10 work automatically with SSC/PluginFramework 17.20?
   - There is a high probability that your plugin will be compatible with 17.20. However, due to significant improvements and validations added in SSC 17.20, be prepared to test your plugin with SSC/PluginFramework 17.20 and update your plugin for compatibility, if needed. 

