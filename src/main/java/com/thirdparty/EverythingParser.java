package com.thirdparty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fortify.plugin.api.ScanData;
import com.fortify.plugin.event.Event;
import com.fortify.plugin.result.Status;
import com.fortify.plugin.result.parser.VulnerabilityBuilder;
import com.fortify.plugin.result.parser.VulnerabilityHandler;
import com.fortify.plugin.spi.ParserPlugin;
import com.thirdparty.scan.Finding;
import com.thirdparty.scan.Scan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * (C) Copyright 2015,2016 Hewlett Packard Enterprise Development, L.P.
 */
public class EverythingParser implements ParserPlugin {
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
    public com.fortify.plugin.result.parser.Scan parseScan(final ScanData scanData) throws Exception {
        final ObjectReader r = JSONMAPPER.readerFor(Scan.class);
        try (final InputStream content = getScanInputStream(scanData)) {
            final Scan s = r.readValue(content);
            com.fortify.plugin.result.parser.Scan result = new com.fortify.plugin.result.parser.Scan();
            result.setScanDate(s.getScanDate());
            return result;
        }
    }

    @Override
    public void parseVulnerabilities(final ScanData scanData, final VulnerabilityHandler vh) throws Exception {
        final ObjectReader r = JSONMAPPER.readerFor(Scan.class);
        try (final InputStream content = getScanInputStream(scanData)) {
            final Scan s = r.readValue(content);
            int counter = 0;
            for (Finding f : s.getFindings()) {
                counter += 1;
                VulnerabilityBuilder v = vh.startVulnerability(f.getUniqueId());
                // custom field
                v.setCustomAttributeValue("Field", f.getField());
                v.setCategory(String.format("Sample issue %d", counter));
                v.setFileName(String.format("vulnerable_file_%d_%s.bin", counter, f.getUniqueId()));
                v.setVulnerabilityAbstract(String.format("Abstract of issue %d", counter));
                v.completeVulnerability();
            }
        }
    }

    private InputStream getScanInputStream(final ScanData scanData) throws IOException {
        return scanData.getInputStream(x -> x.endsWith(".json"));
    }
}
