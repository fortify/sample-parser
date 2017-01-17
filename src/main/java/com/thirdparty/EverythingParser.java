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
import com.thirdparty.scan.Finding;
import com.thirdparty.scan.Scan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.time.Duration;

/**
 * (C) Copyright 2015,2016 Hewlett Packard Enterprise Development, L.P.
 */
public class EverythingParser implements ParserPlugin<SampleParserVulnerabilityAttribute> {
    private static final Logger LOG = LoggerFactory.getLogger(EverythingParser.class);

    private static final ObjectMapper JSONMAPPER = new ObjectMapper();

    private static final Duration elapsed = Duration.ofHours(2).plus(Duration.ofMinutes(3)).plus(Duration.ofSeconds(4));

    @Override
    public void start() throws Exception {
        LOG.info("EverythingParser plugin is starting");
    }

    @Override
    public void stop() throws Exception {
        LOG.info("EverythingParser plugin is stopping");
    }

    @Override
    public Class<SampleParserVulnerabilityAttribute> getVulnerabilityAttributesClass() {
        return SampleParserVulnerabilityAttribute.class;
    }

    @Override
    public void parseScan(final ScanData scanData, final ScanBuilder scanBuilder) throws ScanParsingException, IOException {
        final ObjectReader r = JSONMAPPER.readerFor(Scan.class);
        try (final InputStream content = getScanInputStream(scanData)) {
            // parse scan
            final Scan s = r.readValue(content);
            scanBuilder.setScanDate(s.getScanDate());
            // fake some values not present in the scan
            scanBuilder.setEngineVersion("TEST-0.1");
            scanBuilder.setHostName(InetAddress.getLocalHost().getHostName());
            scanBuilder.setElapsedTime((int) elapsed.getSeconds());
            // complete scan building
            scanBuilder.completeScan();
        } catch (final JsonProcessingException e) {
            throw new ScanParsingException("Invalid scan syntax", e);
        }
    }

    @Override
    public void parseVulnerabilities(final ScanData scanData, final VulnerabilityHandler vh) throws ScanParsingException, IOException {
        final ObjectReader r = JSONMAPPER.readerFor(Scan.class);
        try (final InputStream content = getScanInputStream(scanData)) {
            final Scan s = r.readValue(content);
            int counter = 0;
            for (Finding f : s.getFindings()) {
                counter += 1;
                StaticVulnerabilityBuilder v = vh.startStaticVulnerability(f.getUniqueId());
                // custom field
                v.setStringCustomAttributeValue(SampleParserVulnerabilityAttribute.FIELD1, f.getField());
                v.setCategory(String.format("Sample issue %d", counter));
                v.setFileName(String.format("vulnerable_file_%d_%s.bin", counter, f.getUniqueId()));
                v.setVulnerabilityAbstract(String.format("Abstract of issue %d", counter));
                v.completeVulnerability();
            }
        } catch (final JsonProcessingException e) {
            throw new ScanParsingException("Invalid scan syntax", e);
        }
    }

    private InputStream getScanInputStream(final ScanData scanData) throws IOException {
        return scanData.getInputStream(x -> x.endsWith(".json"));
    }
}
