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

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.server.mail.SMTPMailServiceConfig;

import com.propertyvista.operations.domain.tenantsure.TenantSureSubscribers;
import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.biz.tenant.insurance.ICfcApiClient.ReinstatementType;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSure;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSure.CancellationType;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSure.TenantSureStatus;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSureClient;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSureReport;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSureTax;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSureTransaction;
import com.propertyvista.domain.tenant.insurance.TenantSureConstants;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureCoverageDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureCoverageDTO.PreviousClaims;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureMessageDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureQuoteDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureTenantInsuranceStatusDetailedDTO;
import com.propertyvista.server.jobs.TaskRunner;
import com.propertyvista.shared.config.VistaDemo;

public class TenantSureFacadeImpl implements TenantSureFacade {

    private static final boolean USE_CFC_API_MOCKUP_CLIENT = false;

    private static final I18n i18n = I18n.get(TenantSureFacadeImpl.class);

    private static final Logger log = LoggerFactory.getLogger(TenantSureFacadeImpl.class);

    private final ICfcApiClient cfcApiClient;

    public TenantSureFacadeImpl(ICfcApiClient cfcApiClient) {
        this.cfcApiClient = cfcApiClient;
    }

    public TenantSureFacadeImpl() {
        this((VistaDemo.isDemo() | USE_CFC_API_MOCKUP_CLIENT) ? new MockupCfcApiClient() : new CfcApiClient());
    }

    @Override
    public InsurancePaymentMethod getPaymentMethod(Tenant tenantId) {
        return TenantSurePayments.getPaymentMethod(tenantId);
    }

