/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-09
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.services.resident;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.admin.SystemMaintenance;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.biz.tenant.insurance.TenantSureDeductibleOption;
import com.propertyvista.biz.tenant.insurance.TenantSureFacade;
import com.propertyvista.biz.tenant.insurance.TenantSureOptionCode;
import com.propertyvista.biz.tenant.insurance.TenantSureTextFacade;
import com.propertyvista.biz.tenant.insurance.tenantsure.apiadapters.TenantSureCfcMoneyAdapter;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.policy.policies.TenantInsurancePolicy;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSureTaxGrossPremium;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.operations.rpc.VistaSystemMaintenanceState;
import com.propertyvista.portal.rpc.portal.services.resident.TenantSurePurchaseService;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureCoverageDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSurePaymentDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSurePaymentItemDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSurePaymentItemTaxDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureQuotationRequestParamsDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureQuoteDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureQuoteResponseDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.errors.TenantSureOnMaintenanceException;
import com.propertyvista.portal.server.portal.TenantAppContext;
import com.propertyvista.server.common.util.AddressRetriever;

public class TenantSurePurchaseServiceImpl implements TenantSurePurchaseService {

    private static I18n i18n = I18n.get(TenantSurePurchaseServiceImpl.class);

    private static List<BigDecimal> CONTENTS_COVERAGE_OPTIONS;

    private static List<BigDecimal> GENERAL_LIABILITY_OPTIONS;

    static {
        Set<BigDecimal> contentsCoverageOptionsSet = new HashSet<BigDecimal>();
        Set<BigDecimal> generalLiabilitySet = new HashSet<BigDecimal>();
        for (TenantSureOptionCode optionCode : TenantSureOptionCode.values()) {
            contentsCoverageOptionsSet.add(optionCode.contentsCoverage());
            generalLiabilitySet.add(optionCode.generalLiability());

        }

        List<BigDecimal> contentsCoverageOptions = new ArrayList<BigDecimal>(contentsCoverageOptionsSet);
        Collections.sort(contentsCoverageOptions);
        CONTENTS_COVERAGE_OPTIONS = Collections.unmodifiableList(contentsCoverageOptions);

        List<BigDecimal> generalLiabilityOptions = new ArrayList<BigDecimal>(generalLiabilitySet);
        Collections.sort(generalLiabilityOptions);
        GENERAL_LIABILITY_OPTIONS = Collections.unmodifiableList(generalLiabilityOptions);
    }

    @Override
    public void getQuotationRequestParams(AsyncCallback<TenantSureQuotationRequestParamsDTO> callback) {
        if (((VistaSystemMaintenanceState) SystemMaintenance.getSystemMaintenanceInfo()).enableTenantSureMaintenance().isBooleanTrue()) {
            throw new TenantSureOnMaintenanceException();
        }

        TenantSureQuotationRequestParamsDTO params = EntityFactory.create(TenantSureQuotationRequestParamsDTO.class);

        Tenant tenant = Persistence.service().retrieve(Tenant.class, TenantAppContext.getCurrentUserTenant().getPrimaryKey());
        params.tenantName().setValue(tenant.customer().person().name().getStringView());
        params.tenantPhone().setValue(getDefaultPhone(tenant.customer().person()));

        Lease lease = Persistence.service().retrieve(Lease.class, TenantAppContext.getCurrentUserLeaseIdStub().getPrimaryKey());
        TenantInsurancePolicy tenantInsurancePolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(
                lease.unit().<AptUnit> createIdentityStub(), TenantInsurancePolicy.class);

        BigDecimal minRequiredLiabiliy = tenantInsurancePolicy.requireMinimumLiability().isBooleanTrue() ? tenantInsurancePolicy.minimumRequiredLiability()
                .getValue() : BigDecimal.ZERO;
        params.generalLiabilityCoverageOptions().addAll(filterGeneralLiabilityOptions(minRequiredLiabiliy));
        params.contentsCoverageOptions().addAll(CONTENTS_COVERAGE_OPTIONS);

        params.deductibleOptions().addAll(getDeductibleOptions());

        if (false) {
            // TODO right now this is filled on client from resources
            params.personalDisclaimerHolder();
        }
        params.preAuthorizedDebitAgreement().setValue(ServerSideFactory.create(TenantSureTextFacade.class).getPreAuthorizedAgreement());

        callback.onSuccess(params);

    }

