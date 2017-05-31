package com.thirdparty.scan;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * (C) Copyright 2015,2016 Hewlett Packard Enterprise Development, L.P.
 */
@JsonSerialize
public class Scan {

    private Date scanDate;
    private String engineVersion;
    private Integer elapsed;
    private String buildServer;
    private List<Finding> findings = new LinkedList<>();

    @JsonSerialize(converter = DateSerializer.class)
    public Date getScanDate() {
        return scanDate;
    }

    @JsonDeserialize(converter = DateDeserializer.class)
    public void setScanDate(final Date scanDate) {
        this.scanDate = scanDate;
    }

    public String getEngineVersion() {
        return engineVersion;
    }

    public void setEngineVersion(final String engineVersion) {
        this.engineVersion = engineVersion;
    }

    public Integer getElapsed() {
        return elapsed;
    }

    public void setElapsed(final Integer elapsed) {
        this.elapsed = elapsed;
    }

    public String getBuildServer() {
        return buildServer;
    }

    public void setBuildServer(final String buildServer) {
        this.buildServer = buildServer;
    }

    public List<Finding> getFindings() {
        return findings;
    }

    public void setFindings(final List<Finding> findings) {
        this.findings = findings;
    }
}
