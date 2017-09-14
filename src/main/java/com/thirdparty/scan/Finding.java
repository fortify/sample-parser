package com.thirdparty.scan;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fortify.plugin.api.BasicVulnerabilityBuilder;

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
    private Float impact;
    private BasicVulnerabilityBuilder.Priority priority;

    // custom attributes
    private String categoryId;
    private String artifact;
    private String description;
    private String comment;
    private String buildNumber;
    private String customStatus;
    private Date lastChangeDate;
    private Date artifactBuildDate;
    private String text64;

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

    public Float getImpact() {
        return impact;
    }

    public void setImpact(final Float impact) { this.impact = impact; }

    public void setUniqueId(final String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public BasicVulnerabilityBuilder.Priority getPriority() { return priority; }

    public void setPriority(final BasicVulnerabilityBuilder.Priority priority) { this.priority = priority; }

    public String getCategoryId() { return categoryId; }

    public void setCategoryId(final String categoryId) { this.categoryId = categoryId; }

    public String getArtifact() {
        return artifact;
    }

    public void setArtifact(final String artifact) {
        this.artifact = artifact;
    }

    public String getDescription() { return description; }

    public void setDescription(final String description) { this.description = description; }

    public String getComment() { return comment; }

    public void setComment(final String comment) { this.comment = comment; }

    public String getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(final String buildNumber) {
        this.buildNumber = buildNumber;
    }

    public String getCustomStatus() { return customStatus; }

    public void setCustomStatus(final String customStatus) { this.customStatus = customStatus; }


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

    public String getText64() {
        return text64;
    }

    public void setText64(String text64) {
        this.text64 = text64;
    }
}
