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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;

import com.cfcprograms.api.ArrayOfString;
import com.cfcprograms.api.CFCAPI;
import com.cfcprograms.api.CFCAPISoap;
import com.cfcprograms.api.ObjectFactory;
import com.cfcprograms.api.OptionQuote;
import com.cfcprograms.api.Result;
import com.cfcprograms.api.SimpleClient;
import com.cfcprograms.api.SimpleClientResponse;
import com.sun.xml.ws.developer.JAXWSProperties;

import com.pyx4j.commons.Consts;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.Credentials;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.essentials.j2se.CredentialsFileStorage;

import com.propertyvista.biz.tenant.insurance.errors.CfcApiException;
import com.propertyvista.biz.tenant.insurance.errors.TooManyPreviousClaimsException;
import com.propertyvista.biz.tenant.insurance.tenantsure.apiadapters.TenantSureCfcMoneyAdapter;
import com.propertyvista.biz.tenant.insurance.tenantsure.apiadapters.TenantSureCoverageRequestAdapter;
import com.propertyvista.biz.tenant.insurance.tenantsure.apiadapters.TenantSureTenantAdapter;
import com.propertyvista.biz.tenant.insurance.tenantsure.rules.ITenantSurePaymentSchedule;
import com.propertyvista.biz.tenant.insurance.tenantsure.rules.TenantSurePaymentScheduleFactory;
import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.SystemConfig;
import com.propertyvista.config.TenantSureConfiguration;
import com.propertyvista.config.VistaInterfaceCredentials;
import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicyClient;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.TenantSureCoverageDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.TenantSureQuoteDTO;

/** This is an adapter class for CFC SOAP API */
// TODO CFCAPISOAP
public class CfcApiAdapterFacadeImpl implements CfcApiAdapterFacade {

    private static final boolean ENABLE_WORKAROUNDS_FOR_CFC_UNDOCUMENTED_STUFF = true;

    /**
     * Copy from com.sun.xml.internal.ws.developer.JAXWSProperties
     * value in milliseconds {@link HttpURLConnection#setConnectTimeout(int)}
     */
    public static final String JAXWSProperties_CONNECT_TIMEOUT = "com.sun.xml.internal.ws.connect.timeout";

    /**
     * Copy from com.sun.xml.internal.ws.developer.JAXWSProperties
     * value in milliseconds {@link HttpURLConnection#httpConnection.setReadTimeout(int)}
     */
    public static final String JAXWSProperties_REQUEST_TIMEOUT = "com.sun.xml.internal.ws.request.timeout";

    private final TenantSureConfiguration configuration;

    public CfcApiAdapterFacadeImpl(TenantSureConfiguration configuration) {
        this.configuration = configuration;
    }

    // TODO this monster needs refactoring: looks like having an abstract factory and a a multiple factories with every permutation of settings would do the job.    
    private CFCAPISoap getApi() {
        // Initialize proxy configuration
        SystemConfig.instance();

        CFCAPI api = null;
        try {
            String url = configuration.cfcApiEndpointUrl();
            api = new CFCAPI(new URL(url), new QName("http://api.cfcprograms.com/", "CFC_API"));
            api.setHandlerResolver(new HandlerResolver() {
                @SuppressWarnings("rawtypes")
                @Override
                public List<Handler> getHandlerChain(PortInfo portInfo) {
                    List<Handler> handlerChain = new ArrayList<Handler>();
                    handlerChain.add(new CfcApiLogHandler());
                    return handlerChain;
                }
            });
        } catch (MalformedURLException e) {
            throw new Error(e);
        }
        CFCAPISoap soap = api.getCFCAPISoap();
        Map<String, Object> requestContext = ((BindingProvider) soap).getRequestContext();

        // If we are using JAXWS reference implementation at runtime
        requestContext.put(JAXWSProperties.CONNECT_TIMEOUT, 30 * Consts.SEC2MILLISECONDS);
        requestContext.put(JAXWSProperties.REQUEST_TIMEOUT, 80 * Consts.SEC2MILLISECONDS);
        // Just in case if we will be using JDK bundled JAXWS
        requestContext.put(JAXWSProperties_CONNECT_TIMEOUT, 30 * Consts.SEC2MILLISECONDS);
        requestContext.put(JAXWSProperties_REQUEST_TIMEOUT, 80 * Consts.SEC2MILLISECONDS);

        return soap;
    }

    @Override
    public String createClient(Tenant tenant, String tenantName, String tenantPhone) throws CfcApiException {
        CFCAPISoap api = getApi();
        String sessionId = makeNewCfcSession(api);

        SimpleClient simpleClient = new ObjectFactory().createSimpleClient();
        simpleClient.setSessionID(sessionId);
        simpleClient.setCompanyName(tenantName);
        simpleClient.setContactName(tenantName);
        simpleClient.setTelephoneNumber(tenantPhone);
        TenantSureTenantAdapter.setClientsAddress(tenant, simpleClient);

        SimpleClientResponse createClientRssult = api.runCreateClient(simpleClient);
        if (!isSuccessfulCode(createClientRssult.getSimpleClientResult().getCode())) {
            throw new CfcApiException(createClientRssult.getSimpleClientResult().getCode());
        }
        return createClientRssult.getSimpleClientResult().getId();
    }

