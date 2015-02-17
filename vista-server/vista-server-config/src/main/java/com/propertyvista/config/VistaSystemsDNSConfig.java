/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 13, 2015
 * @author vlads
 */
package com.propertyvista.config;

public abstract class VistaSystemsDNSConfig {

    protected final AbstractVistaServerSideConfiguration config;

    protected VistaSystemsDNSConfig(AbstractVistaServerSideConfiguration config) {
        this.config = config;
    }

    public String getDnsServer() {
        return config.getConfigProperties().getValue("dns.dnsServer", "8.8.8.8");
    }

    public String getVistaSiteIP() {
        return config.getConfigProperties().getValue("dns.siteIP", "127.0.0.1");
    }

    public String getVistaResidentIP() {
        return config.getConfigProperties().getValue("dns.residentIP", "127.0.0.1");
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("configurationClass                : ").append(getClass().getName()).append("\n");
        b.append("dns.dnsServer                     : ").append(getDnsServer()).append("\n");
        return b.toString();
    }

}
