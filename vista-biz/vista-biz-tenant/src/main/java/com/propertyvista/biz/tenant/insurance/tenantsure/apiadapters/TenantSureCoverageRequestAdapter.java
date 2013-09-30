/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-21
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.tenant.insurance.tenantsure.apiadapters;

import java.math.BigDecimal;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import com.cfcprograms.api.InsuredActivity;
import com.cfcprograms.api.ObjectFactory;
import com.cfcprograms.api.OptionQuote;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.tenant.insurance.TenantSureOptionCode;
import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicyClient;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.TenantSureCoverageDTO;

/**
 * For more information see
 * <a href="http://jira.birchwoodsoftwaregroup.com/wiki/download/attachments/10027124/Tenantsure+API+Integration+Guide+20120910-1.pdf">Tenant Sure API
 * Integration Guide - 2012-09-10</a>.
 */
public class TenantSureCoverageRequestAdapter {

    private static final I18n i18n = I18n.get(TenantSureCoverageRequestAdapter.class);

    /**
     * Defined in the CFC API Documentation as follows: <br/>
     * 'The activity code that most closely represents the prospective
     * insured's activities (only one code is permitted for option quotes).
     * This is a complex date type consisting of a "Code" and a
     * "Percentage". The "Percentage" must equal 100 for option quotes.' <br/>
     */
    public static final InsuredActivity TENANT_SURE_INSURED_ACTIVITY_CODE;

    static {
        TENANT_SURE_INSURED_ACTIVITY_CODE = new ObjectFactory().createInsuredActivity();
        TENANT_SURE_INSURED_ACTIVITY_CODE.setCode("TS001");
        TENANT_SURE_INSURED_ACTIVITY_CODE.setPercentage(new BigDecimal(100));
    }

    public static void fillOptionQuote(TenantSureInsurancePolicyClient tenantSureClient, TenantSureCoverageDTO coverageRequest, OptionQuote optionQuote) {
        DatatypeFactory dataTypeFactory;
        try {
            dataTypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }

        Persistence.service().retrieve(tenantSureClient.tenant().lease());
        Persistence.service().retrieve(tenantSureClient.tenant().lease().unit().building());

        // code that represents the pre-agreed insurance package for which the quote is required
        optionQuote.setOptionCode(matchOptionCode(coverageRequest.personalLiabilityCoverage().getValue(), coverageRequest.contentsCoverage().getValue()));
        optionQuote.setActivityCode(TENANT_SURE_INSURED_ACTIVITY_CODE);
        optionQuote.setClientID(tenantSureClient.clientReferenceNumber().getValue());
        optionQuote.setInceptionDate(dataTypeFactory.newXMLGregorianCalendar(new GregorianCalendar()));
        optionQuote.setRevenue(tenantSureClient.tenant().lease().currentTerm().version().leaseProducts().serviceItem().agreedPrice().getValue());
        optionQuote.setEmployeeCount(countNumberOfTenants(tenantSureClient.tenant().lease().currentTerm()));
        optionQuote.setUsExposure(BigDecimal.ZERO);
        optionQuote.setRetroDate(dataTypeFactory.newXMLGregorianCalendar(new GregorianCalendar()));
        optionQuote.setPolicyPeriod("12M");
        optionQuote.setOptionalExtras(new TenantSureOptionalExtrasFormatter().formatOptionalExtras(coverageRequest, tenantSureClient.tenant()));

        // quote from the CFC-API doc:
        // Pass a blank string.
        // This field is reserved for a future date where we may offer the ability for clients to supply discount codes or similar.
        optionQuote.setReferralCode("");

    }

    static BigDecimal countNumberOfTenants(com.propertyvista.domain.tenant.lease.LeaseTerm term) {
        EntityQueryCriteria<LeaseTermTenant> numOfTenantsCriteria = EntityQueryCriteria.create(LeaseTermTenant.class);
        numOfTenantsCriteria.add(PropertyCriterion.eq(numOfTenantsCriteria.proto().leaseTermV().holder(), term));
        int numOfTenants = Persistence.service().count(numOfTenantsCriteria);
        return new BigDecimal(numOfTenants);
    }

    private static String matchOptionCode(BigDecimal generalLiablilty, BigDecimal contentsCoverage) {
        String optionCode = null;
        try {
            optionCode = TenantSureOptionCode.codeOf(generalLiablilty, contentsCoverage).name();
        } catch (IllegalArgumentException ex) {
            throw new UserRuntimeException(i18n.tr("There a pre-defined quote that matches the requrested coverage amounts was not found."));
        }
        return optionCode;
    }

}
