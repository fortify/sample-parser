package com.thirdparty.scan;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.math.BigDecimal;
import java.util.Date;

/**
 * (C) Copyright 2015,2016 Hewlett Packard Enterprise Development, L.P.
 */
@JsonSerialize
public class Finding {

    // mandatory attributes
    private String uniqueId;

    // builtin attributes
    private String category;
    private String fileName;
    private String vulnerabilityAbstract;
    private Integer lineNumber;
    private Float confidence;
    private String friority;

    // custom attributes
    private String buildServer;
    private String artifact;
    private String text1;
    private String text2;
    private BigDecimal ratio;
    private BigDecimal buildNumber;
    private Date lastChangeDate;
    private Date artifactBuildDate;


    public String getUniqueId() {
        return uniqueId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(final String category) {
        this.category = category;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    public String getVulnerabilityAbstract() {
        return vulnerabilityAbstract;
    }

    public void setVulnerabilityAbstract(final String vulnerabilityAbstract) {
        this.vulnerabilityAbstract = vulnerabilityAbstract;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(final Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public Float getConfidence() {
        return confidence;
    }

    public void setConfidence(final Float confidence) { this.confidence = confidence; }

    public String getFriority() { return friority; }

    public void setFriority(final String friority) { this.friority = friority; }

    public void setUniqueId(final String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getBuildServer() {
        return buildServer;
    }

    public void setBuildServer(final String buildServer) {
        this.buildServer = buildServer;
    }

    public String getArtifact() {
        return artifact;
    }

    public void setArtifact(final String artifact) {
        this.artifact = artifact;
    }

    public String getText1() {
        return text1;
    }

    public void setText1(final String text1) {
        this.text1 = text1;
    }

    public String getText2() {
        return text2;
    }

    public void setText2(final String text2) {
        this.text2 = text2;
    }

    public BigDecimal getRatio() {
        return ratio;
    }

    public void setRatio(final BigDecimal ratio) {
        this.ratio = ratio;
    }

    public BigDecimal getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(final BigDecimal buildNumber) {
        this.buildNumber = buildNumber;
    }

    @JsonSerialize(converter = DateSerializer.class)
    public Date getLastChangeDate() {
        return lastChangeDate;
    }

    @JsonDeserialize(converter = DateDeserializer.class)
    public void setLastChangeDate(final Date lastChangeDate) {
        this.lastChangeDate = lastChangeDate;
    }

    @JsonSerialize(converter = DateSerializer.class)
    public Date getArtifactBuildDate() {
        return artifactBuildDate;
    }

    @JsonDeserialize(converter = DateDeserializer.class)
    public void setArtifactBuildDate(final Date artifactBuildDate) {
        this.artifactBuildDate = artifactBuildDate;
    }
}
