package com.thirdparty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fortify.plugin.PluginPublisher;
import com.fortify.plugin.event.Event;
import com.fortify.plugin.event.ScanSubmission;
import com.fortify.plugin.event.parser.AcceptScan;
import com.fortify.plugin.result.Vulnerability;
import com.fortify.plugin.spi.ParserPlugin;
import com.thirdparty.scan.Finding;
import com.thirdparty.scan.Scan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * (C) Copyright 2015,2016 Hewlett Packard Enterprise Development, L.P.
 */
public class EverythingParser implements ParserPlugin {
    private static final Logger LOG = LoggerFactory.getLogger(EverythingParser.class);
    private PluginPublisher publisher;

    private static final ObjectMapper JSONMAPPER = new ObjectMapper();

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void stop() {
        LOG.info("they see me runnin they stoppin");
        executor.shutdownNow();
    }

    @Override
    public void handle(Event event) {
        if (event instanceof ScanSubmission) {
            ScanSubmission scanSubmission = (ScanSubmission) event;
            ObjectReader r = JSONMAPPER.readerFor(Scan.class);
            final String location = scanSubmission.getLocation();
            LOG.info("Reading stream from location:" + location);

            // Simulation of location data stream
            InputStream content = new ByteArrayInputStream(new String("{ \"findings\" : [{ \"uniqueId\": 0, \"field\": \"test field\"}]}").getBytes());
            try {
                final Scan s = r.readValue(content);
                executor.submit(() -> {
                    try {
                        Thread.sleep(2500);
                        for (Finding f : s.getFindings()) {
                            Vulnerability v = new Vulnerability();
                            v.setCustomAttributeValue("Field", f.getField());
                            v.setInstanceId(f.getUniqueId());
                            v.setScanId(scanSubmission.getScanId());
                            publisher.send(v);
                        }
                        LOG.info("Processed " + scanSubmission.getScanId());
                    } catch (InterruptedException e) {
                        LOG.warn("Interrupted!");
                    }

                });
            } catch (IOException e) {
                LOG.error("FAILED because of my EX", e);
            } finally {
                try {
                    if (content != null) content.close();
                } catch (IOException e) {
                }
            }
        }
    }

    @Override
    public void setPublisher(PluginPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public boolean acceptScan(AcceptScan event) {
        return true;
    }

    @Override
    public com.fortify.plugin.result.Scan getScan() {
        com.fortify.plugin.result.Scan scan = new com.fortify.plugin.result.Scan();
        scan.setScanDate(new Date());
        scan.setEngineVersion("1");
        return scan;

    }
}
