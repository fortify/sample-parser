package com.thirdparty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fortify.plugin.event.ScanParse;
import com.fortify.plugin.parser.ParserPlugin;
import com.fortify.plugin.queue.ResultQueue;
import com.fortify.plugin.result.Vulnerability;
import com.thirdparty.scan.Finding;
import com.thirdparty.scan.Scan;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * (C) Copyright 2015,2016 Hewlett Packard Enterprise Development, L.P.
 */
public class EverythingParser implements ParserPlugin {
    
    private static final ObjectMapper JSONMAPPER = new ObjectMapper();
    
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    @Override
    public void stop() {
         System.out.println("they see me runnin they stoppin");
        executor.shutdownNow();
    }

    @Override
    public boolean accept(ScanParse event, ResultQueue<Vulnerability> pipe) {
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
                            pipe.add(v);
                        }
                    } catch (InterruptedException e) {
                        System.out.println("Stopped!");
                        return;
                    }
    
                    System.out.println("Received file " +  event.getFileName() + " with id " + event.getScanId());
    
                    pipe.published();
                });
                return true;
            }
        } catch (IOException  e) {
            System.err.println("FAILED with EX");
            return false;
        } finally {
            try {
                if(event.getContent() != null) event.getContent().close();
            } catch (IOException e) { /*/*/ }
        }
    }
}
