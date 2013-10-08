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
package com.propertyvista.portal.server.portal.web.services.services;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.essentials.server.admin.SystemMaintenance;
import com.pyx4j.rpc.shared.ServiceExecution;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.server.contexts.Visit;

import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.biz.tenant.insurance.TenantSureDeductibleOption;
import com.propertyvista.biz.tenant.insurance.TenantSureFacade;
import com.propertyvista.biz.tenant.insurance.TenantSureOptionCode;
import com.propertyvista.biz.tenant.insurance.TenantSureTextFacade;
import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.policy.policies.TenantInsurancePolicy;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.insurance.TenantSureConstants;
import com.propertyvista.domain.tenant.insurance.TenantSurePaymentSchedule;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.operations.rpc.VistaSystemMaintenanceState;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.TenantSureAgreementParamsDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.TenantSureCoverageDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.TenantSureInsurancePolicyDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.TenantSureQuoteDTO;
import com.propertyvista.portal.rpc.portal.web.services.services.TenantSureInsurancePolicyCrudService;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSurePaymentDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSurePaymentItemDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSurePaymentItemTaxDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.errors.TenantSureAlreadyPurchasedException;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.errors.TenantSureOnMaintenanceException;
import com.propertyvista.portal.server.portal.TenantAppContext;
import com.propertyvista.server.common.util.AddressRetriever;

public class TenantSureInsurancePolicyCrudServiceImpl implements TenantSureInsurancePolicyCrudService {

    private static class ServerSideQuteStorage {

        public static void put(TenantSureQuoteDTO quote) {
            getQuoteStorage().put(quote.quoteId().getValue(), quote);
        }

        public static TenantSureQuoteDTO get(String quoteId) {
            return getQuoteStorage().get(quoteId);
        }

        public static void clear() {
            Visit visit = Context.getVisit();
            synchronized (visit) {
                Context.getVisit().removeAttribute(ServerSideQuteStorage.class.getName());
            }
        }

        @SuppressWarnings("unchecked")
        private static ConcurrentHashMap<String, TenantSureQuoteDTO> getQuoteStorage() {
            Visit visit = Context.getVisit();

            ConcurrentHashMap<String, TenantSureQuoteDTO> storage;
            synchronized (visit) {
                Serializable o = Context.getVisit().getAttribute(ServerSideQuteStorage.class.getName());
                if (o == null) {
                    storage = new ConcurrentHashMap<String, TenantSureQuoteDTO>();
                    Context.getVisit().setAttribute(ServerSideQuteStorage.class.getName(), storage);
                } else {
                    storage = (ConcurrentHashMap<String, TenantSureQuoteDTO>) o;
                }
            }
            return storage;
        }

    }

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
    public void getQuote(AsyncCallback<TenantSureQuoteDTO> callback, TenantSureCoverageDTO quotationRequest) {
        if (((VistaSystemMaintenanceState) SystemMaintenance.getSystemMaintenanceInfo()).enableTenantSureMaintenance().isBooleanTrue()) {
            throw new TenantSureOnMaintenanceException();
        }

        TenantSureQuoteDTO quote = ServerSideFactory.create(TenantSureFacade.class).getQuote(//@formatter:off
                quotationRequest,
                TenantAppContext.getCurrentUserTenant().<Tenant> createIdentityStub()
        );//@formatter:on

        if (quote.specialQuote().isNull()) {
            ServerSideQuteStorage.put(quote);
        }
        callback.onSuccess(quote);
    }

    @Override
    public void acceptQuote(AsyncCallback<VoidSerializable> callback, TenantSureQuoteDTO quoteIdHolder, String tenantName, String tenantPhone,
            InsurancePaymentMethod paymentMethod) {
        if (((VistaSystemMaintenanceState) SystemMaintenance.getSystemMaintenanceInfo()).enableTenantSureMaintenance().isBooleanTrue()) {
            throw new TenantSureOnMaintenanceException();
        }

        paymentMethod.tenant().set(TenantAppContext.getCurrentUserTenant());

        // TODO since we pass the current user tenant to the facade function i think there's we should not settenant() filed of payment method
        ServerSideFactory.create(TenantSureFacade.class).savePaymentMethod(//@formatter:off
                paymentMethod,
                TenantAppContext.getCurrentUserTenant().<Tenant> createIdentityStub()
        );//@formatter:on

        TenantSureQuoteDTO quote = ServerSideQuteStorage.get(quoteIdHolder.quoteId().getValue());
        if (quote == null) {
            throw new Error("The requested quote " + quoteIdHolder.quoteId().getValue() + " was not found in client's context");
        }
        ServerSideFactory.create(TenantSureFacade.class).buyInsurance(//@formatter:off
                quote,
                TenantAppContext.getCurrentUserTenant().<Tenant> createIdentityStub(),
                tenantName,
                tenantPhone
        );//@formatter:on

        ServerSideQuteStorage.clear();

        callback.onSuccess(null);
    }

