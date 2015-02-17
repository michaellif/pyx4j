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
package com.propertyvista.biz.system;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

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
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.domain.pmc.PmcDnsConfigTO;
import com.propertyvista.domain.pmc.PmcDnsName;
import com.propertyvista.domain.pmc.PmcDnsName.DnsNameTarget;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.server.TaskRunner;

public class CustomDNSManager {

    private final static Logger log = LoggerFactory.getLogger(CustomDNSManager.class);

    static final VistaApplication[] supportedApps = { VistaApplication.site, VistaApplication.resident };

    static final String IP_DOES_NOT_MATCH_VISTA_SERVER_ADDRESS = "Solved ip address does not match with Property Vista server ip address";

    static final String ERROR_RESOLVING_CUSTOMER_ADDRESS = "Error resolving customer ip address. Please, try again.";

    // Only resident and site are supported. TODO prospect
    public PmcDnsConfigTO getApplicationDnsConfig(Pmc pmc, VistaApplication application) {
        if (!isApplicationSuspported(application)) {
            return null;
        }

        PmcDnsConfigTO pmcDnsConfig = EntityFactory.create(PmcDnsConfigTO.class);
        pmcDnsConfig.dnsNameDefault().setValue(VistaDeployment.getBaseApplicationURL(pmc, application, true));
        pmcDnsConfig.dnsNameIsActive().setValue(getDnsNameIsActiveForApplication(pmc, application));
        pmcDnsConfig.serverIPAddress().setValue(getIpAddressForApplication(application));

        setDnsResolutionDataForApplication(pmc, pmcDnsConfig, application);

        return pmcDnsConfig;
    }

    public void updateApplicationDnsConfig(final Pmc pmc, final VistaApplication application, PmcDnsConfigTO dnsConfig) {
        if (!isApplicationSuspported(application)) {
            return;
        }

        if (application == VistaApplication.site) {
            dnsConfig = setSiteAppDnsResolutionData(dnsConfig);
        } else if (application == VistaApplication.resident) {
            dnsConfig = setResidentAppDnsResolutionData(dnsConfig);
        }

        final String dnsCustomerName = dnsConfig.customerDnsName().getValue();
        TaskRunner.runInOperationsNamespace(new Callable<Void>() {
            @Override
            public Void call() {
                PmcDnsName pmcDnsName = getPmcDnsAliasesForPmcAndApplication(pmc, application);
                pmcDnsName.pmc().set(pmc);
                pmcDnsName.dnsName().setValue(dnsCustomerName);
                pmcDnsName.enabled().setValue(Boolean.TRUE);
                pmcDnsName.target().setValue(DnsNameTarget.site);
                pmcDnsName.httpsEnabled().setValue(Boolean.FALSE); // TODO what here?? Read http/https in customerAddress?

                Persistence.service().persist(pmcDnsName);
                return null;
            }
        });

    }

    private PmcDnsConfigTO setResidentAppDnsResolutionData(PmcDnsConfigTO pmcDnsConfig) {
        String customerCompleteDnsName = pmcDnsConfig.customerDnsName().getValue();
        String customerDnsName;

        try {
            customerDnsName = getHostName(customerCompleteDnsName);
        } catch (Exception e) {
            log.error("Bad format for CustomerDNSName '{}', {}", customerCompleteDnsName, e.getMessage());
            return null;
        }

        if (customerDnsName.startsWith("my.")) {
            pmcDnsConfig.customerDnsName().setValue(customerDnsName);

            if (resolveDnsName("www." + customerDnsName) != null) {
                pmcDnsConfig.customerDnsName().setValue("www." + customerDnsName);
            }
        } else if (customerDnsName.startsWith("www.my.")) {
            pmcDnsConfig.customerDnsName().setValue(customerDnsName);

            if (resolveDnsName("my." + customerDnsName) != null) {
                pmcDnsConfig.customerDnsName().setValue("my." + customerDnsName);
            }
        } else {
            pmcDnsConfig.customerDnsName().setValue(customerDnsName);
        }

        return pmcDnsConfig;
    }

    private String resolveDnsName(String customerDnsName) {
        String ipAddress = null;
        try {
            ipAddress = getJavaResolverForTargetHost(customerDnsName).resolveHost();
        } catch (UnknownHostException e) {
            log.error("UnknownHost customerDnsName '{}', {}", customerDnsName, e.getMessage());
        } catch (IOException | InterruptedException e) {
            log.error("Error resolving customerDnsName '{}', {}", customerDnsName, e.getMessage());
        }

        return ipAddress;
    }

