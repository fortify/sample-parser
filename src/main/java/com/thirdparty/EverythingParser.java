package com.thirdparty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fortify.plugin.event.ScanSubmission;
import com.fortify.plugin.result.Vulnerability;
import com.fortify.plugin.spi.ParserPlugin;
import com.fortify.plugin.spi.VulnerabilityPublisher;
import com.thirdparty.scan.Finding;
import com.thirdparty.scan.Scan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * (C) Copyright 2015,2016 Hewlett Packard Enterprise Development, L.P.
 */
public class EverythingParser implements ParserPlugin {
    private static final Logger LOG = LoggerFactory.getLogger(EverythingParser.class);

    private static final ObjectMapper JSONMAPPER = new ObjectMapper();
    private final VulnerabilityPublisher publisher;

    public EverythingParser() {
        ServiceLoader<VulnerabilityPublisher> loader = ServiceLoader.load(VulnerabilityPublisher.class, EverythingParser.class.getClassLoader());
        Iterator<VulnerabilityPublisher> implementations = loader.iterator();
        publisher = implementations.next();
        if (publisher == null || implementations.hasNext()) {
            throw new IllegalStateException("None or multiple implementations found for " + ParserPlugin.class.getCanonicalName());
        }
    }

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void stop() {
        LOG.info("they see me runnin they stoppin");
        executor.shutdownNow();
    }

    @Override
    public boolean accept(ScanSubmission event) {
        ObjectReader r = JSONMAPPER.readerFor(Scan.class);
        final String location = event.getLocation();
        LOG.info("Reading stream from location:" + location);

        // Simulation of location data stream
        InputStream content = new ByteArrayInputStream(new String("{ \"findings\" : [{ \"uniqueId\": 0, \"field\": \"test field\"}]}").getBytes());
        try {
            final Scan s = r.readValue(content);
            if(s == null) {
                LOG.error("FAILED with NULL");
                return false;
            }
            else {
                executor.submit(() -> {
                    try {
                        Thread.sleep(2500);
                        for(Finding f : s.getFindings()) {
                            Vulnerability v = new Vulnerability();
                            v.setCustomAttributeValue("Field", f.getField());
                            v.setInstanceId(f.getUniqueId());
                            v.setScanId(event.getScanId());
                            publisher.publish(v);
                        }
                        LOG.info("Processed " + event.getScanId());
                    } catch (InterruptedException e) {
                        LOG.warn("Interrupted!");
                        return;
                    }

                });
                return true;
            }
        } catch (IOException  e) {
            LOG.error("FAILED because of my EX", e);
            return false;
        } finally {
            try {
                if(content != null) content.close();
            } catch (IOException e) { }
        }
    }
}
