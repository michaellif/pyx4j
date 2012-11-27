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
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;

import com.cfcprograms.api.ArrayOfString;
import com.cfcprograms.api.CFCAPI;
import com.cfcprograms.api.CFCAPISoap;
import com.cfcprograms.api.ObjectFactory;
import com.cfcprograms.api.OptionQuote;
import com.cfcprograms.api.Result;
import com.cfcprograms.api.SimpleClient;
import com.cfcprograms.api.SimpleClientResponse;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.j2se.J2SEServiceConnector;
import com.pyx4j.essentials.j2se.J2SEServiceConnector.Credentials;

import com.propertyvista.biz.tenant.insurance.tenantsure.apiadapters.TenantSureCoverageRequestAdapter;
import com.propertyvista.biz.tenant.insurance.tenantsure.apiadapters.TenantSureTenantAdapter;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSureClient;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureCoverageDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureQuoteDTO;

/** This is an adapter class for CFC SOAP API */
public class CfcApiClient implements ICfcApiClient {

    @Override
    public String createClient(Tenant tenant) {
        CFCAPISoap api = getApi().getCFCAPISoap();
        String sessionId = makeNewCfcSession(api);

        SimpleClient simpleClient = new ObjectFactory().createSimpleClient();
        simpleClient.setSessionID(sessionId);
        TenantSureTenantAdapter.fillClient(tenant, simpleClient);

        SimpleClientResponse createClientRssult = api.runCreateClient(simpleClient);
        if (!isSuccessfulCode(createClientRssult.getSimpleClientResult().getCode())) {
            throw new Error(createClientRssult.getSimpleClientResult().getCode());
        }
        return createClientRssult.getSimpleClientResult().getId();
    }

    @Override
    public TenantSureQuoteDTO getQuote(InsuranceTenantSureClient client, TenantSureCoverageDTO coverageRequest) {
        CFCAPISoap api = getApi().getCFCAPISoap();
        String sessionId = makeNewCfcSession(api);

        OptionQuote optionQuote = new ObjectFactory().createOptionQuote();
        optionQuote.setSessionID(sessionId);

        TenantSureCoverageRequestAdapter.fillOptionQuote(client, coverageRequest, optionQuote);
        Result quoteResponse = api.createQuote(optionQuote).getOptionQuoteResult();
        if (!isSuccessfulCode(quoteResponse.getCode())) {
            throw new Error(quoteResponse.getCode());
        }

        TenantSureQuoteDTO tenantSureQuote = EntityFactory.create(TenantSureQuoteDTO.class);
        tenantSureQuote.quoteId().setValue(quoteResponse.getQuoteData().getQuoteId());

        tenantSureQuote.grossPremium().setValue(new BigDecimal(quoteResponse.getGrossPremium()));
        tenantSureQuote.underwriterFee().setValue(new BigDecimal(quoteResponse.getFee()));
        // TODO deal with taxes
        tenantSureQuote.totalMonthlyPayable().setValue(new BigDecimal(quoteResponse.getTotalPayable()));

        return tenantSureQuote;
    }

    @Override
    public String bindQuote(String quoteId) {
        CFCAPISoap api = getApi().getCFCAPISoap();
        String sessionId = makeNewCfcSession(api);

        Result bindResult = api.requestBind(quoteId, sessionId);
        if (!isSuccessfulCode(bindResult.getCode())) {
            throw new Error(bindResult.getCode());
        }

        return bindResult.getId();
    }

    @Override
    public void requestDocument(String quoteId, List<String> emails) {
        CFCAPISoap cfcApiSoap = getApi().getCFCAPISoap();
        String sessionId = makeNewCfcSession(cfcApiSoap);

        ArrayOfString toEmailArray = new ObjectFactory().createArrayOfString();
        toEmailArray.getString().addAll(emails);
        cfcApiSoap.requestDocument(quoteId, sessionId, toEmailArray, new ObjectFactory().createArrayOfString());
    }

    private void assertSuccessfulResponse(Result response) {
        if (!isSuccessfulCode(response.getCode())) {
            throw new Error(response.getCode());
        }
    }

    /**
     * Opens a new session to CFC
     * 
     * @return session id, or throw an <code>Error<code>
     */
    private String makeNewCfcSession(CFCAPISoap cfcApiSoap) {
        Credentials crs = getCredentials();
        Result authResult = cfcApiSoap.userAuthentication(crs.email, crs.password);
        assertSuccessfulResponse(authResult);
        return authResult.getId();
    }

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

}
