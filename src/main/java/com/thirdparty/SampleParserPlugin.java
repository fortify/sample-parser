package com.thirdparty;

/**
 * (c) Copyright [2017] Micro Focus or one of its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fortify.plugin.api.BasicVulnerabilityBuilder;
import com.fortify.plugin.api.ScanBuilder;
import com.fortify.plugin.api.ScanData;
import com.fortify.plugin.api.ScanParsingException;
import com.fortify.plugin.api.StaticVulnerabilityBuilder;
import com.fortify.plugin.api.VulnerabilityHandler;
import com.fortify.plugin.spi.ParserPlugin;
import com.thirdparty.scan.DateDeserializer;
import com.thirdparty.scan.Finding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static com.fasterxml.jackson.core.JsonToken.START_OBJECT;
import static com.thirdparty.CustomVulnAttribute.*;
import static com.thirdparty.ScanGenerator.GenPriority;
import static com.thirdparty.ScanGenerator.CustomStatus;

public class SampleParserPlugin implements ParserPlugin<CustomVulnAttribute> {
    private static final Logger LOG = LoggerFactory.getLogger(SampleParserPlugin.class);

    private static final JsonFactory JSON_FACTORY;
    private static final DateDeserializer DATE_DESERIALIZER = new DateDeserializer();

    static {
        JSON_FACTORY = new JsonFactory();
        JSON_FACTORY.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
    }

    @Override
    public void start() throws Exception {
        LOG.info("SampleParserPlugin plugin is starting");
    }

    @Override
    public void stop() throws Exception {
        LOG.info("SampleParserPlugin plugin is stopping");
    }

    @Override
    public Class<CustomVulnAttribute> getVulnerabilityAttributesClass() {
        return CustomVulnAttribute.class;
    }

    @Override
    public void parseScan(final ScanData scanData, final ScanBuilder scanBuilder) throws ScanParsingException, IOException {
        parseJson(scanData, scanBuilder, this::parseScanInternal);
        // complete scan building
        scanBuilder.completeScan();
    }

    private void parseScanInternal(final ScanData scanData, final ScanBuilder scanBuilder, final JsonParser jsonParser) throws IOException, ScanParsingException {
        // load data from top-level object fields
        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            final VulnAttribute vulnAttr = VulnAttribute.get(jsonParser.getCurrentName());
            jsonParser.nextToken();
            if (vulnAttr == null) {
                skipChildren(jsonParser);
                continue;
            }

            switch (vulnAttr) {
                case SCAN_DATE:
                    scanBuilder.setScanDate(DATE_DESERIALIZER.convert(jsonParser.getText()));
                    break;

                case ENGINE_VERSION:
                    scanBuilder.setEngineVersion(jsonParser.getText());
                    break;

                case ELAPSED:
                    scanBuilder.setElapsedTime(jsonParser.getIntValue());
                    break;

                case BUILD_SERVER:
                    scanBuilder.setHostName(jsonParser.getText());
                    break;

                // Skip unneeded fields
                default:
                    skipChildren(jsonParser);
                    break;
            }
        }
    }

    @Override
    public void parseVulnerabilities(final ScanData scanData, final VulnerabilityHandler vh) throws ScanParsingException, IOException {
        parseJson(scanData, vh, this::parseVulnerabilitiesInternal);
    }

    private void parseVulnerabilitiesInternal(final ScanData scanData, final VulnerabilityHandler vh, final JsonParser jsonParser) throws ScanParsingException, IOException {
        int debugCounter = 0;
        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            final String fieldName = jsonParser.getCurrentName();
            jsonParser.nextToken();
            if (fieldName.equals("findings")) {
                if (jsonParser.currentToken() != JsonToken.START_ARRAY) {
                    throw new ScanParsingException(String.format("Expected array as a value for findings at %s", jsonParser.getTokenLocation()));
                }
                while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                    assertStartObject(jsonParser);
                    final String uniqueId = parseVulnerability(scanData, vh, jsonParser);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(String.format("Parsed vulnerability %06d/%s in session %s", ++debugCounter, uniqueId, scanData.getSessionId()));
                    }
                }
            } else {
                skipChildren(jsonParser);
            }
        }
    }

    private String parseVulnerability(final ScanData scanData, final VulnerabilityHandler vh, final JsonParser jsonParser) throws IOException {
        final Finding fn = new Finding();
        loadFinding(jsonParser, fn);    // Load data from one scan json vulnerability to the Finding onject

        final StaticVulnerabilityBuilder vb = vh.startStaticVulnerability(fn.getUniqueId());  // Start new vulnerability building
        populateVulnerability(vb, fn);
        vb.completeVulnerability();  // Complete vulnerability building

        return fn.getUniqueId();
    }

    private void loadFinding(final JsonParser jsonParser, Finding fn) throws IOException {
        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            VulnAttribute vulnAttr = VulnAttribute.get(jsonParser.getCurrentName());
            jsonParser.nextToken();
            if (vulnAttr == null) {
                skipChildren(jsonParser);
                continue;
            }

            switch (vulnAttr) {

                // Custom mandatory attributes:

                case UNIQUE_ID:
                    fn.setUniqueId(jsonParser.getText());
                    break;

                // Standard SSC attributes

                case CATEGORY:
                    fn.setCategory(jsonParser.getText());
                    break;

                case FILE_NAME:
                    fn.setFileName(jsonParser.getText());
                    break;

                case VULNERABILITY_ABSTRACT:
                    fn.setVulnerabilityAbstract(jsonParser.getText());
                    break;

                case LINE_NUMBER:
                    fn.setLineNumber(jsonParser.getIntValue());
                    break;

                case CONFIDENCE:
                    fn.setConfidence(jsonParser.getFloatValue());
                    break;

                case IMPACT:
                    fn.setImpact(jsonParser.getFloatValue());
                    break;

                case PRIORITY:
                    try {
                        fn.setPriority(GenPriority.valueOf(jsonParser.getText()));
                    } catch (IllegalArgumentException e) {
                        fn.setPriority(GenPriority.Medium);
                    }
                    break;

                // Custom attributes

                case CATEGORY_ID:
                    fn.setCategoryId(jsonParser.getText());
                    break;

                case ARTIFACT:
                    fn.setArtifact(jsonParser.getText());
                    break;

                case DESCRIPTION:
                    fn.setDescription(jsonParser.getText());
                    break;

                case COMMENT:
                    fn.setComment(jsonParser.getText());
                    break;

                case BUILD_NUMBER:
                    fn.setBuildNumber(jsonParser.getText());
                    break;

                case CUSTOM_STATUS:
                    try {
                        fn.setCustomStatus(CustomStatus.valueOf(jsonParser.getText()));
                    } catch (IllegalArgumentException e) {
                        fn.setCustomStatus(CustomStatus.NEW);
                    }
                    break;

                case LAST_CHANGE_DATE:
                    fn.setLastChangeDate(DATE_DESERIALIZER.convert(jsonParser.getText()));
                    break;

                case ARTIFACT_BUILD_DATE:
                    fn.setArtifactBuildDate(DATE_DESERIALIZER.convert(jsonParser.getText()));
                    break;

                case TEXT_BASE64:
                    fn.setTextBase64(new String(jsonParser.getBinaryValue(), StandardCharsets.US_ASCII));
                    break;

                // Skip unneeded fields:
                default:
                    skipChildren(jsonParser);
                    break;
            }
        }
    }

    private void populateVulnerability(final StaticVulnerabilityBuilder vb, final Finding fn) {

        // Set builtin attributes
        vb.setCategory(fn.getCategory());                             // REST -> issueName
        vb.setFileName(fn.getFileName());                             // REST -> fullFileName or shortFileName
        vb.setVulnerabilityAbstract(fn.getVulnerabilityAbstract());   // REST -> brief
        vb.setLineNumber(fn.getLineNumber());                         // REST -> N/A, UI issue table -> part of Primary Location
        vb.setConfidence(fn.getConfidence());                         // REST -> confidence
        vb.setImpact(fn.getImpact());                                 // REST -> impact
        try {
            vb.setPriority(BasicVulnerabilityBuilder.Priority.valueOf(fn.getPriority().name()));  // REST -> friority, UI issue table -> Criticality
        } catch (IllegalArgumentException e) {
            // Leave priority unset if the value from scan is unknown
        }

        // Set string custom attributes
        if (fn.getUniqueId() != null) {
            vb.setStringCustomAttributeValue(UNIQUE_ID, fn.getUniqueId());
        }
        if (fn.getCategoryId() != null) {
            vb.setStringCustomAttributeValue(CATEGORY_ID, fn.getCategoryId());
        }
        if (fn.getArtifact() != null) {
            vb.setStringCustomAttributeValue(ARTIFACT, fn.getArtifact());
        }
        if (fn.getBuildNumber() != null) {
            vb.setStringCustomAttributeValue(BUILD_NUMBER, fn.getBuildNumber());
        }
        if (fn.getCustomStatus() != null) {
            vb.setStringCustomAttributeValue(CUSTOM_STATUS, fn.getCustomStatus().name());
        }

        // set long string custom attributes
        if (fn.getDescription() != null) {
            vb.setStringCustomAttributeValue(DESCRIPTION, fn.getDescription());
        }
        if (fn.getComment() != null) {
            vb.setStringCustomAttributeValue(COMMENT, fn.getComment());
        }
        if (fn.getTextBase64() != null) {
            vb.setStringCustomAttributeValue(TEXT_BASE64, fn.getTextBase64());
        }

        // set date custom attributes
        if (fn.getLastChangeDate() != null) {
            vb.setDateCustomAttributeValue(LAST_CHANGE_DATE, fn.getLastChangeDate());
        }
        if (fn.getArtifactBuildDate() != null) {
            vb.setDateCustomAttributeValue(ARTIFACT_BUILD_DATE, fn.getArtifactBuildDate());
        }
    }


    private static <T> void parseJson(final ScanData scanData, final T object, final Callback<T> fn) throws ScanParsingException, IOException {
        try (
                final InputStream content = scanData.getInputStream(x -> x.endsWith(".json"));
                final JsonParser jsonParser = JSON_FACTORY.createParser(content)
        ) {
            jsonParser.nextToken();
            assertStartObject(jsonParser);
            fn.apply(scanData, object, jsonParser);
        }
    }

    private static void assertStartObject(final JsonParser jsonParser) throws ScanParsingException {
        if (jsonParser.currentToken() != START_OBJECT) {
            throw new ScanParsingException(String.format("Expected object start at %s", jsonParser.getTokenLocation()));
        }
    }

    private void skipChildren(final JsonParser jsonParser) throws IOException {
        switch (jsonParser.getCurrentToken()) {
            case START_ARRAY:
            case START_OBJECT:
                jsonParser.skipChildren();
                break;
        }
    }

    private interface Callback<T> {
        void apply(final ScanData scanData, final T object, final JsonParser jsonParser) throws ScanParsingException, IOException;
    }
}
