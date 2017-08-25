# Sample parser plugin 
## Example of a plugin that can parse non-Fortify security scan results and import them into Fortify Software Security Center. 

## Java plugin API
- All types of plugins are developed against plugin-api (current version is plugin-api-1.0.1.jar)
- Plugin API version 1.0 supports only parser plugins (`com.fortify.plugin.spi.ParserPlugin`). 
- The SPI that a plugin can implement is in package `com.fortify.plugin.spi` of plugin-api library
- The API that a plugin can use is in `com.fortify.plugin.api` of plugin-api library
- Sample parser plugin implements `com.fortify.plugin.spi.ParserPlugin`

## Plugin requirements
- Plugin has to be a single Java library (JAR)
- All plugin dependencies have to be extracted and packed inside Plugin JAR as individual classes. Software Security Center (SSC) does not support the inclusion of other JARs inside of the plugin JAR file.
- The plugin must implement one, and only one, service provider interface in plugin-api/com.fortify.plugin.spi.
  - The plugin must declare SPI implementation in `META-INF/services`. For parser plugin implementation, it would be a `com.fortify.plugin.spi.ParserPlugin` file containing declaration of a class that implements the `com.fortify.plugin.spi.ParserPlugin` interface.
- Plugin JAR must contain the plugin.xml manifest in root of the JAR file. See the description of the plugin manifest attributes below.

