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
import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;

import com.cfcprograms.api.ArrayOfString;
import com.cfcprograms.api.CFCAPI;
import com.cfcprograms.api.CFCAPISoap;
import com.cfcprograms.api.ObjectFactory;
import com.cfcprograms.api.OptionQuote;
import com.cfcprograms.api.OutputTax;
import com.cfcprograms.api.Result;
import com.cfcprograms.api.SimpleClient;
import com.cfcprograms.api.SimpleClientResponse;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.Credentials;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.j2se.CredentialsFileStorage;

import com.propertyvista.biz.tenant.insurance.tenantsure.apiadapters.TenantSureCoverageRequestAdapter;
import com.propertyvista.biz.tenant.insurance.tenantsure.apiadapters.TenantSureTenantAdapter;
import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSureClient;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSureTax;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureCoverageDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureQuoteDTO;

/** This is an adapter class for CFC SOAP API */
public class CfcApiClient implements ICfcApiClient {

    private static final boolean ENABLE_WORKAROUNDS_FOR_CFC_UNDOCUMENTED_CRAP = true;

    private CFCAPI getApi() {
        CFCAPI api = null;
        try {
            String url = "https://api.cfcprograms.com/cfc_api.asmx";
            if (!VistaDeployment.isVistaProduction()) {
//                boolean tcpMonitor = true;
//                if (tcpMonitor) {
//                    url = "http://localhost:9992/cfc_api.asmx";
//                } else {
                url = "http://testapi.cfcprograms.com/cfc_api.asmx";
//                }
            }
            api = new CFCAPI(new URL(url), new QName("http://api.cfcprograms.com/", "CFC_API"));
        } catch (MalformedURLException e) {
            throw new Error(e);
        }
        api.setHandlerResolver(new HandlerResolver() {
            @SuppressWarnings("rawtypes")
            @Override
            public List<Handler> getHandlerChain(PortInfo portInfo) {
                List<Handler> handlerChain = new ArrayList<Handler>();
                handlerChain.add(new CfcApiLogHandler());
                return handlerChain;
            }
        });

        boolean eclipseTcpIpMonitor = false;

        if (eclipseTcpIpMonitor) {
            ProxySelector ps = new ProxySelector() {

                ProxySelector defsel = ProxySelector.getDefault();

                @Override
                public List<Proxy> select(URI uri) {
                    if (uri.getHost().equals("testapi.cfcprograms.com")) {
                        ArrayList<Proxy> l = new ArrayList<Proxy>();
                        l.add(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", 9992)));
                        return l;
                    }
                    return defsel.select(uri);
                }

                @Override
                public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
                    defsel.connectFailed(uri, sa, ioe);
                }
            };
            ProxySelector.setDefault(ps);
        }

        return api;
    }

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
        if (ENABLE_WORKAROUNDS_FOR_CFC_UNDOCUMENTED_CRAP) {
            // THIS IS ACCORDING TO REAL LIFE EXPERIENCE:
            tenantSureQuote.quoteId().setValue(quoteResponse.getId());
        } else {
            // THIS IS ACCORDING TO THE DOCUMENTATION
            tenantSureQuote.quoteId().setValue(quoteResponse.getQuoteData().getQuoteId());
        }

        tenantSureQuote.grossPremium().setValue(new BigDecimal(quoteResponse.getGrossPremium()));
        tenantSureQuote.underwriterFee().setValue(new BigDecimal(quoteResponse.getFee()));

        for (OutputTax tax : quoteResponse.getQuoteData().getApplicableTaxes().getOutputTax()) {
            InsuranceTenantSureTax tenantSureTax = EntityFactory.create(InsuranceTenantSureTax.class);
            tenantSureTax.absoluteAmount().setValue(tax.getAbsoluteAmount());
            tenantSureTax.description().setValue(tax.getDescription());
            tenantSureTax.buinessLine().setValue(tax.getBusinessLine());
            tenantSureQuote.taxBreakdown().add(tenantSureTax);
        }
        tenantSureQuote.totalMonthlyPayable().setValue(new BigDecimal(quoteResponse.getTotalPayable()));

        tenantSureQuote.coverage().set(coverageRequest.duplicate(TenantSureCoverageDTO.class));
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

    @Override
    public LogicalDate cancel(String policyId, CancellationType cancellationType, String toAddress) {
        CFCAPISoap cfcApiSoap = getApi().getCFCAPISoap();

        String sessionId = makeNewCfcSession(cfcApiSoap);

        ArrayOfString toEmailArray = new ObjectFactory().createArrayOfString();
        toEmailArray.getString().add(toAddress);

        ArrayOfString bccEmailArray = new ObjectFactory().createArrayOfString();

        Result result = cfcApiSoap.mtaCancelPolicy(sessionId, policyId, null, cancellationType.name(), toEmailArray, bccEmailArray);
        assertSuccessfulResponse(result);

        return new LogicalDate(result.getQuoteData().getExpiryDate().toGregorianCalendar().getTime());
    }

    @Override
    public void reinstate(String policyId, ReinstatementType reinstatementType, String toAddress) {
        CFCAPISoap cfcApiSoap = getApi().getCFCAPISoap();

        String sessionId = makeNewCfcSession(cfcApiSoap);

        ArrayOfString toEmailArray = new ObjectFactory().createArrayOfString();
        toEmailArray.getString().add(toAddress);

        ArrayOfString bccEmailArray = new ObjectFactory().createArrayOfString();

        Result result = cfcApiSoap.mtaReinstatePolicy(sessionId, policyId, null, reinstatementType.name(), toEmailArray, bccEmailArray);
        assertSuccessfulResponse(result);
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
        Result authResult = cfcApiSoap.userAuthentication(crs.userName, crs.password);
        assertSuccessfulResponse(authResult);
        return authResult.getId();
    }

    private static Credentials getCredentials() {
        AbstractVistaServerSideConfiguration config = ((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance());
        return CredentialsFileStorage.getCredentials(new File(config.getConfigDirectory(), "cfcprograms-tenantsure-credentials.properties"));
    }

    private boolean isSuccessfulCode(String code) {
        if (code != null && code.startsWith("SU")) {
            return true;
        } else {
            return false;
        }
    }

}
