/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-20
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.tenant.insurance;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

import com.cfcprograms.api.CFCAPI;
import com.cfcprograms.api.CFCAPISoap;
import com.cfcprograms.api.ObjectFactory;
import com.cfcprograms.api.Result;
import com.cfcprograms.api.SimpleClient;
import com.cfcprograms.api.SimpleClientResponse;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.j2se.J2SEServiceConnector;
import com.pyx4j.essentials.j2se.J2SEServiceConnector.Credentials;

import com.propertyvista.biz.tenant.insurance.tenantsure.apiadapters.TenantSureTenantAdapter;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSureClient;
import com.propertyvista.domain.tenant.lease.Tenant;

/** This is an adapter class for CFC API */
class CfcApiCleint {
    private CFCAPI getApi() {
        try {
            if (VistaDeployment.isVistaProduction()) {
                return new CFCAPI(new URL("https://api.cfcprograms.com/cfc_api.asmx"), new QName("http://api.cfcprograms.com/"));
            } else {
                return new CFCAPI(new URL("http://testapi.cfcprograms.com/cfc_api.asmx"), new QName("http://api.cfcprograms.com/"));
            }
        } catch (MalformedURLException e) {
            throw new Error(e);
        }
    }

    private static Credentials getCredentials() {
        File credentialsFile = new File(System.getProperty("user.dir", "."), "cfcprograms-tenantsure-credentials.properties");
        return J2SEServiceConnector.getCredentials(credentialsFile.getAbsolutePath());
    }

    private boolean isSuccessfulCode(String code) {
        if (code != null && code.startsWith("SU")) {
            return true;
        } else {
            return false;
        }
    }

    /** Return client ID */
    InsuranceTenantSureClient createClient(Tenant tenant) {
        CFCAPISoap api = getApi().getCFCAPISoap();

        Credentials crs = getCredentials();
        Result autenticationResult = api.userAuthentication(crs.email, crs.password);

        if (!isSuccessfulCode(autenticationResult.getCode())) {
            throw new Error(autenticationResult.getCode());
        }

        SimpleClient parameters = new ObjectFactory().createSimpleClient();
        parameters.setSessionID(autenticationResult.getId());

        TenantSureTenantAdapter.fillClient(tenant, parameters);

        SimpleClientResponse createClientRssult = api.runCreateClient(parameters);
        if (!isSuccessfulCode(createClientRssult.getSimpleClientResult().getCode())) {
            throw new Error(createClientRssult.getSimpleClientResult().getCode());
        }

        InsuranceTenantSureClient tenantSureClient = EntityFactory.create(InsuranceTenantSureClient.class);
        tenantSureClient.tenant().set(tenant);
        tenantSureClient.clientReferenceNumber().setValue(createClientRssult.getSimpleClientResult().getId());
        return tenantSureClient;
    }

}
