package com.thirdparty;

import com.fortify.plugin.parser.ParserPlugin;
import com.fortify.plugin.parser.ParserPluginFactory;

/**
 * (C) Copyright 2015,2016 Hewlett Packard Enterprise Development, L.P.
 */
public class Factory implements ParserPluginFactory {
    @Override
    public ParserPlugin getInstance() {
        return new EverythingParser();
    }
}
