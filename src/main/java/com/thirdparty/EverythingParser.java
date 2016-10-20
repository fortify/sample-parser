package com.thirdparty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fortify.plugin.event.ScanParse;
import com.fortify.plugin.result.Vulnerability;
import com.fortify.plugin.spi.ParserPlugin;
import com.fortify.plugin.spi.VulnerabilityPublisher;
import com.thirdparty.scan.Finding;
import com.thirdparty.scan.Scan;

import java.io.IOException;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * (C) Copyright 2015,2016 Hewlett Packard Enterprise Development, L.P.
 */
public class EverythingParser implements ParserPlugin {
    
    private static final ObjectMapper JSONMAPPER = new ObjectMapper();
    private final VulnerabilityPublisher publisher;
    
    public EverythingParser() {
        ServiceLoader<VulnerabilityPublisher> loader = ServiceLoader.load(VulnerabilityPublisher.class);
        Iterator<VulnerabilityPublisher> implementations = loader.iterator();
        publisher = implementations.next();
        if (publisher == null || implementations.hasNext()) {
            throw new IllegalStateException("None or multiple implementations found for " + ParserPlugin.class.getCanonicalName());
        }
    }
    
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    @Override
    public void stop() {
         System.out.println("they see me runnin they stoppin");
        executor.shutdownNow();
    }

    @Override
    public boolean accept(ScanParse event) {
        System.out.println("Received file " +  event.getFileName() + " scan id " + event.getScanId());
        ObjectReader r = JSONMAPPER.readerFor(Scan.class);
        try {
            final Scan s = r.readValue(event.getContent());
            if(s == null) {
                System.out.println("FAILED with NULL");
                return false;
            }
            else {
                executor.submit(() -> {
                    try {
                        Thread.sleep(2500);
                        for(Finding f : s.getFindings()) {
                            Vulnerability v = new Vulnerability();
                            v.setField(f.getField());
                            v.setInstanceId(f.getUniqueId());
                            v.setScanId(event.getScanId());
                            publisher.publish(v);
                        }
                        System.out.println("Processed " + event.getScanId());
                    } catch (InterruptedException e) {
                        System.out.println("Stopped!");
                        return;
                    }
    
                });
                return true;
            }
        } catch (IOException  e) {
            System.err.println("FAILED because of my EX");
            return false;
        } finally {
            try {
                if(event.getContent() != null) event.getContent().close();
            } catch (IOException e) { /*/*/ }
        }
    }
}