## Plugin Metadata Specification 
The [plugin metadata specification](https://github.com/FortifySaTPublish/plugin-api/blob/master/src/main/resources/schema/pluginmanifest-1.0.xsd "Plugin manifest XSD") defines the acceptable plugin metadata in a plugin jar. When new plugins are installed, the plugin framework enforces the plugin metadata specification. The enforcement of the specification is an implementation detail of the plugin framework and can differ for different plugin types. However, for parser plugins, the specification is mainly enforced using a plugin manifest XML file (see the Plugin manifest file description below), but can also include additional validations implemented in code.  

## Plugin manifest file description
- The plugin manifest is an XML file named plugin.xml. You cannot install a plugin in SSC if the plugin does not contain the plugin.xml file in the root of the plugin JAR.
- The [plugin manifest XSD](https://github.com/FortifySaTPublish/plugin-api/blob/master/src/main/resources/schema "Plugin manifest XSD") provides the - plugin.xml schema.
- Description of the attributes that can be defined in the plugin.xml: (The constraints listed for the various fields are meant to give a general idea of acceptable values. The list is not exhaustive. For the authoritative reference, consult the `pluginmanifest XSD` for the release with which your plugin is compatible.) 
  - __Plugin id (id):__ unique plugin identifier defined by the plugin developer. The indentifier must be unique, stable over time,  compact, and readable (for logging/debugging purposes). Fortify recommends that you construct the plugin identifier in the following way: `(your domain name in reverse) + separator + (meaningful name, such as build artifactID)`. Do not include version information. That is specified separately below. 
    Mandatory. Max length: 80 chars.

    Example plugin ID definition:
    ```
    id="com.example.parser.SampleParser"
    ```
  - __Plugin API version (api-version):__ plugin API version used to develop the plugin. The plugin framework uses this value to determine whether the deployed plugin is compatible with plugin API supported in current plugin framework. If the API version defined in the plugin manifest is not supported by plugin framework, the plugin installation will fail.
    Mandatory. Max length is 8 chars. Format: dot-separated numbers

    Example plugin API version definition:
    ```
    api-version="1.0"
    ```
  - __Plugin info (plugin-info):__ section contains generic attributes valid for all the plugin types that SSC supports.
    - __Plugin name (name):__ meaningful name of the plugins to be displayed in the SSC UI. The name should help users to identify the plugin.
      Mandatory. Max length: 40 chars. Allowed symbols: letters, space, hyphen, and dot.
      
      Example lugin name definition:
      ```
      <name>Sample parser plugin</name>
      ```
    - __Plugin version (version):__ version of the plugin. SSC uses this value to validate the plugin package when a plugin is installed. SSC prevents plugin installation if a newer version of that plugin is already installed in SSC. This check helps avoid certain human errors when installing/managing plugins. 
      Mandatory. Max length: 25 chars. Format: dot-separated numbers.
      
      Example plugin version definition:
      ```
      <version>10.5</version>
      ```
    - __Plugin data version (data-version):__ This attribute helps SSC determine whether a new version of the plugin produces data in the same format as the previous plugin version. This is an important attribute that SSC uses heavily. Plugin developers must set value for this attribute very carefully.
    
      The value of this attribute must be changed in the new plugin version only if the plugin output data format was changed and the output data that the new plugin version produces is incompatible with the data that the older version produced.
      For example, parser plugin version 1.0 produces vulnerability with only 1 String attribute. The next version (2.0) produces vulnerabilities that have five attributes. The vulnerabilities that these two versions produce cannot be compared with each other, and data versions of the plugins must be different.
      In addition, you must change the data version of the plugin if the vulnerability view template definition was updated. (See details about vulnerability view template below.)
      If a new plugin version produces output data in exactly the same way as the previous version, the data versions must be the same for both plugins.
      SSC has a strict limitation about installing plugins with lower data version if the same plugin with higher data version is already installed in SSC.
      The general rule for setting this value is this: If the output data format has not changed, then you must not change the plugin data version. If the output data format changes, you must increase the data version.
      Mandatory. Data version value must be a valid integer between 1 and the Java max integer value.
      Example plugin data version definition:
      ```
      <data-version>1</data-version>
      ```
    - __Plugin vendor (vendor):__ basic information about company or individual that developed a plugin.
        - __Plugin vendor name (vendor : name):__ Name of the company or person that developed the plugin. Optional. Max length: 80 chars.
        - __Plugin vendor URL (vendor : url):__ Address of the website of the plugin developer company or person. Optional. Max length: 100 chars.

      Example plugin vendor definition:
      ```
      <vendor name="A vendor" url="https://plugin.dev.com/"/>
      ```

    - __Plugin description (description):__ description of the plugin. Any information useful for plugin users can be placed here.
      Optional. Max length: 500 chars.

      Example plugin description definition:
      ```
      <description>Parser description.</description>
      ```

    - __Plugin resources (resources):__ This section contains references to the resource files that can be included in the plugin package. Currently, only two types of resources are supported: localization files and images.
        - __Plugin localization files (resources : localization):__ collection of language sections that define languages that the plugin suppports. For parser plugins, localization files must contain valid names of the vulnerability attributes that the parser plugin can produce.
          Each language definition consist of HTML ISO language code and the path to the localization file located inside the plugin package.
          There is a special language code "default". This language type is used if an SSC client requests a language that has not been localized in the plugin package.
          If SSC client requests localization for a language that is not defined in the plugin manifest, and no default language is defined, English language localization is used as the default.
          The location attribute of the language section must contain the full path to the localization file in the plugin package.
          The localization file must be a valid key value property file. SSC supports only UTF-8 encoding of the localization files.

          Example plugin localization definition:
          ```
            <localization>
                <language id="default" location="/resources/sample_en.properties"/>
                <language id="en" location="/resources/sample_en.properties"/>
                <language id="ru" location="/resources/sample_ru.properties"/>
            </localization>
          ```
          Example plugin localization file:
          ```
            artifact=Artifact name
            artifactBuildDate=Artifact build date
            brief=Vulnerability description
            buildNumber=Build number
          ```

        - __Plugin images (resources : images):__ collection of images definitions provided by the plugin. For each image definition, you must set the image type (image : imageType attribute) and location (image : location attribute).
          Current version of the framework supports only two image types: __icon__ and __logo__.
          The location attribute must include the full path to the image file inside of the plugin package.
          Icons are displayed in the plugins list in the SSC plugin management user interface. Icon images used to mark issues parsed by scan parser plugin make it easier to distinguish those issues from the ones parsed by native SSC parsers.
          The idea of the logo image is to represent the plugin developer logotype.
          You can define only one image of each type can in this section.
          Both image types must be PNG files with transparent backgrounds. The preferred resolution for icons is 50 x 50 pixels or less. The preferred resolution for logos is 225 x 50 pixels or less.

          Example plugin images definition:
          ```
            <images>
                <image imageType="icon" location="/images/sample-icon.png"/>
                <image imageType="logo" location="/images/sample-logo.png"/>
            </images>
          ```

- __Scan vulnerability (issue) parser specific attributes (issue-parser):__ Use this section to define parser plugin-specific attributes. Do not include this section in the manifest of other plugin types.
    - __Vulnerability source engine type (engine-type):__ This important attribute helps SSC to coordinate work of different parser plugins types installed in SSC and to distinguish vulnerabilities parsed by different parser plugins.
      This attribute contains a meaningful name for the analyser (engine) that produces files that a parser can parse. Fortify recommends that you use the public analyser product name to define the supported engine type for the plugin. 
      Different developers can develop plugins that can parse the same analyser's scan results. So, using public analysers' product names as engine types should help SSC to determine whether multiple plugins installed in SSC are compatible with each other and whether they can parse different types of analysis result files.
      __One plugin can support only one engine type.__
      If you absolutely must change the supported engine type, a new plugin (with a different, unique plugin ID) must be released for the new engine type. 
      Mandatory. Max length: 80 chars. Format: uppercase Latin characters / numbers to be separated by spaces, hyphens, dots, or underscores.

      Example plugin engine type definitions:
      ```
        <engine-type>SAMPLE</engine-type>

        or

        <engine-type>BLACKDUCK</engine-type>

        or

        <engine-type>WEBINSPECT</engine-type>
      ```

    - __Supported engine versions (supported-engine-versions):__ Descriptive field that shows which versions of a supported analyzer produces scan results that the parser plugin can parse. 
      This is not mandatory, but if set, the value must be in the format specified by the XSD. Max length: 40 characters.
      Example supported engine versions definition:
      ```
        <supported-engine-versions>[2.2, 4.3]</supported-engine-versions>
      ```

    - __Vulnerability view template (view-template):__ Definition of view template that is used by SSC to represent details of a vulnerability parsed by a parser.
    The view template defines which vulnerability attributes are represented in vulnerability details view, and also indicates where exactly vulnerability attribute values are represented.
    All parser plugins must specify a view template, even if they produce only attributes that already exist in SSC.  This is meant to prevent any inconsistency in content and dependency of the plugin's view on the native view presented by a specific SSC version. If no view template is specified, SSC displays no issue details.
    There are a few rules that SSC follows to generate the UI from view template. Understanding these rules will help plugin developers to create valid templates.
    The vulnerability details area consist of three vertical sub-areas (columns). Each of these columns can display one or more vulnerability attributes.
    Columns are defined from left to right. You cannot define the content for a column, but leave a column with a lower index empty.
    Columns can contain different numbers of attributes, as shown in the following example:
    
        | Column1       | Column2       | Column3     |
        |:-------------:|:-------------:|:-----------:|
        | Attribute A   | Attribute D   | Attribute E |
        | Attribute B   | Attribute E   |             |
        | Attribute C   | Attribute F   |             |
        |               | Attribute G   |             |
    
        A vulnerability template contains only names of the attributes for which values should be displayed. The actual values of vulnerability attributes are taken from issue objects returned by `/issueDetails/{id}`  REST service.
    The high-level structure of a vulnerability template is as follows:

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
        The top array can contain up to three elements representing the three columns. If the array contains more than three elements, only the first three elements are "seen." The rest are ignored.
        Column N definition consists of one or more fields defined with the following structure:
        ```javascript
            {
              "type": " -- value -- ",
              "key": " -- value -- ",
              "templateId": " -- value -- ",
              "dataType": " -- value -- "
            }
        
        ```

        - `type` is the attribute field type
        - `key` is name of the vulnerability attribute to display
        - `templateId` identifies how to represent attribute values
        - `dataType` defines the exact type of the attribute value. This is used to format the vulnerability attribute value 

        Template rendering engine currently supports four types:
        - `SIMPLE` - simple section with no special styling
        - `COLLAPSE` - collapsible panel
        - `TITLEBOX` - field type to be displayed as a title
        - `PRIMARYTAG` - this type says that the field is to be rendered as a primary custom tag value. For more information, see the SSC documentation about the custom tags.

        List of supported data types: `string`, `date` and `float`

        You can combine attributes into logical groups with a common header. This construction should be used to do so.

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

## Setting up plugin framework working directory location (SSC 17.20)
- Plugin installation directory is given by JVM system property `fortify.plugins.home`
- `fortify.plugins.home` is set by default to `<fortify.home>/plugin-framework/` or `<fortify.home>/<app-context>/plugin-framework/` if plugin framework runs inside Software Security Center web application
 - By default the location of plugin directory is `<fortify.plugins.home>/plugins`
 
## Installation to SSC and enabling plugin (for SSC 17.20)
- SSC version 17.20 supports installation of plugin through plugin management UI (Administration - Plugins)
- In this version, plugins are modeled in SSC with three primary states - "installed/disabled", "enabled", "uninstalled/not present".  (It also models some transient and failure states but we can ignore those for now.)
- In subsequent text, we will use the terms "family of plugins" or "plugin family" to refer to a set of plugins with small code variations which may differ in pluginVersion and/or dataVersion but are identified by the same pluginId. SSC 17.20 allows multiple plugins of the same family to be installed (with some restrictions). 
  `Add` button should be used to install new plugin in SSC
- All installed plugins are disabled after installation. Disabled means that plugin is defined in SSC but cannot do any work and accept any requests from SSC
- To enable plugin click on plugin row in the plugins list and click `Enable` button
- Plugin container log `<fortify.plugins.home>/log` should contain INFO record about plugin being successfully installed or enabled (started), e.g.:
`org.apache.felix.fileinstall - 3.5.4 | Started bundle: file:<fortify.plugins.home>/plugins/com.example.parser.jar`
- There are several validation steps performed by SSC when plugins are being installed or enabled. Plugin installation as well as plugin enable actions can be blocked by SSC if certain conditions are met, such as:
      - A plugin of lower version is not allowed if a plugin from the same family and higher version is already installed in SSC. Since plugins are developed by 3rd party developers, SSC does know any details about the logic implemented in plugins.
        In this case SSC assumes that higher versions of some plugin can produce data that will not be compatible with lower version of the plugins that can make SSC system unstable.
        If lower version of a plugin must be installed (for example to rollback from a buggy higher version), remove the higher version of this plugin and then install the lower version.
      - Plugin of lower __data version__ cannot be installed in SSC.
      - To maintain consistency of visual information in the administration UI with the underlying pluginIds, SSC enforces that plugins in  the same family must have the same name and any other identifying attribute such as engineType. 
      - Only one plugin of a plugin family (ie. sharing the same pluginId and name) can be enabled at a given time. 
      

## Disabling/Uninstallation from SSC (for SSC 17.20)
- A plugin previous installed in SSC can be removed if it is in the "disabled" state. 
  - To disable a plugin select it in the plugins list and click the `Disable` button.
  - To remove a plugin (which is already in the "disabled" state), view the plugin details and click the `Remove` button.
- When parser plugin is disabled or uninstalled, SSC can no longer process new result files of the engine type that were supported by that plugin. 
- However, all the data previously parsed by this plugin is preserved in the database and vulnerabilities that have been parsed by the plugin can still be viewed in the audit page listing. 
  - Further, if the plugin has been just __disabled__, the details of previously parsed issues can still be seen. 
  - However, if the plugin has also been __uninstalled__, it will not be possible to view the details of these vulnerabilities since the view template is also removed. 
- If some plugin was uninstalled by mistake, it is possible to install it again without data loss.

## Scan artifact uploading requirements
- The scan result file has to be accompanied by `scan.info` metadata and packed into a ZIP file together
  - the ZIP has to contain at least 2 entries
    1. /scan.info
    2. /raw.scan - name and location depends on parser implementation and how it retrieves entry from `com.fortify.plugin.api.ScanData` (e.g. `scanData.getInputStream(x -> x.endsWith(".json"))` retrieves file ending with `.json` extension)
- Optionally, 3rd party scan can be uploaded as a raw scan (not packed in ZIP with `scan.info`) but only through SSC REST API where call to REST API has to provide engine type as parameter of a call. Example:
  - retrieve file upload token; using for example admin user and password `curl --noproxy localhost -X POST -H "Content-Type: application/json" -u admin:password -T "uploadFileToken.json" http://localhost:8080/ssc/api/v1/fileTokens` where content of `uploadFileToken.json` is `{"fileTokenType": "UPLOAD"}`
  - upload scan with engine type parameter; using token retrieved in previous operation `curl --noproxy localhost -X POST --form files=@"security.csv" "http://localhost:8080/ssc/upload/resultFileUpload.html?mat=TOKEN_FROM_PREV_OPERATION&entityId=APPLICATION_VERSION_ID&engineType=SAMPLE"` where engine type parameter matches engine type registered by parser plugin (`plugin.xml/plugin/issue-parser/engine-type`)

## `scan.info` metadata contract
- `scan.info` is a property file
  - SSC can retrieve 2 properties from the file: `engineType` (STRING) and `scanDate` (STRING)
- `scan.info` file has to provide at least engineType property, designating scan producer, which will match engine type registered by parser plugin (`plugin.xml/plugin/issue-parser/engine-type`)
- `scan.info` can also provide `scanDate` property value in ISO-8601 format
  - if `scanDate` is not provided the parser plugin will be responsible to provide a meaningful scan date value for SSC operations

## Generating scan with random data
The sample plugin library can be also used as a generator for scans, which can be parsed by plugin itself. The usage is as follows:
- `java -cp sample-parser-[version].jar com.thirdparty.ScanGenerator output_scan.zip ISSUE_COUNT CATEGORY_COUNT LONG_TEXT_SIZE`
  - e.g. `java -cp b:\tmp\sample-plugin\sample-parser-1.0.412650.3098.jar com.thirdparty.ScanGenerator sample_scan.zip 50 10 500`

## Debugging
- Developer can follow an `ssc.log` and `plugin-framework.log` to monitor what is happening in SSC and plugin container.
  - `ssc.log` is by default located in application server log directory or can be configured by `com.fortify.ssc.logPath` JVM system property
  - plugin container log is by default stored in `<fortify.plugins.home>/log` location and can be configured in `org.ops4j.pax.logging.cfg` (`WEB-INF/plugin-framework/etc`)

## FAQ
1) What is a scan?
   - Scan means a file of analyser-specific format that contains analysis results and can be parsed by a scan parser plugin.
  
2) What is vulnerability ID and what are the basic rules that plugin must follow to provide it?
   - SSC uses vulnerability ID quite intensively to track vulnerabilities status. For example, the ID is used to check if some vulnerability was **fixed** (if it is not presented in teh latest scan), **reintroduced** (previous scan did not contain some vulnerability, but the latest scan does), **updated** (both the latest and the previous to the latest scan contain some vulnerability) or **new** if vulnerability was found first time.
   - __ID must be unique__ among vulnerability IDs in some specific scan. Scan file will be considered as incorrect and will not be processed if plugin provides multiple vulnerabilities with the same ID.
   - If the same vulnerability exists in different scans, ID of this vulnerability must be the same in different scans. If IDs are not consistent for the same issues in different scans, vulnerability status will not be calculated correctly and SSC users will not be able to see how many new issues are produced or old issues are fixed after processing of the latest scan
   - Some security analysers already produce IDs that can be passed to SSC by plugin without doing any additional processing
   - If analysers do not provide vulnerability identifiers in scan result files, parser plugin is responsible for generating this ID using some other set vulnerability attributes if they are unique for issues in one scan and the same for the same issues in different scans.
   