    @Override
    public void getQuote(AsyncCallback<TenantSureQuoteResponseDTO> callback, TenantSureCoverageDTO quotationRequest) {
        if (((VistaSystemMaintenanceState) SystemMaintenance.getSystemMaintenanceInfo()).enableTenantSureMaintenance().isBooleanTrue()) {
            throw new TenantSureOnMaintenanceException();
        }
        TenantSureQuoteResponseDTO quoteResponse = EntityFactory.create(TenantSureQuoteResponseDTO.class);

        TenantSureQuoteDTO quote = ServerSideFactory.create(TenantSureFacade.class).getQuote(quotationRequest,
                TenantAppContext.getCurrentUserTenant().<Tenant> createIdentityStub());
        quoteResponse.quote().set(quote);

        TenantSurePaymentDTO monthlyPayment = EntityFactory.create(TenantSurePaymentDTO.class);
        TenantSurePaymentItemDTO monthlyPremium = monthlyPayment.paymentBreakdown().$();
        monthlyPremium.amount().setValue(quote.grossPremium().getValue().divide(new BigDecimal(12), TenantSureCfcMoneyAdapter.getRoundingMode()));
        monthlyPremium.description().setValue(i18n.tr("Premium"));
        for (InsuranceTenantSureTaxGrossPremium grossPremiumTax : quote.grossPremiumTaxBreakdown()) {
            TenantSurePaymentItemTaxDTO monthlyPremiumTax = monthlyPremium.taxBreakdown().$();
            monthlyPremiumTax.tax().setValue(grossPremiumTax.description().getValue());
            monthlyPremiumTax.amount().setValue(
                    grossPremiumTax.absoluteAmount().getValue().divide(new BigDecimal(12), TenantSureCfcMoneyAdapter.getRoundingMode()));
            monthlyPremium.taxBreakdown().add(monthlyPremiumTax);
        }
        monthlyPayment.paymentBreakdown().add(monthlyPremium);
        monthlyPayment.total().setValue(quote.totalPayable().getValue().divide(new BigDecimal(12), TenantSureCfcMoneyAdapter.getRoundingMode()));

        quoteResponse.monthlyPayment().set(monthlyPayment);

        callback.onSuccess(quoteResponse);
    }

    @Override
    public void acceptQuote(AsyncCallback<VoidSerializable> callback, TenantSureQuoteDTO quote, String tenantName, String tenantPhone,
            InsurancePaymentMethod paymentMethod) {
        if (((VistaSystemMaintenanceState) SystemMaintenance.getSystemMaintenanceInfo()).enableTenantSureMaintenance().isBooleanTrue()) {
            throw new TenantSureOnMaintenanceException();
        }

        paymentMethod.tenant().set(TenantAppContext.getCurrentUserTenant());

        // TODO since we pass the current user tenant to the facade function i think there's we should not settenant() filed of payment method
        ServerSideFactory.create(TenantSureFacade.class)
                .savePaymentMethod(paymentMethod, TenantAppContext.getCurrentUserTenant().<Tenant> createIdentityStub());

        ServerSideFactory.create(TenantSureFacade.class).buyInsurance(quote, TenantAppContext.getCurrentUserTenant().<Tenant> createIdentityStub(), tenantName,
                tenantPhone);

        callback.onSuccess(null);
    }

    @Override
    public void getCurrentTenantAddress(AsyncCallback<AddressStructured> callback) {
        AddressRetriever.getLeaseParticipantCurrentAddress(callback, TenantAppContext.getCurrentUserTenantInLease());
    }

    private String getDefaultPhone(Person person) {
        if (!person.homePhone().isNull()) {
            return person.homePhone().getValue();
        } else if (person.mobilePhone().isNull()) {
            return person.mobilePhone().getValue();
        } else {
            return "";
        }
    }

    private List<BigDecimal> getDeductibleOptions() {
        return Arrays.asList(TenantSureDeductibleOption.amountValues());
    }

    private List<BigDecimal> filterGeneralLiabilityOptions(BigDecimal minValue) {
        List<BigDecimal> filteredValues = new ArrayList<BigDecimal>();
        for (BigDecimal option : GENERAL_LIABILITY_OPTIONS) {
            if (option.compareTo(minValue) >= 0) {
                filteredValues.add(option);
            }
        }
        return filteredValues;
    }
}
