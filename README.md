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
The [plugin metadata specification](https://github.com/fortify/plugin-api/blob/master/src/main/resources/schema/pluginmanifest-1.0.xsd "Plugin manifest XSD") defines the acceptable plugin metadata in a plugin jar. When new plugins are installed, the plugin framework enforces the plugin metadata specification. The enforcement of the specification is an implementation detail of the plugin framework and can differ for different plugin types. However, for parser plugins, the specification is mainly enforced using a plugin manifest XML file (see the Plugin manifest file description below), but can also include additional validations implemented in code.

## Plugin manifest file description
- The plugin manifest is an XML file named plugin.xml. You cannot install a plugin in SSC if the plugin does not contain the plugin.xml file in the root of the plugin JAR.
- The [plugin manifest XSD](https://github.com/fortify/plugin-api/blob/master/src/main/resources/schema "Plugin manifest XSD") provides the - plugin.xml schema.
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

        <engine-type>ACME_ENGINE</engine-type>

        or

        <engine-type>ACME ENGINE-TYPE.1</engine-type>
      ```

    - __Supported engine versions (supported-engine-versions):__ Descriptive field that shows which versions of a supported analyzer produces scan results that the parser plugin can parse.
      This is not mandatory, but if set, the value must be in the format specified by the XSD. Max length: 40 characters.
      Example supported engine versions definition:
      ```
        <supported-engine-versions>[2.2, 4.3]</supported-engine-versions>
      ```

    - __Vulnerability view template (view-template):__ definition of view template that SSC uses to represent details of vulnerability parsed by a parser.
    The view template is responsible for determining which vulnerability attributes are to be represented in the vulnerability details view. It also defines where, exactly, vulnerability attribute values are to be represented.
    All parser plugins must specify a view template - even if they only produce attributes that already exist in SSC.  This prevents content inconsistencies and dependency of the plugin's view on the native view presented by a specific SSC version. SSC displays no issue details if the view template is absent.
    SSC follows a few rules to generate the UI from the view template. Understanding these rules will help plugin developers to create valid templates.
    The vulnerability details area consists of three vertical sub-areas (columns). Each of these columns can display one or more vulnerability attributes.
    Columns are defined from left to right. It is not allowed to define column's content with higher index and leave column with lower index empty.
    Columns can contain different number of attributes, as in the example below

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

        Simple view template example:

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
                  "key": "customAttributes.description",
                  "templateId": "SIMPLE",
                  "dataType": "string"
                }
              ]
            }
          ],
          [
            {
              "type": "template",
              "key": "customAttributes.textBase64",
              "templateId": "COLLAPSE",
              "dataType": "string"
            }
          ]
        ]
      ```

## Matching attributes provided by parser and /issueDetail/{id} endpoint response attributes

To make it possible to build issue view template, developers must know how /issueDetails response attributes matched to issue attributes that are set when issues are parsed by a plugin.

Issue builder attributes | issue details attribute
--- | ---
accuracy | Not exposed
analyzer | analyzer
engineType | engineType
category | issueName prefix (before :)
subCategory | issueName suffix (after :)
mappedCategory | Not exposed
confidence | confidence
priority | friority
impact | impact
vulnerabilityAbstract | detail
vulnerabilityRecommendation | tips
kingdom | kingdom
likelihood | likelihood
probability | Not exposed
ruleGuid | primaryRuleGuid
severity | severity
className | Not exposed
fileName | fullFileName, shortFileName
functionName | Not exposed
lineNumber | Not exposed
sourceFile | Not exposed
sourceLine | Not exposed
packageName | Not exposed
sink | Not exposed
sinkContext | Not exposed
source | Not exposed
sourceContext | Not exposed
minVirtualCallConfidence | Not exposed
remediationConstant | Not exposed
taintFlag | Not exposed
customAttributeName | customAttributes.*customAttributeName*

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
        <description>Simple parser plugin implementation example:</description>
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
- A plugin must be built with all dependencies contained in the plugin library.
 - All dependencies must be extracted and included in the plugin JAR as individual packages and classes. The plugin class loader cannot access JARs within the JAR file. It is implemented in the Gradle build script provided with the sample plugin.
- Example Gradle build `build.gradle` is provided with the plugin.
    Tasks supported by build script:
    - `gradle clean` Cleans up previous build results
    - `gradle build` Builds plugin binary. The plugin library artifact is created as `build/libs/sample-parser-[version].jar`
    - `gradle cleanIdea` IntelliJ Idea IDE users can use this to clean up the IDE work folder.
    - `gradle idea` IntelliJ Idea IDE users can use this to generate IDE project files.
- Sources includes a Gradle wrapper that can be used to build the project. The wrapper downloads the Gradle distribution on first run. The build must also have access to the Maven Central repository for downloading some project dependencies. Depending on your platform, use either the `gradlew.bat` or the `gradlew` script.

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

## Generating scan with fixed or random data
The sample plugin library can also be used as a generator for scans that can be parsed by the plugin itself.

Two types of scans can be generated. A fixed scan with more realistic but small data and a random scan with artificial data but with configurable size.
The fixed scan will be automatically generated to the `build/scan/fixed-sample-scan.zip` as a part of a project's build.

The usage for the fixed scan generator is as follows:
- `java -cp path/to/sample-parser-[version].jar com.thirdparty.ScanGenerator fixed <FIXED_OUTPUT_SCAN_ZIP_NAME>`
  - For example, in the project root: `java -cp build/libs/* com.thirdparty.ScanGenerator fixed fixed_sample_scan.zip`

The usage for the random scan generator is as follows:
- `java -cp path/to/sample-parser-[version].jar com.thirdparty.ScanGenerator random <RANDOM_OUTPUT_SCAN_ZIP_NAME> <ISSUE_COUNT> <CATEGORY_COUNT> <LONG_TEXT_SIZE>`
  - For example, in the project root: `java -cp build/libs/* com.thirdparty.ScanGenerator random random_sample_scan.zip 50 10 500`

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

6) Will my parser plugin developed for SSC/PluginFramework 17.10 work automatically with SSC/PluginFramework 17.20?
   - No. There is a change in XML namespace for plugin.xml. So the minimal change needed for 17.20 support is plugin.xml update. After that, there is a high probability that your plugin will be compatible with 17.20. However, due to significant improvements and validations added in SSC 17.20, be prepared to test your plugin with SSC/PluginFramework 17.20 and update your plugin for compatibility, if needed.
