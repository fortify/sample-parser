package com.thirdparty.scan;

/**
 * (c) Copyright [2017] Micro Focus or one of its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
