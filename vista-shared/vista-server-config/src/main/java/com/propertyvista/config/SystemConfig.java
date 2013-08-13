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
package com.propertyvista.config;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.essentials.j2se.HostConfig;

public class SystemConfig extends HostConfig {

    private final static Logger log = LoggerFactory.getLogger(SystemConfig.class);

    private static List<String> noCaledonProxyHost = Arrays.asList("dev", "dev.birchwoodsoftwaregroup.com", "prod03a", "prod03b", "qa",
            "qa.birchwoodsoftwaregroup.com");

    protected ProxyConfig caledonProxy;

    private static SystemConfig instance;

    public static SystemConfig instance() {
        if (instance == null) {
            instance = new SystemConfig();
        }
        return instance;
    }

    public SystemConfig() {
        configure();
    }

    @Override
    protected void configure() {
        super.configure();

        // Set this when testing with http://www.charlesproxy.com/
        //setProxy("127.0.0.1", 9999);

        if (this.getProxyConfig() != null) {
            log.info("default proxy defined {}:{} ", this.getProxyConfig().getHost(), this.getProxyConfig().getPort());
        } else {
            log.info("default proxy is not defined");
        }
    }

    @Override
    protected void configure(String hostName) {
        log.info("SystemConfig for host '{}'", hostName);
        if (!noCaledonProxyHost.contains(hostName)) {
            setVistaCaledonProxy();
        }
    }

    private void setVistaCaledonProxy() {
        setCaledonProxy("dev.birchwoodsoftwaregroup.com", 8888, "sys-dev-env", "he8rEcr9");
        log.info("caledon proxy defined {}:{} ", this.getCaledonProxy().getHost(), this.getCaledonProxy().getPort());
    }

    private void setCaledonProxy(String proxyHost, int proxyPort, String user, String password) {
        this.caledonProxy = new ProxyConfig(proxyHost, proxyPort, user, password);
    }

    public ProxyConfig getCaledonProxy() {
        return caledonProxy;
    }

}