    private PmcDnsConfigTO setSiteAppDnsResolutionData(PmcDnsConfigTO pmcDnsConfig) {
        String customerCompleteDnsName = pmcDnsConfig.customerDnsName().getValue();
        String customerDnsName;

        try {
            customerDnsName = getHostName(customerCompleteDnsName);
        } catch (Exception e) {
            log.error("Bad format for CustomerDNSName '{}'", customerCompleteDnsName, e);
            return null;
        }

        pmcDnsConfig.customerDnsName().setValue(customerDnsName);

        return pmcDnsConfig;
    }

    private static String getIpAddressForApplication(VistaApplication application) {
        if (application == VistaApplication.site) {
            return ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).getVistaSystemDNSConfig().getVistaSiteIP();
        } else if (application == VistaApplication.resident) {
            return ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).getVistaSystemDNSConfig().getVistaResidentIP();
        }

        return null;
    }

    private void setDnsResolutionDataForApplication(Pmc pmc, PmcDnsConfigTO pmcDnsConfig, VistaApplication application) {

        String customerCompleteDnsName = getCustomerDnsName(pmc, application);

        if (customerCompleteDnsName != null) {
            String customerDnsName;

            try {
                customerDnsName = getHostName(customerCompleteDnsName);
            } catch (Exception e) {
                log.error("Bad format for CustomerDNSName", e);
                return;
            }

            pmcDnsConfig.customerDnsName().setValue(customerDnsName);

            String ipAddress = null;

            try {
                ipAddress = getJavaResolverForTargetHost(customerDnsName).resolveHost();
            } catch (UnknownHostException e) {
//                pmcDnsConfig.dnsResolutionMessage().setValue(getUnknownHostExceptionTypeMessage(e));
                pmcDnsConfig.dnsResolutionMessage().setValue(e.getMessage());
            } catch (IOException | InterruptedException e) {
                pmcDnsConfig.dnsResolutionMessage().setValue(ERROR_RESOLVING_CUSTOMER_ADDRESS);
                log.error("Error resolving customerDnsName", e);
            }

            if (ipAddress == null) {
                pmcDnsConfig.dnsResolved().setValue(Boolean.FALSE);
            } else if (ipAddress.equalsIgnoreCase(pmcDnsConfig.serverIPAddress().getValue())) {
                pmcDnsConfig.dnsResolved().setValue(Boolean.TRUE);
            } else {
                pmcDnsConfig.dnsResolved().setValue(Boolean.FALSE);
                pmcDnsConfig.dnsResolutionMessage().setValue(IP_DOES_NOT_MATCH_VISTA_SERVER_ADDRESS);
            }
        }
    }

    private static JavaResolver getJavaResolverForTargetHost(String targetHost) {
        JavaResolver javaDnsResolver = new JavaResolver();
        javaDnsResolver.setTargetHost(targetHost);
        List<String> dnsServers = new ArrayList<String>();
        dnsServers.add(ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).getVistaSystemDNSConfig().getDnsServer());
        javaDnsResolver.setDnsServers(dnsServers);
        return javaDnsResolver;
    }

    private String getHostName(String customerDnsName) throws URISyntaxException, URIException, NullPointerException {
        URI uri = new URI(customerDnsName, false);
        String domain = uri.getHost();
        return domain;
//        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }

    private String getCustomerDnsName(Pmc pmc, VistaApplication application) {
        for (PmcDnsName dnsName : pmc.dnsNameAliases()) {
            if (dnsName.target().getValue().name().equalsIgnoreCase(application.name())) {
                return dnsName.dnsName().getValue();
            }
        }

        return null;
    }

    private PmcDnsName getPmcDnsAliasesForPmcAndApplication(Pmc pmc, VistaApplication application) {
        EntityQueryCriteria<PmcDnsName> criteria = EntityQueryCriteria.create(PmcDnsName.class);
        criteria.eq(criteria.proto().enabled(), Boolean.TRUE);
        criteria.eq(criteria.proto().dnsName(), pmc.dnsName());
        criteria.eq(criteria.proto().pmc().status(), PmcStatus.Active);

        PmcDnsName pmcDns = Persistence.service().retrieve(criteria);

        if (pmcDns != null) {
            return pmcDns;
        } else {
            PmcDnsName newPmcDnsName = EntityFactory.create(PmcDnsName.class);
            return newPmcDnsName;
        }

    }

    // Search in dns names aliases for first record with same application
    private static Boolean getDnsNameIsActiveForApplication(Pmc pmc, VistaApplication application) {

        for (PmcDnsName dnsName : pmc.dnsNameAliases()) {
            if (dnsName.target().getValue().name().equalsIgnoreCase(application.name())) {
                if (dnsName.enabled().getValue()) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean isApplicationSuspported(VistaApplication application) {
        if (!Arrays.asList(supportedApps).contains(application)) {
            log.warn("Application {} is not currently supported", application.name());
            return false;
        }

        return true;
    }

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

}
