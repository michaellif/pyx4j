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

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.domain.tenant.insurance.InsuranceCertificate;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSure;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSure.CancellationType;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSure.Status;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSureClient;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSureTransaction;
import com.propertyvista.domain.tenant.insurance.TenantSureConstants;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureCoverageDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureCoverageDTO.PreviousClaims;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureMessageDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureQuoteDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureTenantInsuranceStatusDetailedDTO;

public class TenantSureFacadeImpl implements TenantSureFacade {

    private static final I18n i18n = I18n.get(TenantSureFacadeImpl.class);

    private static final Logger log = LoggerFactory.getLogger(TenantSureFacadeImpl.class);

    public static boolean mockup = true;

    private final ICfcApiClient cfcApiClient;

    public TenantSureFacadeImpl(ICfcApiClient cfcApiClient) {
        this.cfcApiClient = cfcApiClient;
    }

    public TenantSureFacadeImpl() {
        this(mockup ? new MockupCfcApiClient() : new CfcApiClient());
    }

    @Override
    public InsurancePaymentMethod getPaymentMethod(Tenant tenantId) {
        return TenantSurePayments.getPaymentMethod(tenantId);
    }

    @Override
    public TenantSureQuoteDTO getQuote(TenantSureCoverageDTO coverage, Tenant tenantId) {
        if (coverage.numberOfPreviousClaims().getValue() != PreviousClaims.MoreThanTwo) {
            InsuranceTenantSureClient client = initializeClient(tenantId);
            TenantSureQuoteDTO quote = cfcApiClient.getQuote(client, coverage);
            return quote;
        } else {
            TenantSureQuoteDTO quote = EntityFactory.create(TenantSureQuoteDTO.class);
            quote.specialQuote().setValue(i18n.tr("Please call TenantSure {0} to get your quote.", TenantSureConstants.TENANTSURE_PHONE_NUMBER));
            return quote;
        }
    }

    /**
     * Only update credit card, do not perform outstanding payment
     */
    @Override
    public InsurancePaymentMethod updatePaymentMethod(InsurancePaymentMethod paymentMethod, Tenant tenantId) {
        return TenantSurePayments.updatePaymentMethod(paymentMethod, tenantId);
    }