    @Override
    public void getCurrentTenantAddress(AsyncCallback<AddressSimple> callback) {
        callback.onSuccess(AddressRetriever.getLeaseParticipantCurrentAddressSimple(TenantAppContext.getCurrentUserTenant()));
    }

    @Override
    public void sendQuoteDetails(AsyncCallback<String> asyncCallback, String quoteId) {
        if (ServerSideQuteStorage.get(quoteId) == null) {
            throw new Error("The requested quote " + quoteId + " was not found in client's context");
        }
        String email = ServerSideFactory.create(TenantSureFacade.class).sendQuote(TenantAppContext.getCurrentUserTenant().<Tenant> createIdentityStub(),
                quoteId);
        asyncCallback.onSuccess(email);
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

    @Override
    public void init(final AsyncCallback<TenantSureInsurancePolicyDTO> callback, InitializationData initializationData) {

        TenantSureInsurancePolicyDTO tenantInsurancePolicy = EntityFactory.create(TenantSureInsurancePolicyDTO.class);

        Tenant tenant = Persistence.service().retrieve(Tenant.class, TenantAppContext.getCurrentUserTenant().getPrimaryKey());
        tenantInsurancePolicy.tenantSureCoverageRequest().tenantName().setValue(tenant.customer().person().name().getStringView());
        tenantInsurancePolicy.tenantSureCoverageRequest().tenantPhone().setValue(getDefaultPhone(tenant.customer().person()));
        tenantInsurancePolicy.tenantSureCoverageRequest().paymentSchedule().setValue(TenantSurePaymentSchedule.Monthly);

        tenantInsurancePolicy.agreementParams().set(getAgreementParams());

        tenantInsurancePolicy.paymentMethod().set(EntityFactory.create(InsurancePaymentMethod.class));
        tenantInsurancePolicy.paymentMethod().type().setValue(PaymentType.CreditCard);

        callback.onSuccess(tenantInsurancePolicy);

    }

    @Override
    public void retrieve(AsyncCallback<TenantSureInsurancePolicyDTO> callback, Key entityId,
            com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTarget retrieveTarget) {
        // THIS IS MOCKUP:
        TenantSureInsurancePolicyDTO mockupPolicyDTO = EntityFactory.create(TenantSureInsurancePolicyDTO.class);
        mockupPolicyDTO.certificate().insuranceCertificateNumber().setValue("TENANT-SURE-MOCKUP-001");
        mockupPolicyDTO.certificate().insuranceProvider().setValue(TenantSureConstants.TENANTSURE_LEGAL_NAME);
        mockupPolicyDTO.certificate().liabilityCoverage().setValue(new BigDecimal("1000000"));
        mockupPolicyDTO.certificate().inceptionDate().setValue(new LogicalDate());
        mockupPolicyDTO.contentsCoverage().setValue(new BigDecimal("50000"));
        mockupPolicyDTO.deductible().setValue(new BigDecimal("2000"));
        mockupPolicyDTO.paymentSchedule().setValue(TenantSurePaymentSchedule.Monthly);

        {
            TenantSurePaymentDTO paymentDTO = EntityFactory.create(TenantSurePaymentDTO.class);
            Calendar cal = GregorianCalendar.getInstance();
            cal.add(Calendar.MONTH, 1);
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));

            paymentDTO.paymentDate().setValue(new LogicalDate(cal.getTime()));
            paymentDTO.total().setValue(new BigDecimal("30.00"));
            TenantSurePaymentItemDTO paymentItem = paymentDTO.paymentBreakdown().$();
            paymentItem.description().setValue("premium");
            paymentItem.amount().setValue(new BigDecimal("19.99"));
            TenantSurePaymentItemTaxDTO taxA = paymentItem.taxBreakdown().$();
            taxA.tax().setValue("HST");
            taxA.amount().setValue(new BigDecimal("5"));
            paymentItem.taxBreakdown().add(taxA);
            paymentDTO.paymentBreakdown().add(paymentItem);
            mockupPolicyDTO.nextPaymentDetails().set(paymentDTO);
        }

