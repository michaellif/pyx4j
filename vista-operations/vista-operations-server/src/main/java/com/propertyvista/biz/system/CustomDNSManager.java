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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Key;
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

        PmcDnsConfigTO pmcDnsConfig = getDnsResolutionInformationForApplication(pmc, application);
        return pmcDnsConfig;
    }

    public void updateApplicationDnsConfig(Pmc pmc, final VistaApplication application, PmcDnsConfigTO dnsConfig) {
        if (!isApplicationSuspported(application)) {
            return;
        }

        String customerCompleteDnsName = dnsConfig.customerDnsName().getValue();
        String customerDnsName = getHostName(customerCompleteDnsName);

        if (customerDnsName == null) {
            return;
        }

        dnsConfig.customerDnsName().setValue(customerDnsName);

        if (application == VistaApplication.site) {
            updateSiteAppDnsResolutionData(pmc, dnsConfig);
        } else if (application == VistaApplication.resident) {
            updateResidentAppDnsResolutionData(pmc, dnsConfig);
        }
    }

    private static void updateSiteAppDnsResolutionData(Pmc pmc, final PmcDnsConfigTO dnsConfig) {
        removePmcDnsAliasesForPmcAndApplication(pmc, VistaApplication.site);
        addDnsAliasForPmcAndApplication(pmc, VistaApplication.site, dnsConfig);
    }

    private static void updateResidentAppDnsResolutionData(Pmc pmc, final PmcDnsConfigTO dnsConfig) {
        String customerDnsName = dnsConfig.customerDnsName().getValue();

        // Add customer dnsAlias for Resident Portal
        removePmcDnsAliasesForPmcAndApplication(pmc, VistaApplication.resident);
        addDnsAliasForPmcAndApplication(pmc, VistaApplication.resident, dnsConfig);

        // If case of "my." or "www.my." check for adding alternative dns alias if they are also resolved
        if (customerDnsName.startsWith("my.")) {
            String alternativeDnsName = "www." + customerDnsName;
            if (resolveDNS(alternativeDnsName) != null) {
                dnsConfig.customerDnsName().setValue(alternativeDnsName);
                addDnsAliasForPmcAndApplication(pmc, VistaApplication.resident, dnsConfig);
            }
        } else if (customerDnsName.startsWith("www.my.")) {
            String alternativeDnsName = customerDnsName.replaceFirst("www.my.", "my.");
            if (resolveDNS(alternativeDnsName) != null) {
                dnsConfig.customerDnsName().setValue(alternativeDnsName);
                addDnsAliasForPmcAndApplication(pmc, VistaApplication.resident, dnsConfig);
            }
        }

    }

    // TODO Move method to PmcFacade
    private static void addDnsAliasForPmcAndApplication(Pmc pmc, final VistaApplication application, final PmcDnsConfigTO dnsConfig) {
        final Key pmcPrimaryKey = pmc.getPrimaryKey();
        final DnsNameTarget targetApp = PmcDNSUtils.getDnsNameTargetByVistaApplication(application);

        TaskRunner.runInOperationsNamespace(new Callable<Void>() {
            @Override
            public Void call() {
                Pmc pmc = Persistence.service().retrieve(Pmc.class, pmcPrimaryKey);
                Persistence.service().persist(pmc);
                PmcDnsName pmcDnsName = EntityFactory.create(PmcDnsName.class);
                pmcDnsName.dnsName().setValue(dnsConfig.customerDnsName().getValue());
                pmcDnsName.enabled().setValue(isDnsSolvedAndMatch(dnsConfig.customerDnsName().getValue(), application));
                pmcDnsName.target().setValue(targetApp);
                pmcDnsName.httpsEnabled().setValue(Boolean.FALSE); // TODO what here?? Read http/https in customerAddress?
                pmcDnsName.pmc().set(pmc);
                pmc.dnsNameAliases().add(pmcDnsName);
                Persistence.service().persist(pmc);
                return null;
            }
        });
    }

    // TODO Move method to PmcFacade
    private static void removePmcDnsAliasesForPmcAndApplication(Pmc targetPmc, final VistaApplication application) {
        final Key pmcPrimaryKey = targetPmc.getPrimaryKey();
        final DnsNameTarget app = PmcDNSUtils.getDnsNameTargetByVistaApplication(application);
        TaskRunner.runInOperationsNamespace(new Callable<Void>() {
            @Override
            public Void call() {
                Pmc pmc = Persistence.service().retrieve(Pmc.class, pmcPrimaryKey);
                List<PmcDnsName> dnsNameAliases = new ArrayList<PmcDnsName>();

                for (PmcDnsName currentDnsName : pmc.dnsNameAliases()) {
                    if (!currentDnsName.target().getValue().equals(app)) {
                        dnsNameAliases.add(currentDnsName);
                    }
                }

                pmc.dnsNameAliases().clear();
                pmc.dnsNameAliases().addAll(dnsNameAliases);
                Persistence.service().persist(pmc);
                return null;
            }
        });
    }

    private PmcDnsConfigTO getDnsResolutionInformationForApplication(Pmc pmc, VistaApplication application) {

        PmcDnsConfigTO pmcDnsConfig = EntityFactory.create(PmcDnsConfigTO.class);

        pmcDnsConfig.dnsNameIsActive().setValue(PmcDNSUtils.isDnsNameActiveForApplication(pmc, application));
        pmcDnsConfig.serverIPAddress().setValue(PmcDNSUtils.getDefaultIpAddressForApplication(application));

        String customerCompleteDnsName = PmcDNSUtils.getCustomerDnsName(pmc, application);

        if (customerCompleteDnsName != null) {
            String customerDnsName = getHostName(customerCompleteDnsName);

            if (customerDnsName == null) {
                pmcDnsConfig.dnsResolved().setValue(Boolean.FALSE);
                return null;
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

        if (pmcDnsConfig.dnsNameIsActive().getValue()) {
            pmcDnsConfig.dnsNameDefault().setValue(pmcDnsConfig.customerDnsName().getValue());
        } else {
            pmcDnsConfig.dnsNameDefault().setValue(PmcDNSUtils.getHostName(VistaDeployment.getBaseApplicationURL(pmc, application, true)));
        }

        return pmcDnsConfig;
    }

    private static boolean isApplicationSuspported(VistaApplication application) {
        if (!Arrays.asList(supportedApps).contains(application)) {
            log.warn("Application {} is not currently supported", application.name());
            return false;
        }
        return true;
    }

    private static boolean isDnsSolvedAndMatch(String dns, VistaApplication application) {
        String ipAddress = resolveDNS(dns);

        if (ipAddress != null && ipAddress.equalsIgnoreCase(PmcDNSUtils.getDefaultIpAddressForApplication(application))) {
            return true;
        } else {
            return false;
        }
    }

    private static String resolveDNS(String dns) {
        return PmcDNSUtils.resolveDnsName(dns);
    }

    private String getHostName(String customerCompleteDnsName) {
        return PmcDNSUtils.getHostName(customerCompleteDnsName);
    }

}
