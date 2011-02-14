/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-14
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.payment;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.essentials.j2se.HostConfig;

public class SystemConfig extends HostConfig {

    private final static Logger log = LoggerFactory.getLogger(SystemConfig.class);

    private static List<String> doxHost = Arrays.asList("vlads-i7");

    public SystemConfig() {
        configure();
    }

    @Override
    public void configure() {
        super.configure();
        if (CommonsStringUtils.isStringSet(this.getProxyHost())) {
            log.info("proxy defined {} ", this.getProxyHost());
        }
    }

    @Override
    public void configure(String hostName) {
        if (doxHost.contains(hostName)) {
            setDoxProxy();
        }
    }

    public void setDoxProxy() {
        setProxy("torproxy1", "8080");
    }

}
