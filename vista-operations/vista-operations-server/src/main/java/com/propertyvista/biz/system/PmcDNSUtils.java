/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 18, 2015
 * @author ernestog
 */
package com.propertyvista.biz.system;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.system.DNSResolver.JavaResolver;
import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.domain.pmc.PmcDnsName;
import com.propertyvista.domain.pmc.PmcDnsName.DnsNameTarget;
import com.propertyvista.domain.security.common.VistaApplication;

public class PmcDNSUtils {

    private final static Logger log = LoggerFactory.getLogger(PmcDNSUtils.class);

    // Function to customize message to be shown at PmcDnsConfigTO.dnsResolutionMessage
    private static String getUnknownHostExceptionTypeMessage(UnknownHostException e) {
        String exMessage = e.getMessage();
        if (exMessage.contains("no A record")) {
            return "TYPE_NOT_FOUND: No A records";
        }

        if (exMessage.contains("Cannot lookup host")) {
            return "TYPE_UNRECOVORABLE: Cannot lookup host";
        }

        if (exMessage.contains("Temporary failure")) {
            return "TRY_AGAIN: Temporary failure to lookup host";
        }

        if (exMessage.contains("Unknown error")) {
            return "UNKNOWN ERROR";
        }

        return "HOST_NOT_FOUND";
    }

    // Search in dns names aliases for first record with same application
    public static Boolean isDnsNameActiveForApplication(Pmc pmc, VistaApplication application) {

        for (PmcDnsName dnsName : pmc.dnsNameAliases()) {
            if (dnsName.target().getValue().name().equalsIgnoreCase(application.name())) {
                if (dnsName.enabled().getValue()) {
                    return true;
                }
            }
        }

        return false;
    }

    public static PmcDnsName getPmcDnsAliasesForPmcAndApplication(Pmc pmc, VistaApplication application) {
        EntityQueryCriteria<PmcDnsName> criteria = EntityQueryCriteria.create(PmcDnsName.class);
        criteria.eq(criteria.proto().enabled(), Boolean.TRUE);
        criteria.eq(criteria.proto().target(), application == VistaApplication.site ? DnsNameTarget.site : DnsNameTarget.portal);
        criteria.eq(criteria.proto().pmc().status(), PmcStatus.Active);

        PmcDnsName pmcDns = Persistence.service().retrieve(criteria);

        if (pmcDns != null) {
            return pmcDns;
        } else {
            PmcDnsName newPmcDnsName = EntityFactory.create(PmcDnsName.class);
            return newPmcDnsName;
        }
    }

    public static JavaResolver getJavaResolver() {
        List<String> dnsServers = new ArrayList<String>();
        dnsServers.add(ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).getVistaSystemDNSConfig().getDnsServer());
        return new JavaResolver(dnsServers);
    }

    public static String getDefaultIpAddressForApplication(VistaApplication application) {
        if (application == VistaApplication.site) {
            return ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).getVistaSystemDNSConfig().getVistaSiteIP();
        } else if (application == VistaApplication.resident) {
            return ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).getVistaSystemDNSConfig().getVistaResidentIP();
        }

        return null;
    }

    public static String resolveDnsName(String customerDnsName) {
        String ipAddress = null;
        try {
            ipAddress = getJavaResolver().resolveHost(customerDnsName);
        } catch (UnknownHostException e) {
            log.error("UnknownHost customerDnsName '{}', {}", customerDnsName, e.getMessage());
        } catch (IOException | InterruptedException e) {
            log.error("Error resolving customerDnsName '{}', {}", customerDnsName, e.getMessage());
        }

        return ipAddress;
    }

    public static String getHostName(String customerDnsName) {

        if (customerDnsName == null) {
            return null;
        }

        if (!customerDnsName.startsWith("http")) {
            customerDnsName = "http://" + customerDnsName;
        }

        URI uri;
        try {
            uri = new URI(customerDnsName, false);
            return uri.getHost();
        } catch (URIException e) {
            log.error("Bad format for CustomerDNSName '{}', {}", customerDnsName, e.getMessage());
            return null;
        }

//        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }

    public static String getCustomerDnsName(Pmc pmc, VistaApplication application) {
        for (PmcDnsName dnsName : pmc.dnsNameAliases()) {
            if (dnsName.target().getValue().name().equalsIgnoreCase(application.name())) {
                return dnsName.dnsName().getValue();
            }
        }

        return null;
    }

}