    @Override
    public TenantSureQuoteDTO getQuote(TenantSureInsurancePolicyClient client, TenantSureCoverageDTO coverageRequest) throws CfcApiException {
        CFCAPISoap api = getApi();
        String sessionId = makeNewCfcSession(api);

        OptionQuote optionQuote = new ObjectFactory().createOptionQuote();
        optionQuote.setSessionID(sessionId);

        TenantSureCoverageRequestAdapter.fillOptionQuote(client, coverageRequest, optionQuote);
        Result quoteResponse = api.createQuote(optionQuote).getOptionQuoteResult();
        if (CfcApiException.isCfcErrorCodeLine(quoteResponse.getCode())) {
            String error = quoteResponse.getCode();
            if (TooManyPreviousClaimsException.isTooManyPreviousClaimsMessage(error)) {
                throw new TooManyPreviousClaimsException(error);
            } else {
                throw new CfcApiException(quoteResponse.getCode());
            }
        }

        TenantSureQuoteDTO tenantSureQuote = EntityFactory.create(TenantSureQuoteDTO.class);
        if (ENABLE_WORKAROUNDS_FOR_CFC_UNDOCUMENTED_STUFF) {
            // THIS IS ACCORDING TO REAL LIFE EXPERIENCE:
            tenantSureQuote.quoteId().setValue(quoteResponse.getId());
        } else {
            // THIS IS ACCORDING TO THE DOCUMENTATION
            tenantSureQuote.quoteId().setValue(quoteResponse.getQuoteData().getQuoteId());
        }

        BigDecimal annualPremium = TenantSureCfcMoneyAdapter.parseMoney(quoteResponse.getGrossPremium());
        BigDecimal underwritingFee = TenantSureCfcMoneyAdapter.parseMoney(quoteResponse.getFee());
        BigDecimal totalAnnualTax = BigDecimal.ZERO;
        int numOfTaxLines = quoteResponse.getQuoteData().getApplicableTaxes().getOutputTax().size();
        if (numOfTaxLines > 1) {
            throw new Error("Got a wrong number of tax lines from CFC-API quote response: there should be only one (at most), but got " + numOfTaxLines);
        } else if (numOfTaxLines == 1) {
            totalAnnualTax = TenantSureCfcMoneyAdapter.adoptMoney(quoteResponse.getQuoteData().getApplicableTaxes().getOutputTax().get(0).getAbsoluteAmount());
        }

        ITenantSurePaymentSchedule paymentSchedule = TenantSurePaymentScheduleFactory.create(coverageRequest.paymentSchedule().getValue());
        paymentSchedule.prepareQuote(tenantSureQuote, annualPremium, underwritingFee, totalAnnualTax);

        // not even sure we need this
        tenantSureQuote.coverage().set(coverageRequest.duplicate(TenantSureCoverageDTO.class));

        return tenantSureQuote;
    }

    @Override
    public String bindQuote(String quoteId) throws CfcApiException {
        CFCAPISoap api = getApi();
        String sessionId = makeNewCfcSession(api);

        Result bindResult = api.requestBind(quoteId, sessionId);
        if (!isSuccessfulCode(bindResult.getCode())) {
            throw new CfcApiException(bindResult.getCode());
        }

        return bindResult.getId();
    }

    @Override
    public void requestDocument(String quoteId, List<String> emails) throws CfcApiException {
        CFCAPISoap cfcApiSoap = getApi();
        String sessionId = makeNewCfcSession(cfcApiSoap);

        ArrayOfString toEmailArray = new ObjectFactory().createArrayOfString();
        toEmailArray.getString().addAll(emails);
        Result r = cfcApiSoap.requestDocument(quoteId, sessionId, toEmailArray, new ObjectFactory().createArrayOfString());
        assertSuccessfulResponse(r);
    }

    @Override
    public LogicalDate cancel(String policyId, CancellationType cancellationType, String toAddress) throws CfcApiException {
        CFCAPISoap cfcApiSoap = getApi();

        String sessionId = makeNewCfcSession(cfcApiSoap);

        ArrayOfString toEmailArray = new ObjectFactory().createArrayOfString();
        toEmailArray.getString().add(toAddress);

        ArrayOfString bccEmailArray = new ObjectFactory().createArrayOfString();

        Result result = cfcApiSoap.mtaCancelPolicy(sessionId, policyId, null, cancellationType.name(), toEmailArray, bccEmailArray);
        assertSuccessfulResponse(result);
        return new LogicalDate(result.getEffectiveDate().toGregorianCalendar().getTime());
    }

    @Override
    public void reinstate(String policyId, ReinstatementType reinstatementType, String toAddress) throws CfcApiException {
        CFCAPISoap cfcApiSoap = getApi();

        String sessionId = makeNewCfcSession(cfcApiSoap);

        ArrayOfString toEmailArray = new ObjectFactory().createArrayOfString();
        toEmailArray.getString().add(toAddress);

        ArrayOfString bccEmailArray = new ObjectFactory().createArrayOfString();

        Result result = cfcApiSoap.mtaReinstatePolicy(sessionId, policyId, null, reinstatementType.name(), toEmailArray, bccEmailArray);
        assertSuccessfulResponse(result);
    }

    private void assertSuccessfulResponse(Result response) throws CfcApiException {
        if (!isSuccessfulCode(response.getCode())) {
            throw new CfcApiException(response.getCode());
        }
    }

    /**
     * Opens a new session to CFC
     * 
     * @return session id, or throw an <code>Error<code>
     * @throws CfcApiException
     */
    private String makeNewCfcSession(CFCAPISoap cfcApiSoap) throws CfcApiException {
        Credentials crs = getCredentials();
        Result authResult = cfcApiSoap.userAuthentication(crs.userName, crs.password);
        assertSuccessfulResponse(authResult);
        return authResult.getId();
    }

    private static Credentials getCredentials() {
        AbstractVistaServerSideConfiguration config = ((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance());
        return CredentialsFileStorage.getCredentials(new File(config.getConfigDirectory(), VistaInterfaceCredentials.tenantSurecCfcApi));
    }

    private boolean isSuccessfulCode(String code) {
        if (code != null && code.startsWith("SU")) {
            return true;
        } else {
            return false;
        }
    }

}
