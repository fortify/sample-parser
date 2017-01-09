package com.thirdparty;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fortify.plugin.api.ScanData;
import com.fortify.plugin.event.Event;
import com.fortify.plugin.result.Status;
import com.fortify.plugin.result.parser.StaticVulnerabilityBuilder;
import com.fortify.plugin.result.parser.VulnerabilityHandler;
import com.fortify.plugin.spi.ParserPlugin;
import com.fortify.plugin.spi.ScanParsingException;
import com.thirdparty.scan.Finding;
import com.thirdparty.scan.Scan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * (C) Copyright 2015,2016 Hewlett Packard Enterprise Development, L.P.
 */
public class EverythingParser implements ParserPlugin<SampleParserVulnerabilityAttribute> {
    private static final Logger LOG = LoggerFactory.getLogger(EverythingParser.class);

    private static final ObjectMapper JSONMAPPER = new ObjectMapper();


    @Override
    public void stop() throws Exception {
        LOG.info("Plugin is stopping");
    }

    @Override
    public Status status() {
        return null;
    }

    @Override
    public void handle(Event event) {
        LOG.info("Handling event " + event.getClass().getName());
    }

    @Override
    public Class<SampleParserVulnerabilityAttribute> getVulnerabilityAttributesClass() {
        return SampleParserVulnerabilityAttribute.class;
    }

    @Override
    public com.fortify.plugin.result.parser.Scan parseScan(final ScanData scanData) throws ScanParsingException, IOException {
        final ObjectReader r = JSONMAPPER.readerFor(Scan.class);
        try (final InputStream content = getScanInputStream(scanData)) {
            final Scan s = r.readValue(content);
            com.fortify.plugin.result.parser.Scan result = new com.fortify.plugin.result.parser.Scan();
            result.setScanDate(s.getScanDate());
            return result;
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
                v.setCustomAttributeValue(SampleParserVulnerabilityAttribute.FIELD1, f.getField());
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
