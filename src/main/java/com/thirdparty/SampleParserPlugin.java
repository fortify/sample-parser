package com.thirdparty;

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
import static com.thirdparty.SscBuiltInVulnerabilityAttribute.*;
import static com.thirdparty.SampleParserVulnerabilityAttribute.*;

/**
 * (C) Copyright 2015,2016 Hewlett Packard Enterprise Development, L.P.
 */
public class SampleParserPlugin implements ParserPlugin<SampleParserVulnerabilityAttribute> {
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
    public Class<SampleParserVulnerabilityAttribute> getVulnerabilityAttributesClass() {
        return SampleParserVulnerabilityAttribute.class;
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
            final String fieldName = jsonParser.getCurrentName();
            jsonParser.nextToken();
            if (fieldName.equals(SCAN_DATE.attributeName())) {
                scanBuilder.setScanDate(DATE_DESERIALIZER.convert(jsonParser.getText()));

            } else if (fieldName.equals(ENGINE_VERSION.attributeName())) {
                scanBuilder.setEngineVersion(jsonParser.getText());

            } else if (fieldName.equals(ELAPSED.attributeName())) {
                scanBuilder.setElapsedTime(jsonParser.getIntValue());

            } else if (fieldName.equals(BUILD_SERVER.attributeName())) {
                scanBuilder.setHostName(jsonParser.getText());

            // Skip unneeded fields
            } else {
                skipChildren(jsonParser);

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
                    throw new ScanParsingException(String.format("Expected array as value for findings at %s", jsonParser.getTokenLocation()));
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
        final Finding f = new Finding();
        // load data from one vulnerability object
        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            final String fieldName = jsonParser.getCurrentName();
            jsonParser.nextToken();

            // Custom mandatory attributes:
            if (fieldName.equals(UNIQUE_ID.attributeName())) {
                f.setUniqueId(jsonParser.getText());

            // Standard attributes
            } else if (fieldName.equals(CATEGORY.attributeName())) {
                f.setCategory(jsonParser.getText());

            } else if (fieldName.equals(FILE_NAME.attributeName())) {
                f.setFileName(jsonParser.getText());

            } else if (fieldName.equals(VULNERABILITY_ABSTRACT.attributeName())) {
                f.setVulnerabilityAbstract(jsonParser.getText());

            } else if (fieldName.equals(LINE_NUMBER.attributeName())) {
                f.setLineNumber(jsonParser.getIntValue());

            } else if (fieldName.equals(CONFIDENCE.attributeName())) {
                f.setConfidence(jsonParser.getFloatValue());

            } else if (fieldName.equals(IMPACT.attributeName())) {
                f.setImpact(jsonParser.getFloatValue());

            } else if (fieldName.equals(PRIORITY.attributeName())) {
                try {
                    f.setPriority(BasicVulnerabilityBuilder.Priority.valueOf(jsonParser.getText()));
                } catch (IllegalArgumentException e) {
                    f.setPriority(BasicVulnerabilityBuilder.Priority.Medium);
                }

            // Custom attributes
            } else if (fieldName.equals(CATEGORY_ID.attributeName())) {
                f.setCategoryId(jsonParser.getText());

            } else if (fieldName.equals(ARTIFACT.attributeName())) {
                f.setArtifact(jsonParser.getText());

            } else if (fieldName.equals(DESCRIPTION.attributeName())) {
                f.setDescription(jsonParser.getText());

            } else if (fieldName.equals(COMMENT.attributeName())) {
                f.setComment(jsonParser.getText());

            } else if (fieldName.equals(BUILD_NUMBER.attributeName())) {
                f.setBuildNumber(jsonParser.getText());

            } else if (fieldName.equals(CUSTOM_STATUS.attributeName())) {
                f.setCustomStatus(jsonParser.getText());

            } else if (fieldName.equals(LAST_CHANGE_DATE.attributeName())) {
                f.setLastChangeDate(DATE_DESERIALIZER.convert(jsonParser.getText()));

            } else if (fieldName.equals(ARTIFACT_BUILD_DATE.attributeName())) {
                f.setArtifactBuildDate(DATE_DESERIALIZER.convert(jsonParser.getText()));

            } else if (fieldName.equals(TEXT64.attributeName())) {
                f.setText64(new String(jsonParser.getBinaryValue(), StandardCharsets.US_ASCII));

            // Skip unneeded fields:
            } else {
                skipChildren(jsonParser);
            }
        }

        // start new vulnerability
        final StaticVulnerabilityBuilder v = vh.startStaticVulnerability(f.getUniqueId());
        // set builtin attributes
        v.setCategory(f.getCategory());                             // REST -> issueName
        v.setFileName(f.getFileName());                             // REST -> fullFileName or shortFileName
        v.setVulnerabilityAbstract(f.getVulnerabilityAbstract());   // REST -> brief
        v.setLineNumber(f.getLineNumber());                         // REST -> N/A, UI issue table -> part of Primary Location
        v.setConfidence(f.getConfidence());                         // REST -> confidence
        v.setImpact(f.getImpact());                                 // REST -> impact
        v.setPriority(f.getPriority());                             // REST -> friority, UI issue table -> Criticality

        // set string custom attributes
        if (f.getUniqueId() != null) {
            v.setStringCustomAttributeValue(UNIQUE_ID, f.getUniqueId());
        }
        if (f.getCategoryId() != null) {
            v.setStringCustomAttributeValue(CATEGORY_ID, f.getCategoryId());
        }
        if (f.getArtifact() != null) {
            v.setStringCustomAttributeValue(ARTIFACT, f.getArtifact());
        }
        if (f.getBuildNumber() != null) {
            v.setStringCustomAttributeValue(BUILD_NUMBER, f.getBuildNumber());
        }
        if (f.getCustomStatus() != null) {
            v.setStringCustomAttributeValue(CUSTOM_STATUS, f.getCustomStatus());
        }

        // set long string custom attributes
        if (f.getDescription() != null) {
            v.setStringCustomAttributeValue(DESCRIPTION, f.getDescription());
        }
        if (f.getComment() != null) {
            v.setStringCustomAttributeValue(COMMENT, f.getComment());
        }
        if (f.getText64() != null) {
            v.setStringCustomAttributeValue(TEXT64, f.getText64());
        }

        // set date custom attributes
        if (f.getLastChangeDate() != null) {
            v.setDateCustomAttributeValue(LAST_CHANGE_DATE, f.getLastChangeDate());
        }
        if (f.getArtifactBuildDate() != null) {
            v.setDateCustomAttributeValue(ARTIFACT_BUILD_DATE, f.getArtifactBuildDate());
        }

        // complete vulnerability building
        v.completeVulnerability();

        return f.getUniqueId();
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