    /**
     * Function implements this: http://jira.birchwoodsoftwaregroup.com/wiki/pages/viewpage.action?pageId=10027234
     */
    @Override
    public void buyInsurance(TenantSureQuoteDTO quote, Tenant tenantId) {
        Validate.isTrue(!quote.quoteId().isNull(), "it's impossible to buy insurance with no quote id!!!");

        InsuranceTenantSure insuranceTenantSure = EntityFactory.create(InsuranceTenantSure.class);
        insuranceTenantSure.quoteId().setValue(quote.quoteId().getValue());
        insuranceTenantSure.client().set(initializeClient(tenantId));
        insuranceTenantSure.status().setValue(InsuranceTenantSure.Status.Draft);

        insuranceTenantSure.startDate().setValue(new LogicalDate());
        insuranceTenantSure.monthlyPayable().setValue(quote.totalMonthlyPayable().getValue());
        insuranceTenantSure.details().liabilityCoverage().setValue(quote.coverage().personalLiabilityCoverage().getValue());
        insuranceTenantSure.details().contentsCoverage().setValue(quote.coverage().contentsCoverage().getValue());
        insuranceTenantSure.details().deductible().setValue(quote.coverage().deductible().getValue());
        insuranceTenantSure.details().grossPremium().setValue(quote.grossPremium().getValue());
        insuranceTenantSure.details().underwriterFee().setValue(quote.underwriterFee().getValue());
        // TODO fill details with taxes: "ts.details().taxes().setAll(BLA_BLA_BLA)"

        Persistence.service().persist(insuranceTenantSure);

        // Start payment
        InsuranceTenantSureTransaction transaction = EntityFactory.create(InsuranceTenantSureTransaction.class);
        transaction.insurance().set(insuranceTenantSure);
        transaction.paymentMethod().set(TenantSurePayments.getPaymentMethod(tenantId));
        transaction.status().setValue(InsuranceTenantSureTransaction.TransactionStatus.Draft);
        transaction.amount().setValue(insuranceTenantSure.monthlyPayable().getValue().multiply(BigDecimal.valueOf(2)));
        Persistence.service().persist(transaction);

        Persistence.service().commit();

        String tenantSureCertificateNumber = null;
        // Like two phase commit transaction
        {
            try {
                transaction = TenantSurePayments.preAuthorization(transaction);
            } catch (Throwable e) {
                log.error("Error", e);
                transaction.status().setValue(InsuranceTenantSureTransaction.TransactionStatus.Rejected);
                Persistence.service().persist(transaction);
                insuranceTenantSure.status().setValue(InsuranceTenantSure.Status.Failed);
                Persistence.service().persist(insuranceTenantSure);
                Persistence.service().commit();
                if (e instanceof UserRuntimeException) {
                    throw (UserRuntimeException) e;
                } else {
                    throw new UserRuntimeException(i18n.tr("Credit Card Authorization failed"));
                }
            }
            transaction.status().setValue(InsuranceTenantSureTransaction.TransactionStatus.Authorized);
            Persistence.service().persist(transaction);
            Persistence.service().commit();

            try {
                tenantSureCertificateNumber = cfcApiClient.bindQuote(insuranceTenantSure.quoteId().getValue());
            } catch (Throwable e) {
                log.error("Error", e);
                insuranceTenantSure.status().setValue(InsuranceTenantSure.Status.Failed);
                Persistence.service().persist(insuranceTenantSure);
                Persistence.service().commit();
                if (e instanceof UserRuntimeException) {
                    throw (UserRuntimeException) e;
                } else {
                    throw new UserRuntimeException(i18n.tr("Insurance bind failed"));
                }
            }

            insuranceTenantSure.status().setValue(InsuranceTenantSure.Status.Active);
            Persistence.service().persist(insuranceTenantSure);
            Persistence.service().commit();
        }

        createInsuranceCertificate(tenantId, tenantSureCertificateNumber, insuranceTenantSure);

        try {
            TenantSurePayments.compleateTransaction(transaction);
        } catch (Throwable e) {
            log.error("Error", e);
            transaction.status().setValue(InsuranceTenantSureTransaction.TransactionStatus.Rejected);
            Persistence.service().persist(transaction);
            insuranceTenantSure.status().setValue(InsuranceTenantSure.Status.Pending);
            Persistence.service().persist(insuranceTenantSure);
            Persistence.service().commit();
            if (e instanceof UserRuntimeException) {
                throw (UserRuntimeException) e;
            } else {
                throw new UserRuntimeException(i18n.tr("Credit Card payment failed, payment transaction would be completed later"));
            }
        }
        transaction.status().setValue(InsuranceTenantSureTransaction.TransactionStatus.Cleared);
        Persistence.service().commit();

    }

    private void createInsuranceCertificate(Tenant tenantId, String insuranceCertifiateNumber, InsuranceTenantSure ts) {
        InsuranceCertificate ic = EntityFactory.create(InsuranceCertificate.class);
        ic.tenant().set(tenantId);

        ic.insuranceProvider().setValue(TenantSureConstants.TENANTSURE_LEGAL_NAME);
        ic.insuranceCertificateNumber().setValue(insuranceCertifiateNumber);
        ic.personalLiability().setValue(ts.details().liabilityCoverage().getValue());
        ic.startDate().setValue(ts.startDate().getValue());
        ic.expirationDate().setValue(null); // doesn't expire unless payment fails

        Persistence.service().persist(ic);

        ts.insuranceCertificate().set(ic);
        Persistence.service().persist(ts);
    }

