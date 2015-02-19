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
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.pmc.Pmc;
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
        pmcDnsConfig.dnsNameDefault().setValue(PmcDNSUtils.getHostName(VistaDeployment.getBaseApplicationURL(pmc, application, true)));
        pmcDnsConfig.dnsNameIsActive().setValue(PmcDNSUtils.isDnsNameActiveForApplication(pmc, application));
        pmcDnsConfig.serverIPAddress().setValue(PmcDNSUtils.getDefaultIpAddressForApplication(application));

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

        if (dnsCustomerName == null) {
            return;
        }

        TaskRunner.runInOperationsNamespace(new Callable<Void>() {
            @Override
            public Void call() {
                PmcDnsName pmcDnsName = PmcDNSUtils.getPmcDnsAliasesForPmcAndApplication(pmc, application);
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

    private PmcDnsConfigTO setSiteAppDnsResolutionData(PmcDnsConfigTO pmcDnsConfig) {
        String customerCompleteDnsName = pmcDnsConfig.customerDnsName().getValue();
        String customerDnsName = getHostName(customerCompleteDnsName);

        if (customerDnsName != null) {
            pmcDnsConfig.customerDnsName().setValue(customerDnsName);
        }

        return pmcDnsConfig;
    }

    private PmcDnsConfigTO setResidentAppDnsResolutionData(PmcDnsConfigTO pmcDnsConfig) {
        String customerCompleteDnsName = pmcDnsConfig.customerDnsName().getValue();
        String customerDnsName;

        customerDnsName = getHostName(customerCompleteDnsName);

        if (customerDnsName == null) {
            return pmcDnsConfig;
        }

        // TODO Don't understand this part yet...
        if (customerDnsName.startsWith("my.")) {
            pmcDnsConfig.customerDnsName().setValue(customerDnsName);

            if (resolveDNS("www." + customerDnsName) != null) {
                pmcDnsConfig.customerDnsName().setValue("www." + customerDnsName);
            }
        } else if (customerDnsName.startsWith("www.my.")) {
            pmcDnsConfig.customerDnsName().setValue(customerDnsName);

            if (resolveDNS("my." + customerDnsName) != null) {
                pmcDnsConfig.customerDnsName().setValue("my." + customerDnsName);
            }
        } else {
            pmcDnsConfig.customerDnsName().setValue(customerDnsName);
        }

        return pmcDnsConfig;
    }

    private void setDnsResolutionDataForApplication(Pmc pmc, PmcDnsConfigTO pmcDnsConfig, VistaApplication application) {

        String customerCompleteDnsName = PmcDNSUtils.getCustomerDnsName(pmc, application);

        if (customerCompleteDnsName != null) {
            String customerDnsName = getHostName(customerCompleteDnsName);

            if (customerDnsName == null) {
                return;
            }

            pmcDnsConfig.customerDnsName().setValue(customerDnsName);

            String ipAddress = null;

            try {
                ipAddress = PmcDNSUtils.getJavaResolver().resolveHost(customerDnsName);
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

    private static boolean isApplicationSuspported(VistaApplication application) {
        if (!Arrays.asList(supportedApps).contains(application)) {
            log.warn("Application {} is not currently supported", application.name());
            return false;
        }
        return true;
    }

    private String resolveDNS(String dns) {
        return PmcDNSUtils.resolveDnsName(dns);
    }

    private String getHostName(String customerCompleteDnsName) {
        return PmcDNSUtils.getHostName(customerCompleteDnsName);
    }

}