    @Override
    public TenantSureQuoteDTO getQuote(TenantSureCoverageDTO coverage, Tenant tenantId) {
        if (coverage.numberOfPreviousClaims().getValue() != PreviousClaims.MoreThanTwo) {
            InsuranceTenantSureClient client = initializeClient(tenantId, coverage.tenantName().getValue(), coverage.tenantPhone().getValue());
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
    public void buyInsurance(TenantSureQuoteDTO quote, Tenant tenantId, String tenantPhone, String tenantName) {
        Validate.isTrue(!quote.quoteId().isNull(), "it's impossible to buy insurance with no quote id!!!");

        InsuranceTenantSure insuranceTenantSure = EntityFactory.create(InsuranceTenantSure.class);
        insuranceTenantSure.tenant().set(tenantId);
        insuranceTenantSure.isPropertyVistaIntegratedProvider().setValue(true);
        insuranceTenantSure.insuranceProvider().setValue(TenantSureConstants.TENANTSURE_LEGAL_NAME);
        insuranceTenantSure.quoteId().setValue(quote.quoteId().getValue());
        insuranceTenantSure.client().set(initializeClient(tenantId, tenantPhone, tenantName));
        insuranceTenantSure.status().setValue(InsuranceTenantSure.TenantSureStatus.Draft);

        insuranceTenantSure.inceptionDate().setValue(new LogicalDate());
        insuranceTenantSure.monthlyPayable().setValue(quote.totalMonthlyPayable().getValue());
        insuranceTenantSure.liabilityCoverage().setValue(quote.coverage().personalLiabilityCoverage().getValue());
        insuranceTenantSure.details().contentsCoverage().setValue(quote.coverage().contentsCoverage().getValue());
        insuranceTenantSure.details().deductible().setValue(quote.coverage().deductible().getValue());
        insuranceTenantSure.details().grossPremium().setValue(quote.premium().getValue());
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
        transaction.amount().setValue(insuranceTenantSure.monthlyPayable().getValue());
        transaction.paymentDue().setValue(insuranceTenantSure.inceptionDate().getValue());
        Persistence.service().persist(transaction);

        Persistence.service().commit();

        String tenantSureCertificateNumber = null;
        // Like two phase commit transaction
        {
            try {
                transaction = TenantSurePayments.preAuthorization(transaction);
            } catch (Throwable e) {
                log.error("Error", e);
                transaction.status().setValue(InsuranceTenantSureTransaction.TransactionStatus.AuthorizationRejected);
                Persistence.service().persist(transaction);
                insuranceTenantSure.status().setValue(InsuranceTenantSure.TenantSureStatus.Failed);
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
                log.error("failed to bind quote. ", e);
                insuranceTenantSure.status().setValue(InsuranceTenantSure.TenantSureStatus.Failed);

                TenantSurePayments.preAuthorizationReversal(transaction);
                transaction.status().setValue(InsuranceTenantSureTransaction.TransactionStatus.AuthorizationReversal);
                Persistence.service().persist(transaction);

                Persistence.service().persist(insuranceTenantSure);
                Persistence.service().commit();
                if (e instanceof UserRuntimeException) {
                    throw (UserRuntimeException) e;
                } else {
                    throw new UserRuntimeException(i18n.tr("Insurance bind failed"));
                }
            }

            insuranceTenantSure.insuranceCertificateNumber().setValue(tenantSureCertificateNumber);
            insuranceTenantSure.status().setValue(InsuranceTenantSure.TenantSureStatus.Active);
            insuranceTenantSure.paymentDay().setValue(TenantSurePayments.calulatePaymentDay(insuranceTenantSure.inceptionDate().getValue()));
            Persistence.service().persist(insuranceTenantSure);

            InsuranceTenantSureReport tsReportStatusHolder = EntityFactory.create(InsuranceTenantSureReport.class);
            tsReportStatusHolder.insurance().set(insuranceTenantSure);
            Persistence.service().persist(tsReportStatusHolder);

            Persistence.service().commit();

            List<String> emails = new ArrayList<String>();
            SMTPMailServiceConfig mailConfig = (SMTPMailServiceConfig) ServerSideConfiguration.instance().getMailServiceConfigConfiguration();
            if (CommonsStringUtils.isStringSet(mailConfig.getForwardAllTo())) {
                emails.add(mailConfig.getForwardAllTo());
            } else {
                Tenant tenant = Persistence.service().retrieve(Tenant.class, tenantId.getPrimaryKey());
                emails.add(tenant.customer().user().email().getValue());
            }
            cfcApiClient.requestDocument(insuranceTenantSure.quoteId().getValue(), emails);

        }

        createInsuranceCertificate(tenantId, tenantSureCertificateNumber, insuranceTenantSure);
        createTenantSureSubscriberRecord(tenantSureCertificateNumber);

        try {
            TenantSurePayments.compleateTransaction(transaction);
        } catch (Throwable e) {
            log.error("Error", e);
            transaction.status().setValue(InsuranceTenantSureTransaction.TransactionStatus.AuthorizedPaymentRejectedRetry);
            Persistence.service().persist(transaction);
            insuranceTenantSure.status().setValue(InsuranceTenantSure.TenantSureStatus.Pending);
            Persistence.service().persist(insuranceTenantSure);
            Persistence.service().commit();
            if (e instanceof UserRuntimeException) {
                throw (UserRuntimeException) e;
            } else {
                throw new UserRuntimeException(i18n.tr("Credit Card payment failed, payment transaction would be completed later"));
            }
        }
        transaction.status().setValue(InsuranceTenantSureTransaction.TransactionStatus.Cleared);
        Persistence.service().persist(transaction);
        Persistence.service().commit();

    }

    private void createInsuranceCertificate(Tenant tenantId, String insuranceCertifiateNumber, InsuranceTenantSure ts) {

    }

    private void createTenantSureSubscriberRecord(final String insuranceCertificateNumber) {
        final Pmc pmc = VistaDeployment.getCurrentPmc();
        TaskRunner.runInOperationsNamespace(new Callable<VoidSerializable>() {
            @Override
            public VoidSerializable call() throws Exception {
                TenantSureSubscribers tenantSureSubscriber = EntityFactory.create(TenantSureSubscribers.class);
                tenantSureSubscriber.pmc().set(pmc);
                tenantSureSubscriber.certificateNumber().setValue(insuranceCertificateNumber);
                Persistence.service().persist(tenantSureSubscriber);
                return null;
            }
        });
    }

    @Override
    public TenantSureTenantInsuranceStatusDetailedDTO getStatus(Tenant tenantId) {
        InsuranceTenantSure insuranceTenantSure = retrieveActiveInsuranceTenantSure(tenantId);
        if (insuranceTenantSure == null) {
            throw new UserRuntimeException("Failed to retrieve TenantSure status. Probably you don't have active TenantSure insurance.");
        }

        TenantSureTenantInsuranceStatusDetailedDTO status = EntityFactory.create(TenantSureTenantInsuranceStatusDetailedDTO.class);
        status.insuranceCertificateNumber().setValue(insuranceTenantSure.insuranceCertificateNumber().getValue());

        status.quote().coverage().personalLiabilityCoverage().setValue(insuranceTenantSure.liabilityCoverage().getValue());
        status.quote().coverage().contentsCoverage().setValue(insuranceTenantSure.details().contentsCoverage().getValue());
        status.quote().coverage().deductible().setValue(insuranceTenantSure.details().deductible().getValue());
        status.quote().coverage().inceptionDate().setValue(insuranceTenantSure.inceptionDate().getValue());

        status.quote().premium().setValue(insuranceTenantSure.details().grossPremium().getValue());
        status.quote().underwriterFee().setValue(insuranceTenantSure.details().underwriterFee().getValue());
        status.quote().taxBreakdown().addAll(insuranceTenantSure.details().taxes());
        status.quote().totalMonthlyPayable().setValue(insuranceTenantSure.monthlyPayable().getValue());

        status.expiryDate().setValue(insuranceTenantSure.expiryDate().getValue());

        if (insuranceTenantSure.status().getValue() == TenantSureStatus.PendingCancellation) {
            TenantSureMessageDTO message = status.messages().$();
            if (insuranceTenantSure.cancellation().getValue() == CancellationType.SkipPayment) {
                status.isPaymentFailed().setValue(true);
                message.messageText()
                        .setValue(
                                i18n.tr("There was a problem with your last scheduled payment. If you don't update your credit card details until {0,date,short}, your TeantSure insurance will expire",
                                        getGracePeriodEndDate(insuranceTenantSure)));
            } else {
                message.messageText().setValue(
                        i18n.tr("Your insurance has been cancelled and will expire on {0,date,short}", insuranceTenantSure.expiryDate().getValue()));
            }
            status.messages().add(message);
        } else if (insuranceTenantSure.status().getValue() != TenantSureStatus.Cancelled) {
            status.nextPaymentDate().setValue(TenantSurePayments.getNextPaymentDate(insuranceTenantSure));
        }

        return status;

    }

    @Override
    public void cancelByTenant(Tenant tenantId) {
        InsuranceTenantSure insuranceTenantSure = retrieveActiveInsuranceTenantSure(tenantId);
        if (insuranceTenantSure == null) {
            throw new UserRuntimeException("Failed to retrieve TenantSure status. Probably you don't have active TenantSure insurance.");
        }
        validateIsCancellable(insuranceTenantSure);

        LogicalDate expiryDate = cfcApiClient.cancel(insuranceTenantSure.insuranceCertificateNumber().getValue(),
                com.propertyvista.biz.tenant.insurance.ICfcApiClient.CancellationType.PROACTIVE, getTenantsEmail(tenantId));

        insuranceTenantSure.cancellationDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
        insuranceTenantSure.status().setValue(TenantSureStatus.PendingCancellation);
        insuranceTenantSure.cancellation().setValue(CancellationType.CancelledByTenant);
        insuranceTenantSure.expiryDate().setValue(expiryDate);

        Persistence.service().merge(insuranceTenantSure);
        Persistence.service().commit();
    }

    @Override
    public void cancelDueToSkippedPayment(Tenant tenantId) {
        InsuranceTenantSure insuranceTenantSure = retrieveActiveInsuranceTenantSure(tenantId);
        validateIsCancellable(insuranceTenantSure);

        LogicalDate expiryDate = cfcApiClient.cancel(insuranceTenantSure.insuranceCertificateNumber().getValue(),
                com.propertyvista.biz.tenant.insurance.ICfcApiClient.CancellationType.RETROACTIVE, getTenantsEmail(tenantId));

        insuranceTenantSure.cancellationDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
        insuranceTenantSure.status().setValue(TenantSureStatus.Cancelled);
        insuranceTenantSure.cancellation().setValue(CancellationType.SkipPayment);
        insuranceTenantSure.expiryDate().setValue(expiryDate);

        Persistence.service().merge(insuranceTenantSure);
        Persistence.service().commit();
    }

    @Override
    public void startCancellationDueToSkippedPayment(Tenant tenantId) {
        InsuranceTenantSure insuranceTenantSure = retrieveActiveInsuranceTenantSure(tenantId);
        validateIsCancellable(insuranceTenantSure);

        insuranceTenantSure.status().setValue(TenantSureStatus.PendingCancellation);
        insuranceTenantSure.cancellation().setValue(CancellationType.SkipPayment);
        Persistence.service().merge(insuranceTenantSure);

        sendPaymentNotProcessedEmail(getTenantsEmail(tenantId), getGracePeriodEndDate(insuranceTenantSure));

        Persistence.service().commit();
    }

    @Override
    public void reverseCancellationDueToSkippedPayment(Tenant tenantId) {
        InsuranceTenantSure insuranceTenantSure = retrieveActiveInsuranceTenantSure(tenantId);
        validateIsCancellable(insuranceTenantSure);

        sendPaymentsResumedEmail(getTenantsEmail(tenantId));

        insuranceTenantSure.status().setValue(TenantSureStatus.Active);
        insuranceTenantSure.cancellation().setValue(null);
        Persistence.service().merge(insuranceTenantSure);

    }

    /** Warning: this method is not implemented properly */
    @Deprecated
    @Override
    public void cancelByTenantSure(Tenant tenantId, String cancellationReason, LogicalDate expiryDate) {
        if (true) {
            throw new Error("this is not implemented!!!");
        }
        InsuranceTenantSure insuranceTenantSure = retrieveActiveInsuranceTenantSure(tenantId);
        validateIsCancellable(insuranceTenantSure);

        insuranceTenantSure.cancellationDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
        insuranceTenantSure.status().setValue(expiryDate.compareTo(new LogicalDate()) < 0 ? TenantSureStatus.PendingCancellation : TenantSureStatus.Cancelled);
        insuranceTenantSure.cancellation().setValue(CancellationType.CancelledByTenantSure);
        insuranceTenantSure.cancellationDescriptionReasonFromTenantSure().setValue(cancellationReason);

        insuranceTenantSure.expiryDate().setValue(expiryDate);
        Persistence.service().merge(insuranceTenantSure);

        Persistence.service().commit();
    }

    @Override
    public void reinstate(Tenant tenantId) {
        InsuranceTenantSure insuranceTenantSure = retrieveActiveInsuranceTenantSure(tenantId);
        if (insuranceTenantSure.status().getValue() != InsuranceTenantSure.TenantSureStatus.PendingCancellation) {
            throw new UserRuntimeException(i18n.tr("Cannot be reinstanted because it's not cancelled"));
        }
        insuranceTenantSure.cancellationDate().setValue(null);
        insuranceTenantSure.status().setValue(InsuranceTenantSure.TenantSureStatus.Active);
        insuranceTenantSure.cancellation().setValue(null);
        insuranceTenantSure.expiryDate().setValue(null);

        Persistence.service().merge(insuranceTenantSure);

        String tenantsEmail = getTenantsEmail(tenantId);
        cfcApiClient.reinstate(insuranceTenantSure.insuranceCertificateNumber().getValue(), ReinstatementType.REINSTATEMENT_PROACTIVE, tenantsEmail);

        Persistence.service().commit();
    }

    private InsuranceTenantSureClient initializeClient(Tenant tenantId, String name, String phone) {
        EntityQueryCriteria<InsuranceTenantSureClient> criteria = EntityQueryCriteria.create(InsuranceTenantSureClient.class);
        criteria.eq(criteria.proto().tenant(), tenantId);
        InsuranceTenantSureClient tenantSureClient = Persistence.service().retrieve(criteria);
        if (tenantSureClient == null) {
            tenantSureClient = EntityFactory.create(InsuranceTenantSureClient.class);
            tenantSureClient.tenant().set(Persistence.service().retrieve(Tenant.class, tenantId.getPrimaryKey()));
            String clientReferenceNumber = cfcApiClient.createClient(tenantId, name, phone);
            tenantSureClient.clientReferenceNumber().setValue(clientReferenceNumber);
            Persistence.service().persist(tenantSureClient);
            Persistence.service().commit();
        }
        return tenantSureClient;
    }

    private InsuranceTenantSure retrieveActiveInsuranceTenantSure(Tenant tenantId) {
        EntityQueryCriteria<InsuranceTenantSure> criteria = EntityQueryCriteria.create(InsuranceTenantSure.class);
        criteria.add(PropertyCriterion.ne(criteria.proto().status(), InsuranceTenantSure.TenantSureStatus.Draft));
        criteria.add(PropertyCriterion.ne(criteria.proto().status(), InsuranceTenantSure.TenantSureStatus.Failed));
        criteria.or(PropertyCriterion.eq(criteria.proto().status(), InsuranceTenantSure.TenantSureStatus.Active),
                PropertyCriterion.eq(criteria.proto().status(), InsuranceTenantSure.TenantSureStatus.PendingCancellation));
        criteria.add(PropertyCriterion.eq(criteria.proto().tenant().lease().leaseParticipants(), tenantId));

        InsuranceTenantSure insurance = Persistence.service().retrieve(criteria);
        return insurance;
    }

    private void validateIsCancellable(InsuranceTenantSure insuranceTenantSure) {
        Validate.notNull(insuranceTenantSure, "no active TenantSure insurance was found");
        if (insuranceTenantSure.status().getValue() != TenantSureStatus.Active) {
            throw new Error("It's impossible to cancel a tenant sure insurance which is not " + TenantSureStatus.Active);
        }
    }

    @Override
    public void sendDocumentation(Tenant tenantId, String email) {
        InsuranceTenantSure insuranceTenantSure = retrieveActiveInsuranceTenantSure(tenantId);

        List<String> emails = new ArrayList<String>();
        SMTPMailServiceConfig mailConfig = (SMTPMailServiceConfig) ServerSideConfiguration.instance().getMailServiceConfigConfiguration();
        if (CommonsStringUtils.isStringSet(mailConfig.getForwardAllTo())) {
            emails.add(mailConfig.getForwardAllTo());
        } else {
            emails.add(email);
        }
        cfcApiClient.requestDocument(insuranceTenantSure.quoteId().getValue(), emails);
    }

    private String getTenantsEmail(Tenant tenantId) {
        Tenant tenant = Persistence.service().retrieve(Tenant.class, tenantId.getPrimaryKey());

        String tenantsEmail;

        SMTPMailServiceConfig mailConfig = (SMTPMailServiceConfig) ServerSideConfiguration.instance().getMailServiceConfigConfiguration();
        if (CommonsStringUtils.isStringSet(mailConfig.getForwardAllTo())) {
            tenantsEmail = mailConfig.getForwardAllTo();
        } else {
            tenantsEmail = tenant.customer().user().email().getValue();
        }
        return tenantsEmail;
    }

    private LogicalDate getGracePeriodEndDate(InsuranceTenantSure insuranceTenantSure) {
        GregorianCalendar gracePeriodEnd = new GregorianCalendar();
        gracePeriodEnd.setTime(TenantSurePayments.getNextPaymentDate(insuranceTenantSure));
        gracePeriodEnd.add(GregorianCalendar.DATE, TenantSureConstants.TENANTSURE_SKIPPED_PAYMENT_GRACE_PERIOD_DAYS);
        return new LogicalDate(gracePeriodEnd.getTime());
    }

    private void sendPaymentNotProcessedEmail(String tenantEmail, LogicalDate gracePeriodEndDate) {
        ServerSideFactory.create(CommunicationFacade.class).sendPaymentNotProcessedEmail(tenantEmail, gracePeriodEndDate);
    }

    private void sendPaymentsResumedEmail(String tenantEmail) {
        ServerSideFactory.create(CommunicationFacade.class).sendPaymentsResumedEmail(tenantEmail);

    }

}