3) How to release new version of the plugin *if no changes have been done in custom vulnerability attributes definitions*?
   - Make any necessary changes in plugin code
   - Increase __plugin version__ in plugin.xml descriptor
   - Since plugin's output format __was not changed__ and new version of the plugins produces custom attributes in exactly the same way as in previous version of the plugin, __data version__ of the plugin __must not be changed__
   - Plugin can be built and distributed to the users
   
4) How to release new version of the plugin *if some changes have to be done in custom vulnerability attributes definitions or vulnerability view template*? (e.g. changes in number, names or types of the attributes).
   - Enum class that implements `com.fortify.plugin.spi.VulnerabilityAttribute` interface and contains custom attributes definitions must be updated if any changes in custom attributes definitions are required.
     New attributes must be added there or existed attributes definitions must be modified
   - If any changes in vulnerability template are required to modify the way how vulnerabilities are represented in SSC UI, file which location is defined by issue-parser -> view-template section must be edited
   - If it is necessary, plugin localization files whose locations are defined plugin-info -> resources -> localization sections must be modified
   - Increase __plugin version__ in plugin.xml descriptor
   - Increase __data version__ in plugin.xml descriptor. It will be indicator for SSC that new version of the plugin provides data in new format
   - Plugin can be built and distributed to the users
   
5) There is no parser to process a scan
   - engine type provided with scan is different from the engine type provided by parser plugin or there is no installed/enabled plugin of specified engine type in SSC
   - parser plugin registration failed - check plugin container logs and SSC logs for any errors
  
6) Will my plugin developed for SSC/PluginFramework 17.10 work automatically with SSC/PluginFramework 17.20 ?
   - There is a high probability that your plugin will also be compatible with 17.20 - however, due to significant improvements and validations added in SSC 17.20, you must be prepared to test your plugin with SSC/PluginFramework 17.20 and update your plugin to be compatible if needed. 

