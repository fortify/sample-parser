package com.thirdparty;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fortify.plugin.api.ScanBuilder;
import com.fortify.plugin.api.ScanData;
import com.fortify.plugin.api.ScanParsingException;
import com.fortify.plugin.api.StaticVulnerabilityBuilder;
import com.fortify.plugin.api.VulnerabilityHandler;
import com.fortify.plugin.spi.ParserPlugin;
import com.fortify.plugin.spi.VulnerabilityAttribute;
import com.thirdparty.scan.DateDeserializer;
import com.thirdparty.scan.Finding;
import com.thirdparty.scan.Scan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Arrays;

import static com.fasterxml.jackson.core.JsonToken.START_OBJECT;
import static com.thirdparty.SampleParserVulnerabilityAttribute.ARTIFACT;
import static com.thirdparty.SampleParserVulnerabilityAttribute.ARTIFACT_BUILD_DATE;
import static com.thirdparty.SampleParserVulnerabilityAttribute.BUILD_NUMBER;
import static com.thirdparty.SampleParserVulnerabilityAttribute.BUILD_SERVER;
import static com.thirdparty.SampleParserVulnerabilityAttribute.LAST_CHANGE_DATE;
import static com.thirdparty.SampleParserVulnerabilityAttribute.RATIO;
import static com.thirdparty.SampleParserVulnerabilityAttribute.TEXT1;
import static com.thirdparty.SampleParserVulnerabilityAttribute.TEXT2;
import static java.math.BigDecimal.ROUND_HALF_UP;

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
            switch (fieldName) {
                case "scanDate":
                    scanBuilder.setScanDate(DATE_DESERIALIZER.convert(jsonParser.getText()));
                    break;
                case "engineVersion":
                    scanBuilder.setEngineVersion(jsonParser.getText());
                    break;
                case "elapsed":
                    scanBuilder.setElapsedTime(jsonParser.getIntValue());
                    break;
                case "hostName":
                    scanBuilder.setHostName(jsonParser.getText());
                    break;
                default:
                    // skip unneeded fields
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
            switch (fieldName) {

                // standard attributes

                case "uniqueId":
                    f.setUniqueId(jsonParser.getText());
                    break;
                case "category":
                    f.setCategory(jsonParser.getText());
                    break;
                case "fileName":
                    f.setFileName(jsonParser.getText());
                    break;
                case "vulnerabilityAbstract":
                    f.setVulnerabilityAbstract(jsonParser.getText());
                    break;
                case "lineNumber":
                    f.setLineNumber(jsonParser.getIntValue());
                    break;
                case "confidence":
                    f.setConfidence(jsonParser.getFloatValue());
                    break;

                // custom attributes

                case "buildServer":
                    f.setBuildServer(jsonParser.getText());
                    break;
                case "artifact":
                    f.setArtifact(jsonParser.getText());
                    break;

                case "text1":
                    f.setText1(new String(jsonParser.getBinaryValue(), StandardCharsets.US_ASCII));
                    break;
                case "text2":
                    f.setText2(new String(jsonParser.getBinaryValue(), StandardCharsets.US_ASCII));
                    break;

                case "ratio":
                    f.setRatio(jsonParser.getDecimalValue().setScale(VulnerabilityAttribute.MAX_DECIMAL_SCALE, ROUND_HALF_UP));
                    break;
                case "buildNumber":
                    f.setBuildNumber(jsonParser.getDecimalValue().setScale(VulnerabilityAttribute.MAX_DECIMAL_SCALE, ROUND_HALF_UP));
                    break;

                case "lastChangeDate":
                    f.setLastChangeDate(DATE_DESERIALIZER.convert(jsonParser.getText()));
                    break;
                case "artifactBuildDate":
                    f.setArtifactBuildDate(DATE_DESERIALIZER.convert(jsonParser.getText()));
                    break;

                default:
                    // skip unneeded fields
                    skipChildren(jsonParser);
                    break;
            }
        }

        // start new vulnerability
        final StaticVulnerabilityBuilder v = vh.startStaticVulnerability(f.getUniqueId());
        // set builtin attributes
        v.setCategory(f.getCategory());
        v.setFileName(f.getFileName());
        v.setVulnerabilityAbstract(f.getVulnerabilityAbstract());
        v.setLineNumber(f.getLineNumber());
        v.setConfidence(f.getConfidence());
        // set string custom attributes
        if (f.getBuildServer() != null) {
            v.setStringCustomAttributeValue(BUILD_SERVER, f.getBuildServer());
        }
        if (f.getArtifact() != null) {
            v.setStringCustomAttributeValue(ARTIFACT, f.getArtifact());
        }
        // set long string custom attributes
        if (f.getText1() != null) {
            v.setStringCustomAttributeValue(TEXT1, f.getText1());
        }
        if (f.getText2() != null) {
            v.setStringCustomAttributeValue(TEXT2, f.getText2());
        }
        // set big decimal custom attributes
        if (f.getRatio() != null) {
            v.setDecimalCustomAttributeValue(RATIO, f.getRatio());
        }
        if (f.getBuildNumber() != null) {
            v.setDecimalCustomAttributeValue(BUILD_NUMBER, f.getBuildNumber());
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
            final JsonParser jsonParser = JSON_FACTORY.createParser(content);
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
