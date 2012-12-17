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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.shared.ApplicationMode;
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
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSureTax;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSureTransaction;
import com.propertyvista.domain.tenant.insurance.TenantSureConstants;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureCoverageDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureCoverageDTO.PreviousClaims;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureMessageDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureQuoteDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureTenantInsuranceStatusDetailedDTO;

public class TenantSureFacadeImpl implements TenantSureFacade {

    private static final boolean USE_CFC_API_MOCKUP_CLIENT = true;

    private static final I18n i18n = I18n.get(TenantSureFacadeImpl.class);

    private static final Logger log = LoggerFactory.getLogger(TenantSureFacadeImpl.class);

    private final ICfcApiClient cfcApiClient;

    public TenantSureFacadeImpl(ICfcApiClient cfcApiClient) {
        this.cfcApiClient = cfcApiClient;
    }

    public TenantSureFacadeImpl() {
        this(USE_CFC_API_MOCKUP_CLIENT ? new MockupCfcApiClient() : new CfcApiClient());
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

        insuranceTenantSure.inceptionDate().setValue(new LogicalDate());
        insuranceTenantSure.monthlyPayable().setValue(quote.totalMonthlyPayable().getValue());
        insuranceTenantSure.details().liabilityCoverage().setValue(quote.coverage().personalLiabilityCoverage().getValue());
        insuranceTenantSure.details().contentsCoverage().setValue(quote.coverage().contentsCoverage().getValue());
        insuranceTenantSure.details().deductible().setValue(quote.coverage().deductible().getValue());
        insuranceTenantSure.details().grossPremium().setValue(quote.grossPremium().getValue());
        insuranceTenantSure.details().underwriterFee().setValue(quote.underwriterFee().getValue());
        for (InsuranceTenantSureTax tax : quote.taxBreakdown()) {
            insuranceTenantSure.details().taxes().add(tax);
        }

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

            // send documentation to tenant
            List<String> emails = new ArrayList<String>();
            if (ApplicationMode.isDevelopment()) {
                emails.add("artyom@propertyvista.com");
            } else {
                Tenant tenant = Persistence.service().retrieve(Tenant.class, tenantId.getPrimaryKey());
                emails.add(tenant.customer().user().email().getValue());
            }
            cfcApiClient.requestDocument(insuranceTenantSure.quoteId().getValue(), emails);

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
        ic.isPropertyVistaIntegratedProvider().setValue(true);
        ic.tenant().set(tenantId);

        ic.insuranceProvider().setValue(TenantSureConstants.TENANTSURE_LEGAL_NAME);
        ic.insuranceCertificateNumber().setValue(insuranceCertifiateNumber);
        ic.liabilityCoverage().setValue(ts.details().liabilityCoverage().getValue());
        ic.inceptionDate().setValue(ts.inceptionDate().getValue());
        ic.expiryDate().setValue(null); // doesn't expire unless payment fails

        Persistence.service().persist(ic);

        ts.insuranceCertificate().set(ic);
        Persistence.service().persist(ts);
    }

    @Override
    public TenantSureTenantInsuranceStatusDetailedDTO getStatus(Tenant tenantId) {
        InsuranceTenantSure insuranceTenantSure = retrieveActiveInsuranceTenantSure(tenantId);

        TenantSureTenantInsuranceStatusDetailedDTO status = EntityFactory.create(TenantSureTenantInsuranceStatusDetailedDTO.class);
        status.insuranceCertificateNumber().setValue(insuranceTenantSure.insuranceCertificate().insuranceCertificateNumber().getValue());

        status.quote().coverage().personalLiabilityCoverage().setValue(insuranceTenantSure.details().liabilityCoverage().getValue());
        status.quote().coverage().contentsCoverage().setValue(insuranceTenantSure.details().contentsCoverage().getValue());
        status.quote().coverage().deductible().setValue(insuranceTenantSure.details().deductible().getValue());
        status.quote().coverage().inceptionDate().setValue(insuranceTenantSure.insuranceCertificate().inceptionDate().getValue());

        status.quote().grossPremium().setValue(insuranceTenantSure.details().grossPremium().getValue());
        status.quote().underwriterFee().setValue(insuranceTenantSure.details().underwriterFee().getValue());
        status.quote().taxBreakdown().addAll(insuranceTenantSure.details().taxes());
        status.quote().totalMonthlyPayable().setValue(insuranceTenantSure.monthlyPayable().getValue());

        status.expiryDate().setValue(insuranceTenantSure.insuranceCertificate().expiryDate().getValue());

        if (insuranceTenantSure.status().getValue() != Status.PendingCancellation) {
            GregorianCalendar cal = new GregorianCalendar();
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
            cal.add(Calendar.MONTH, 1);
            status.nextPaymentDate().setValue(new LogicalDate(cal.getTime()));
        }

        if (insuranceTenantSure.status().getValue() == Status.PendingCancellation) {
            TenantSureMessageDTO message = status.messages().$();
            if (insuranceTenantSure.cancellation().getValue() == CancellationType.SkipPayment) {
                status.isPaymentFailed().setValue(true);
                message.messageText()
                        .setValue(
                                i18n.tr("There was a problem with your last scheduled payment. If you don't update your credit card details until {0,date,short}, your TeantSure insurance will expire on {1,date,short}.",
                                        insuranceTenantSure.expiryDate().getValue()));
            } else {
                message.messageText().setValue(
                        i18n.tr("Your insurance has been cancelled and will expire on {0,date,short}", insuranceTenantSure.expiryDate().getValue()));
            }
            status.messages().add(message);
        }

        return status;

    }

