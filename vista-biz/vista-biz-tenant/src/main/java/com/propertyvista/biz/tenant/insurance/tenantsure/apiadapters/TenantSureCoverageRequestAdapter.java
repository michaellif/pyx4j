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

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.tenant.insurance.InsuranceTenantSureClient;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureCoverageDTO;

public class TenantSureCoverageRequestAdapter {

    public static void fillOptionQuote(InsuranceTenantSureClient client, TenantSureCoverageDTO coverageRequest, OptionQuote optionQuote) {
        ObjectFactory objectFactory = new ObjectFactory();
        optionQuote.setOptionCode(makeOptionCode(coverageRequest.personalLiabilityCoverage().getValue(), coverageRequest.contentsCoverage().getValue()));

        InsuredActivity activity = objectFactory.createInsuredActivity();
        activity.setCode("TS001");
        activity.setPercentage(new BigDecimal("100"));
        optionQuote.setActivityCode(activity);
        optionQuote.setClientID(client.clientReferenceNumber().getValue());

        GregorianCalendar today = new GregorianCalendar();
        DatatypeFactory dataTypeFactory;
        try {
            dataTypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new Error(e);
        }
        optionQuote.setInceptionDate(dataTypeFactory.newXMLGregorianCalendar(today));

        Tenant tenant = Persistence.service().retrieve(Tenant.class, client.tenant().getPrimaryKey());
        Persistence.service().retrieveMember(tenant.lease());
        BigDecimal monthlyRentalAmount = tenant.lease().unit().financial()._unitRent().getValue();
        optionQuote.setRevenue(monthlyRentalAmount);

        EntityQueryCriteria<LeaseTermTenant> numOfTenantsCriteria = EntityQueryCriteria.create(LeaseTermTenant.class);
        numOfTenantsCriteria.add(PropertyCriterion.eq(numOfTenantsCriteria.proto().leaseTermV(), tenant.lease().currentTerm().version()));
        int numOfTenants = Persistence.service().count(numOfTenantsCriteria);
        optionQuote.setEmployeeCount(new BigDecimal(numOfTenants));
        optionQuote.setUsExposure(BigDecimal.ZERO);
        optionQuote.setRetroDate(dataTypeFactory.newXMLGregorianCalendar(today));
        // expirining policy number?
        optionQuote.setPolicyPeriod("1M");

        String optionalExtras = "";
        if (coverageRequest.smoker().isBooleanTrue()) {
            optionalExtras += "Smoker=true;";
        }
        if (coverageRequest.numberOfPreviousClaims().getValue().numericValue() > 0) {
            optionalExtras += "Claims=" + coverageRequest.numberOfPreviousClaims().getValue().numericValue() + ";";
        }
        if (coverageRequest.deductible().getValue().compareTo(new BigDecimal("500")) > 0) {
            optionalExtras += "Deductible:" + coverageRequest.deductible().getValue().toString() + ";";
        }

        Persistence.service().retrieveMember(tenant.lease().unit().building());
        if (tenant.lease().unit().building().info().hasFireAlarm().isBooleanTrue()) {
            optionalExtras += "Alarm=true;";
        }
        if (tenant.lease().unit().building().info().hasSprinklers().isBooleanTrue()) {
            optionalExtras += "Sprinklers=true;";
        }
        if (tenant.lease().unit().building().info().hasEarthquakes().isBooleanTrue()) {
            optionalExtras += "BCEQ=true;";
        }
        if (!optionalExtras.equals("")) {
            optionQuote.setOptionalExtras(optionalExtras);
        }

        optionQuote.setReferralCode(""); // from the CFC-API doc: Pass a blank string. This field is reserved for a future date where we may offer the ability for clients to supply discount codes or 

    }

    public static String makeOptionCode(BigDecimal liablityCoverage, BigDecimal contentsCoverage) {
        StringBuilder optionCode = new StringBuilder();
        optionCode.append("TSP");
        optionCode.append(firstDigit(liablityCoverage));
        if (contentsCoverage != null && contentsCoverage.compareTo(BigDecimal.ZERO) != 0) {
            optionCode.append(firstDigit(contentsCoverage));
        }
        optionCode.append("0");
        return optionCode.toString();
    }

    public static String firstDigit(BigDecimal number) {
        return number.toString().substring(0, 1);
    }
}