        {
            TenantSurePaymentDTO paymentDTO = EntityFactory.create(TenantSurePaymentDTO.class);
            paymentDTO.total().setValue(new BigDecimal("30.00"));
            TenantSurePaymentItemDTO paymentItem = paymentDTO.paymentBreakdown().$();
            paymentItem.description().setValue("premium");
            paymentItem.amount().setValue(new BigDecimal("19.99"));
            TenantSurePaymentItemTaxDTO taxA = paymentItem.taxBreakdown().$();
            taxA.tax().setValue("HST");
            taxA.amount().setValue(new BigDecimal("5"));
            paymentItem.taxBreakdown().add(taxA);
            paymentDTO.paymentBreakdown().add(paymentItem);
            mockupPolicyDTO.annualPaymentDetails().set(paymentDTO);
        }

        callback.onSuccess(mockupPolicyDTO);
    }

    @Override
    public void create(final AsyncCallback<Key> callback, TenantSureInsurancePolicyDTO editableEntity) {

    }

    @Override
    @ServiceExecution(waitCaption = "Saving...")
    public void save(AsyncCallback<Key> callback, TenantSureInsurancePolicyDTO editableEntity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<TenantSureInsurancePolicyDTO>> callback, EntityListCriteria<TenantSureInsurancePolicyDTO> criteria) {
        // TODO Auto-generated method stub

    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void getPreAuthorizedPaymentsAgreement(AsyncCallback<String> areementHtml) {
        // TODO Auto-generated method stub

    }

    @Override
    public void getFaq(AsyncCallback<String> faqHtml) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updatePaymentMethod(AsyncCallback<VoidSerializable> callback, InsurancePaymentMethod paymentMethod) {
        // TODO Auto-generated method stub

    }

    @Override
    public void cancelTenantSure(AsyncCallback<VoidSerializable> callback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void reinstate(AsyncCallback<VoidSerializable> callback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendCertificate(AsyncCallback<String> defaultAsyncCallback, String email) {
        // TODO Auto-generated method stub

    }

    private TenantSureAgreementParamsDTO getAgreementParams() {
        if (((VistaSystemMaintenanceState) SystemMaintenance.getSystemMaintenanceInfo()).enableTenantSureMaintenance().isBooleanTrue()) {
            throw new TenantSureOnMaintenanceException();
        }
        if (ServerSideFactory.create(TenantSureFacade.class).getStatus(TenantAppContext.getCurrentUserTenant().<Tenant> createIdentityStub()) != null) {
            throw new TenantSureAlreadyPurchasedException();
        }

        TenantSureAgreementParamsDTO params = EntityFactory.create(TenantSureAgreementParamsDTO.class);

        Lease lease = Persistence.service().retrieve(Lease.class, TenantAppContext.getCurrentUserLeaseIdStub().getPrimaryKey());
        TenantInsurancePolicy tenantInsurancePolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(
                lease.unit().<AptUnit> createIdentityStub(), TenantInsurancePolicy.class);

        BigDecimal minRequiredLiabiliy = tenantInsurancePolicy.requireMinimumLiability().isBooleanTrue() ? tenantInsurancePolicy.minimumRequiredLiability()
                .getValue() : BigDecimal.ZERO;
        params.generalLiabilityCoverageOptions().addAll(filterGeneralLiabilityOptions(minRequiredLiabiliy));
        params.contentsCoverageOptions().addAll(CONTENTS_COVERAGE_OPTIONS);

        params.deductibleOptions().addAll(getDeductibleOptions());
        // TODO fill in personal disclaimer: right now this is filled on client from resources         
        params.preAuthorizedDebitAgreement().setValue(ServerSideFactory.create(TenantSureTextFacade.class).getPreAuthorizedAgreement());

        return params;
    }

}