    @Override
    public void cancelByTenant(Tenant tenantId) {
        InsuranceTenantSure insuranceTenantSure = retrieveActiveInsuranceTenantSure(tenantId);
        validateIsCancellable(insuranceTenantSure);

        insuranceTenantSure.cancellationDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
        insuranceTenantSure.status().setValue(Status.PendingCancellation);
        insuranceTenantSure.cancellation().setValue(CancellationType.CancelledByTenant);

        GregorianCalendar cal = new GregorianCalendar();
        cal.add(Calendar.MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        insuranceTenantSure.expiryDate().setValue(new LogicalDate(cal.getTime()));
        Persistence.service().merge(insuranceTenantSure);

        insuranceTenantSure.insuranceCertificate().expiryDate().setValue(insuranceTenantSure.expiryDate().getValue());
        Persistence.service().merge(insuranceTenantSure.insuranceCertificate());

        Persistence.service().commit();
    }

    @Override
    public void cancelDueToSkippedPayment(Tenant tenantId) {
        InsuranceTenantSure insuranceTenantSure = retrieveActiveInsuranceTenantSure(tenantId);
        validateIsCancellable(insuranceTenantSure);

        insuranceTenantSure.cancellationDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
        insuranceTenantSure.status().setValue(Status.PendingCancellation);
        insuranceTenantSure.cancellation().setValue(CancellationType.SkipPayment);

        GregorianCalendar cal = new GregorianCalendar();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        insuranceTenantSure.expiryDate().setValue(new LogicalDate(cal.getTime()));
        Persistence.service().merge(insuranceTenantSure);

        insuranceTenantSure.insuranceCertificate().expiryDate().setValue(insuranceTenantSure.expiryDate().getValue());
        Persistence.service().merge(insuranceTenantSure.insuranceCertificate());

        Persistence.service().commit();
    }

    @Override
    public void cancelByTenantSure(Tenant tenantId, String cancellationReason, LogicalDate expiryDate) {
        InsuranceTenantSure insuranceTenantSure = retrieveActiveInsuranceTenantSure(tenantId);
        validateIsCancellable(insuranceTenantSure);

        insuranceTenantSure.cancellationDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
        insuranceTenantSure.status().setValue(expiryDate.compareTo(new LogicalDate()) < 0 ? Status.PendingCancellation : Status.Cancelled);
        insuranceTenantSure.cancellation().setValue(CancellationType.CancelledByTenantSure);
        insuranceTenantSure.cancellationDescriptionReasonFromTenantSure().setValue(cancellationReason);

        insuranceTenantSure.expiryDate().setValue(expiryDate);
        Persistence.service().merge(insuranceTenantSure);

        insuranceTenantSure.insuranceCertificate().expiryDate().setValue(insuranceTenantSure.expiryDate().getValue());
        Persistence.service().merge(insuranceTenantSure.insuranceCertificate());

        Persistence.service().commit();
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
        criteria.add(PropertyCriterion.eq(criteria.proto().insuranceCertificate().tenant().lease().leaseParticipants(), tenantId));

        InsuranceTenantSure insurance = Persistence.service().retrieve(criteria);
        return insurance;
    }

    private void validateIsCancellable(InsuranceTenantSure insuranceTenantSure) {
        Validate.notNull(insuranceTenantSure, "no active TenantSure insurance was found");
        if (insuranceTenantSure.status().getValue() != Status.Active) {
            throw new Error("It's impossible to cancel a tenant sure insurance which is not " + Status.Active);
        }
    }

}
