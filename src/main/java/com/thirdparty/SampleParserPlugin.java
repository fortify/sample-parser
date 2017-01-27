package com.thirdparty;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fortify.plugin.api.ScanBuilder;
import com.fortify.plugin.api.ScanData;
import com.fortify.plugin.api.ScanParsingException;
import com.fortify.plugin.api.StaticVulnerabilityBuilder;
import com.fortify.plugin.api.VulnerabilityHandler;
import com.fortify.plugin.spi.ParserPlugin;
import com.fortify.plugin.spi.VulnerabilityAttribute;
import com.thirdparty.scan.Finding;
import com.thirdparty.scan.Scan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

import static com.thirdparty.SampleParserVulnerabilityAttribute.ARTIFACT;
import static com.thirdparty.SampleParserVulnerabilityAttribute.ARTIFACT_BUILD_DATE;
import static com.thirdparty.SampleParserVulnerabilityAttribute.BUILD_SERVER;
import static com.thirdparty.SampleParserVulnerabilityAttribute.BUILD_NUMBER;
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

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

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
        final ObjectReader r = JSON_MAPPER.readerFor(Scan.class);
        try (final InputStream content = getScanInputStream(scanData)) {
            // parse scan
            final Scan s = r.readValue(content);
            // set attributes
            scanBuilder.setScanDate(s.getScanDate());
            scanBuilder.setEngineVersion(s.getEngineVersion());
            scanBuilder.setElapsedTime(s.getElapsed());
            scanBuilder.setHostName(s.getHostName());
            // complete scan building
            scanBuilder.completeScan();
        } catch (final JsonProcessingException e) {
            throw new ScanParsingException("Invalid scan syntax", e);
        }
    }

    @Override
    public void parseVulnerabilities(final ScanData scanData, final VulnerabilityHandler vh) throws ScanParsingException, IOException {
        final ObjectReader r = JSON_MAPPER.readerFor(Scan.class);
        try (final InputStream content = getScanInputStream(scanData)) {
            final Scan s = r.readValue(content);
            int debugCounter = 0;
            for (final Finding f : s.getFindings()) {
                // start new vulnerability
                final StaticVulnerabilityBuilder v = vh.startStaticVulnerability(f.getUniqueId());
                // set builtin attributes
                v.setCategory(f.getCategory());
                v.setFileName(f.getFileName());
                v.setVulnerabilityAbstract(f.getVulnerabilityAbstract());
                v.setLineNumber(f.getLineNumber());
                v.setConfidence(f.getConfidence());
                // set string custom attributes
                v.setStringCustomAttributeValue(BUILD_SERVER, f.getBuildServer());
                v.setStringCustomAttributeValue(ARTIFACT, f.getArtifact());
                // set long string custom attributes
                v.setStringCustomAttributeValue(TEXT1, f.getText1());
                v.setStringCustomAttributeValue(TEXT2, f.getText2());
                // set big decimal custom attributes
                v.setDecimalCustomAttributeValue(RATIO, f.getRatio().setScale(VulnerabilityAttribute.MAX_DECIMAL_SCALE, ROUND_HALF_UP));
                v.setDecimalCustomAttributeValue(BUILD_NUMBER, f.getBuildNumber().setScale(VulnerabilityAttribute.MAX_DECIMAL_SCALE, ROUND_HALF_UP));
                // set date custom attributes
                v.setDateCustomAttributeValue(LAST_CHANGE_DATE, f.getLastChangeDate());
                v.setDateCustomAttributeValue(ARTIFACT_BUILD_DATE, f.getArtifactBuildDate());
                // complete vulnerability building
                v.completeVulnerability();
                if (LOG.isDebugEnabled()) {
                    LOG.debug(String.format("Parsed vulnerability %06d/%s in session %s", ++debugCounter, f.getUniqueId(), scanData.getSessionId()));
                }
            }
        } catch (final JsonProcessingException e) {
            throw new ScanParsingException("Invalid scan syntax", e);
        }
    }

    private InputStream getScanInputStream(final ScanData scanData) throws IOException {
        return scanData.getInputStream(x -> x.endsWith(".json"));
    }
}
