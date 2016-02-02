/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Feb 2, 2016
 * @author vlads
 */
package com.pyx4j.essentials.server;

import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transport.http.HTTPConduitConfigurer;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;

import com.pyx4j.commons.Consts;
import com.pyx4j.config.server.PropertiesConfiguration;
import com.pyx4j.config.server.ServerSideConfiguration;

public class PropertyBaseHTTPConduitConfigurer implements HTTPConduitConfigurer {

    private long connectionTimeout;

    private long receiveTimeout;

    public PropertyBaseHTTPConduitConfigurer(String connectionTimeoutName, int connectionTimeoutSecondsDefault, //
            String receiveTimeoutName, int receiveTimeoutSecondsDefault) {
        PropertiesConfiguration config = ServerSideConfiguration.instance().getConfigProperties();
        connectionTimeout = Consts.SEC2MSEC * config.getSecondsValue(connectionTimeoutName, connectionTimeoutSecondsDefault);
        receiveTimeout = Consts.SEC2MSEC * config.getSecondsValue(receiveTimeoutName, receiveTimeoutSecondsDefault);

    }

    @Override
    public void configure(String name, String address, HTTPConduit c) {
        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        httpClientPolicy.setConnectionTimeout(connectionTimeout);
        httpClientPolicy.setReceiveTimeout(receiveTimeout);
    }

}
