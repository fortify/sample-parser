package com.thirdparty.scan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.Random;
import java.util.UUID;

/**
 * (C) Copyright 2015,2016 Hewlett Packard Enterprise Development, L.P.
 */
public class ScanGenerator {
    
    public static void main(String[] args) throws NoSuchAlgorithmException, JsonProcessingException {
        Scan s = new Scan();
        s.setFindings(new LinkedList<>());
        Random r = SecureRandom.getInstanceStrong();
        
        for(int i = 0; i < r.nextInt(8) + 2; i++) {
            Finding f = new Finding();
            f.setField("get" + String.valueOf((char) (r.nextInt('Z' - 'A' + 1) + 'A')));
            f.setUniqueId(UUID.randomUUID().toString());
            s.getFindings().add(f);
        }
        System.out.println(new ObjectMapper().writerFor(Scan.class).writeValueAsString(s));
        
    }
}
