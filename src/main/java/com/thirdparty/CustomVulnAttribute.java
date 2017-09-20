package com.thirdparty;

/**
 * (c) Copyright [2017] EntIT Software LLC
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


/**
 * <P>All custom vulnerability attributes must be defined in this class and must implement {@link com.fortify.plugin.spi.VulnerabilityAttribute} interface .
 * <BR>For all other attributes than custom ones which this parser needs see {@link VulnAttribute}
 */
public enum CustomVulnAttribute implements com.fortify.plugin.spi.VulnerabilityAttribute {

    // Custom attributes must have their types defined:
    UNIQUE_ID(VulnAttribute.UNIQUE_ID.attrName(), AttrType.STRING),
    CATEGORY_ID(VulnAttribute.CATEGORY_ID.attrName(), AttrType.STRING),
    ARTIFACT(VulnAttribute.ARTIFACT.attrName(), AttrType.STRING),
    COMMENT(VulnAttribute.COMMENT.attrName(), AttrType.LONG_STRING),
    DESCRIPTION(VulnAttribute.DESCRIPTION.attrName(), AttrType.LONG_STRING),
    BUILD_NUMBER(VulnAttribute.BUILD_NUMBER.attrName(), AttrType.STRING),
    CUSTOM_STATUS(VulnAttribute.CUSTOM_STATUS.attrName(), AttrType.STRING),
    LAST_CHANGE_DATE(VulnAttribute.LAST_CHANGE_DATE.attrName(), AttrType.DATE),
    ARTIFACT_BUILD_DATE(VulnAttribute.ARTIFACT_BUILD_DATE.attrName(), AttrType.DATE),
    TEXT_BASE64(VulnAttribute.TEXT_BASE64.attrName(), AttrType.LONG_STRING),
    ;

    private final AttrType attributeType;
    private final String attributeName;

    CustomVulnAttribute(final String attributeName, final AttrType attributeType) {
        this.attributeType = attributeType;
        this.attributeName = attributeName;
    }

    @Override
    public String attributeName() {
        return attributeName;
    }

    @Override
    public AttrType attributeType() {
        return attributeType;
    }
}
