package com.thirdparty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fortify.plugin.event.Event;
import com.fortify.plugin.result.Status;
import com.fortify.plugin.result.parser.VulnerabilityBuilder;
import com.fortify.plugin.result.parser.VulnerabilityHandler;
import com.fortify.plugin.spi.ParserPlugin;
import com.thirdparty.scan.Finding;
import com.thirdparty.scan.Scan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Date;

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
    public void parseVulnerabilities(InputStream is, VulnerabilityHandler vh) throws Exception {
        ObjectReader r = JSONMAPPER.readerFor(Scan.class);
        // Simulation of location data stream
        try (InputStream content = EverythingParser.class.getResourceAsStream("/examples/sample-scan.json")) {
            final Scan s = r.readValue(content);

            for (Finding f : s.getFindings()) {
                VulnerabilityBuilder v = vh.startVulnerability(f.getUniqueId());
                v.setCustomAttributeValue("Field", f.getField());
                v.completeVulnerability();
            }
        }
    }

    @Override
    public com.fortify.plugin.result.parser.Scan parseScan(InputStream is) throws Exception {
        com.fortify.plugin.result.parser.Scan result = new com.fortify.plugin.result.parser.Scan();
        result.setScanDate(new Date());
        return result;
    }
}