    @Override
    public TenantSureTenantInsuranceStatusDetailedDTO getStatus(Tenant tenantId) {
        InsuranceTenantSure insurance = retrieveActiveInsuranceTenantSure(tenantId);

        TenantSureTenantInsuranceStatusDetailedDTO tenantSureInsurance = EntityFactory.create(TenantSureTenantInsuranceStatusDetailedDTO.class);
        tenantSureInsurance.insuranceCertificateNumber().setValue(insurance.insuranceCertificate().insuranceCertificateNumber().getValue());

        tenantSureInsurance.quote().coverage().personalLiabilityCoverage().setValue(insurance.details().liabilityCoverage().getValue());
        tenantSureInsurance.quote().coverage().contentsCoverage().setValue(insurance.details().contentsCoverage().getValue());
        tenantSureInsurance.quote().coverage().deductible().setValue(insurance.details().deductible().getValue());
        tenantSureInsurance.quote().grossPremium().setValue(insurance.details().grossPremium().getValue());
        tenantSureInsurance.quote().underwriterFee().setValue(insurance.details().underwriterFee().getValue());
        // TODO add taxes        
        tenantSureInsurance.quote().totalMonthlyPayable().setValue(insurance.monthlyPayable().getValue());

        tenantSureInsurance.expiryDate().setValue(insurance.expiryDate().getValue());

        if (insurance.status().getValue() == Status.PendingCancellation) {
            TenantSureMessageDTO messageHolder = EntityFactory.create(TenantSureMessageDTO.class);
            messageHolder.messageText().setValue(
                    i18n.tr("Your payment couln't be processed, please update your credit card info or else your insurance will expire"));
        }

        return tenantSureInsurance;

    }

    @Override
    public void cancel(Tenant tenantId, CancellationType cancellationType, String cancellationReason) {
        InsuranceTenantSure insuranceTenantSure = retrieveActiveInsuranceTenantSure(tenantId);
        Validate.notNull(insuranceTenantSure, "no active TenantSure insurance was found");
        insuranceTenantSure.status().setValue(Status.PendingCancellation);
        insuranceTenantSure.cancellation().setValue(cancellationType);
        insuranceTenantSure.cancellationDescriptionReasonFromTenantSure().setValue(cancellationReason);
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        insuranceTenantSure.expiryDate().setValue(new LogicalDate(cal.getTime()));
        Persistence.service().persist(insuranceTenantSure);
        Persistence.service().commit();
        // TODO send email notification to tenant?
    }

    private InsuranceTenantSureClient initializeClient(Tenant tenantId) {
        EntityQueryCriteria<InsuranceTenantSureClient> criteria = EntityQueryCriteria.create(InsuranceTenantSureClient.class);
        criteria.eq(criteria.proto().tenant(), tenantId);
        InsuranceTenantSureClient tenantSureClient = Persistence.service().retrieve(criteria);
        if (tenantSureClient == null) {
            tenantSureClient = EntityFactory.create(InsuranceTenantSureClient.class);
            tenantSureClient.tenant().set(tenantId);
            String clientReferenceNumber = cfcApiClient.createClient(tenantId);
            tenantSureClient.clientReferenceNumber().setValue(clientReferenceNumber);
            Persistence.service().persist(tenantSureClient);
            Persistence.service().commit();
        }
        return tenantSureClient;
    }

    private InsuranceTenantSure retrieveActiveInsuranceTenantSure(Tenant tenantId) {
        EntityQueryCriteria<InsuranceTenantSure> criteria = EntityQueryCriteria.create(InsuranceTenantSure.class);
        criteria.add(PropertyCriterion.ne(criteria.proto().status(), InsuranceTenantSure.Status.Draft));
        criteria.add(PropertyCriterion.ne(criteria.proto().status(), InsuranceTenantSure.Status.Failed));
        criteria.or(PropertyCriterion.eq(criteria.proto().status(), InsuranceTenantSure.Status.Active),
                PropertyCriterion.eq(criteria.proto().status(), InsuranceTenantSure.Status.PendingCancellation));
        criteria.add(PropertyCriterion.eq(criteria.proto().client().tenant(), tenantId));
        InsuranceTenantSure insurance = Persistence.service().retrieve(criteria);
        return insurance;
    }
}
