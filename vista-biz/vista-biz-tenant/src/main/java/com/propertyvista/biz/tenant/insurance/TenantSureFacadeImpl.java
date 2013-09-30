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
import java.util.Arrays;
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
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.server.mail.SMTPMailServiceConfig;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.biz.tenant.insurance.CfcApiAdapterFacade.ReinstatementType;
import com.propertyvista.biz.tenant.insurance.tenantsure.errors.CfcApiException;
import com.propertyvista.biz.tenant.insurance.tenantsure.errors.TooManyPreviousClaimsException;
import com.propertyvista.biz.tenant.insurance.tenantsure.rules.TenantSurePaymentScheduleFactory;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicyReport;
import com.propertyvista.domain.tenant.insurance.TenantSureTransaction;
import com.propertyvista.domain.tenant.insurance.TenantSureConstants;
import com.propertyvista.domain.tenant.insurance.TenantSureInsuranceCertificate;
import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicy;
import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicy.CancellationType;
import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicy.TenantSureStatus;
import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicyClient;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.operations.domain.tenantsure.TenantSureSubscribers;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.TenantSureCoverageDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.TenantSureCoverageDTO.PreviousClaims;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.TenantSureQuoteDTO;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.status.TenantSureCertificateSummaryDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureMessageDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSurePaymentItemDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSurePaymentItemTaxDTO;
import com.propertyvista.server.jobs.TaskRunner;

public class TenantSureFacadeImpl implements TenantSureFacade {

    private static final I18n i18n = I18n.get(TenantSureFacadeImpl.class);

    private static final Logger log = LoggerFactory.getLogger(TenantSureFacadeImpl.class);

    private static final int TENANT_SURE_CLIENT_INIT_MUTEX_COUNT = 10;

    private static final Object[] TENANT_SURE_CLIENT_INIT_MUTEX;
    static {
        TENANT_SURE_CLIENT_INIT_MUTEX = new Object[TENANT_SURE_CLIENT_INIT_MUTEX_COUNT];
        for (int i = 0; i < TENANT_SURE_CLIENT_INIT_MUTEX_COUNT; ++i) {
            TENANT_SURE_CLIENT_INIT_MUTEX[i] = new Object();
        }
    }

    public TenantSureFacadeImpl() {
    }

    @Override
    public InsurancePaymentMethod getPaymentMethod(Tenant tenantId) {
        return TenantSurePayments.getPaymentMethod(tenantId);
    }

    @Override
    public TenantSureQuoteDTO getQuote(TenantSureCoverageDTO coverage, Tenant tenantId) {
        TenantSureInsurancePolicyClient client = initializeClient(tenantId, coverage.tenantName().getValue(), coverage.tenantPhone().getValue());
        boolean isTooManyPrevClaims = false;

        TenantSureQuoteDTO quote = null;

        try {
            if (coverage.numberOfPreviousClaims().getValue() != PreviousClaims.MoreThanTwo) {
                quote = ServerSideFactory.create(CfcApiAdapterFacade.class).getQuote(client, coverage);
            } else {
                isTooManyPrevClaims = true;
            }
        } catch (TooManyPreviousClaimsException e) {
            isTooManyPrevClaims = true;
        } catch (CfcApiException e) {
            log.error("failed to get a quote due to CFCAPI error", e);
            throw new UserRuntimeException(i18n.tr("Failed to get a quote, please try again."), e);
        }
        if (isTooManyPrevClaims) {
            quote = EntityFactory.create(TenantSureQuoteDTO.class);
            quote.specialQuote().setValue(i18n.tr("Please call TenantSure {0} to get your quote.", TenantSureConstants.TENANTSURE_PHONE_NUMBER));
        }
        if (quote == null) {
            throw new Error("Failed to get a quote for impossible reason");
        }
        return quote;

    }

    /**
     * Only update credit card, do not perform outstanding payment
     */
    @Override
    public InsurancePaymentMethod savePaymentMethod(InsurancePaymentMethod paymentMethod, Tenant tenantId) {
        return TenantSurePayments.savePaymentMethod(paymentMethod, tenantId);
    }

    @Override
    public InsurancePaymentMethod updatePaymentMethod(InsurancePaymentMethod paymentMethod, Tenant tenantId) {
        {
            InsurancePaymentMethod originalpaymentMethod = TenantSurePayments.getPaymentMethod(tenantId);
            if (originalpaymentMethod != null) {
                originalpaymentMethod.isDeleted().setValue(Boolean.TRUE);
                Persistence.service().persist(originalpaymentMethod);
            }

            paymentMethod = TenantSurePayments.savePaymentMethod(paymentMethod, tenantId);

            Persistence.service().commit();
        }
        TenantSureInsurancePolicy insuranceTenantSure = retrieveActiveInsuranceTenantSure(tenantId);
        if (insuranceTenantSure.status().getValue() == TenantSureStatus.PendingCancellation) {
            TenantSurePayments.performOutstandingPayment(insuranceTenantSure);
            reverseCancellationDueToSkippedPayment(tenantId);
        }
        return paymentMethod;
    }

    /**
     * Function implements this: http://jira.birchwoodsoftwaregroup.com/wiki/pages/viewpage.action?pageId=10027234
     */
    @Override
    public void buyInsurance(TenantSureQuoteDTO quote, Tenant tenantId, String tenantPhone, String tenantName) {
        Validate.isTrue(!quote.quoteId().isNull(), "it's impossible to buy insurance with no quote id!!!");

        TenantSureInsurancePolicy insuranceTenantSure = EntityFactory.create(TenantSureInsurancePolicy.class);
        insuranceTenantSure.tenant().set(tenantId);
        insuranceTenantSure.quoteId().setValue(quote.quoteId().getValue());
        insuranceTenantSure.client().set(initializeClient(tenantId, tenantPhone, tenantName));
        insuranceTenantSure.status().setValue(TenantSureStatus.Draft);
        insuranceTenantSure.isDeleted().setValue(Boolean.TRUE);

        insuranceTenantSure.contentsCoverage().setValue(quote.coverage().contentsCoverage().getValue());
        insuranceTenantSure.deductible().setValue(quote.coverage().deductible().getValue());

        insuranceTenantSure.paymentSchedule().setValue(quote.paymentSchedule().getValue());
        insuranceTenantSure.annualPremium().setValue(quote.annualPremium().getValue());
        insuranceTenantSure.underwriterFee().setValue(quote.underwriterFee().getValue());
        insuranceTenantSure.totalAnnualTax().setValue(quote.totalAnnualTax().getValue());
        insuranceTenantSure.totalAnnualPayable().setValue(quote.totalAnnualPayable().getValue());
        insuranceTenantSure.totalMonthlyPayable().setValue(quote.totalMonthlyPayable().getValue());
        insuranceTenantSure.totalAnniversaryFirstMonthPayable().setValue(quote.totalAnniversaryFirstMonthPayable().getValue());
        insuranceTenantSure.totalFirstPayable().setValue(quote.totalFirstPayable().getValue());

        TenantSureInsuranceCertificate certificate = EntityFactory.create(TenantSureInsuranceCertificate.class);
        certificate.insuranceCertificateNumber().setValue(null); // we will get certificate number later: after we have managed to preauthorize a payment transaction
        certificate.insuranceProvider().setValue(TenantSureConstants.TENANTSURE_LEGAL_NAME);
        certificate.liabilityCoverage().setValue(quote.coverage().personalLiabilityCoverage().getValue());
        certificate.inceptionDate().setValue(new LogicalDate());
        certificate.expiryDate().setValue(null); // TODO actually this is one year, but maybe it's supposed to be renewed somehow
        insuranceTenantSure.certificate().set(certificate);

        Persistence.service().merge(insuranceTenantSure);

        // Start payment
        InsurancePaymentMethod paymentMethod = TenantSurePayments.getPaymentMethod(tenantId);
        TenantSureTransaction transaction = TenantSurePaymentScheduleFactory.create(insuranceTenantSure.paymentSchedule().getValue())
                .initFirstTransaction(insuranceTenantSure, paymentMethod);
        Persistence.service().persist(transaction);

        Persistence.service().commit();

        String tenantSureCertificateNumber = null;
        // Like two phase commit transaction
        {
            try {
                transaction = TenantSurePayments.preAuthorization(transaction);
            } catch (Throwable e) {
                log.error("Error", e);
                transaction.status().setValue(TenantSureTransaction.TransactionStatus.AuthorizationRejected);
                Persistence.service().persist(transaction);
                insuranceTenantSure.status().setValue(TenantSureStatus.Failed);

                transaction.paymentMethod().isDeleted().setValue(Boolean.TRUE);
                Persistence.service().merge(transaction.paymentMethod());

                Persistence.service().persist(insuranceTenantSure);
                Persistence.service().commit();
                if (e instanceof UserRuntimeException) {
                    throw (UserRuntimeException) e;
                } else {
                    throw new UserRuntimeException(i18n.tr("Credit Card Authorization failed"), e);
                }
            }
            transaction.status().setValue(TenantSureTransaction.TransactionStatus.Authorized);
            Persistence.service().persist(transaction);
            Persistence.service().commit();

            try {
                tenantSureCertificateNumber = ServerSideFactory.create(CfcApiAdapterFacade.class).bindQuote(insuranceTenantSure.quoteId().getValue());
            } catch (Throwable e) {
                log.error("failed to bind quote. ", e);
                insuranceTenantSure.status().setValue(TenantSureStatus.Failed);

                TenantSurePayments.preAuthorizationReversal(transaction);
                transaction.status().setValue(TenantSureTransaction.TransactionStatus.AuthorizationReversal);
                Persistence.service().persist(transaction);

                Persistence.service().persist(insuranceTenantSure);
                Persistence.service().commit();
                if (e instanceof UserRuntimeException) {
                    throw (UserRuntimeException) e;
                } else {
                    throw new UserRuntimeException(i18n.tr("Insurance bind failed"), e);
                }
            }

            insuranceTenantSure.certificate().insuranceCertificateNumber().setValue(tenantSureCertificateNumber);
            insuranceTenantSure.status().setValue(TenantSureStatus.Active);
            insuranceTenantSure.paymentDay().setValue(TenantSurePayments.calulatePaymentDay(insuranceTenantSure.certificate().inceptionDate().getValue()));
            insuranceTenantSure.isDeleted().setValue(Boolean.FALSE);

            Persistence.service().merge(insuranceTenantSure);

            createTenantSureSubscriberRecord(tenantSureCertificateNumber);

            TenantSureInsurancePolicyReport tsReportStatusHolder = EntityFactory.create(TenantSureInsurancePolicyReport.class);
            tsReportStatusHolder.insurance().set(insuranceTenantSure);
            Persistence.service().persist(tsReportStatusHolder);

            Persistence.service().commit();

            try {
                List<String> emails = new ArrayList<String>();
                emails.add(getTenantsEmail(tenantId));
                ServerSideFactory.create(CfcApiAdapterFacade.class).requestDocument(insuranceTenantSure.certificate().insuranceCertificateNumber().getValue(),
                        emails);
            } catch (Throwable e) {
                log.error("Error sending TenantSure document", e);
            }

        }

        try {
            TenantSurePayments.compleateTransaction(transaction);
        } catch (Throwable e) {
            log.error("Error", e);

            transaction.status().setValue(TenantSureTransaction.TransactionStatus.AuthorizedPaymentRejectedRetry);
            Persistence.service().persist(transaction);

            insuranceTenantSure.status().setValue(TenantSureStatus.Pending);
            Persistence.service().persist(insuranceTenantSure);
            Persistence.service().commit();
            if (e instanceof UserRuntimeException) {
                throw (UserRuntimeException) e;
            } else {
                throw new UserRuntimeException(i18n.tr("Credit Card payment failed, payment transaction would be completed later"), e);
            }
        }
        transaction.status().setValue(TenantSureTransaction.TransactionStatus.Cleared);
        Persistence.service().persist(transaction);
        Persistence.service().commit();

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
    public TenantSureCertificateSummaryDTO getStatus(Tenant tenantId) {
        TenantSureInsurancePolicy insuranceTenantSure = retrieveActiveInsuranceTenantSure(tenantId);
        if (insuranceTenantSure == null) {
            return null;
        }

        TenantSureCertificateSummaryDTO status = EntityFactory.create(TenantSureCertificateSummaryDTO.class);
        status.insuranceCertificateNumber().setValue(insuranceTenantSure.certificate().insuranceCertificateNumber().getValue());
        status.expiryDate().setValue(insuranceTenantSure.certificate().expiryDate().getValue());

        status.liabilityCoverage().setValue(insuranceTenantSure.certificate().liabilityCoverage().getValue());
        status.contentsCoverage().setValue(insuranceTenantSure.contentsCoverage().getValue());
        status.deductible().setValue(insuranceTenantSure.deductible().getValue());
        status.inceptionDate().setValue(insuranceTenantSure.certificate().inceptionDate().getValue());

        status.annualPaymentDetails().paymentDate().setValue(null);
        status.annualPaymentDetails().paymentBreakdown().add(makePaymentItem(//@formatter:off
                insuranceTenantSure.annualPremium().getMeta().getCaption(),
                insuranceTenantSure.annualPremium().getValue(),
                new ArrayList<TenantSurePaymentItemTaxDTO>()
        ));//@formatter:off
        status.annualPaymentDetails().paymentBreakdown().add(makePaymentItem(//@formatter:off
                insuranceTenantSure.underwriterFee().getMeta().getCaption(),
                insuranceTenantSure.underwriterFee().getValue(),
                new ArrayList<TenantSurePaymentItemTaxDTO>()
                ));//@formatter:off
        status.annualPaymentDetails().paymentBreakdown().add(makePaymentItem(//@formatter:off
                insuranceTenantSure.totalAnnualTax().getMeta().getCaption(),
                insuranceTenantSure.totalAnnualTax().getValue(),
                new ArrayList<TenantSurePaymentItemTaxDTO>()
                ));//@formatter:off
        status.annualPaymentDetails().total().setValue(insuranceTenantSure.totalAnnualPayable().getValue());

        status.nextPaymentDetails().paymentDate().setValue(TenantSurePayments.getNextPaymentDate(insuranceTenantSure));
        BigDecimal thisMonthlyPayable = TenantSurePayments.getMonthlyPayable(insuranceTenantSure, status.nextPaymentDetails().paymentDate().getValue());
        status.nextPaymentDetails().paymentBreakdown().add(makePaymentItem(//@formatter:off
                    i18n.tr("Premium + Tax"), 
                    thisMonthlyPayable,
                    new ArrayList<TenantSurePaymentItemTaxDTO>()
        ));//@formatter:on
        status.nextPaymentDetails().total().setValue(thisMonthlyPayable);

        if (insuranceTenantSure.status().getValue() == TenantSureStatus.PendingCancellation) {
            TenantSureMessageDTO message = status.messages().$();
            if (insuranceTenantSure.cancellation().getValue() == CancellationType.SkipPayment) {
                status.isPaymentFailed().setValue(true);
                message.messageText().setValue(i18n.tr(//@formatter:off
                        "There was a problem with your last scheduled payment. If you don''t update your credit card details until {0,date,short}, your TenantSure insurance will expire.",
                        getGracePeriodEndDate(insuranceTenantSure)
                ));//@formatter:on
            } else {
                message.messageText().setValue(i18n.tr(//@formatter:off
                        "Your insurance has been cancelled and will expire on {0,date,short}",
                        insuranceTenantSure.certificate().expiryDate().getValue()
                ));//@formatter:on
            }
            status.messages().add(message);

        }

        return status;

    }

    @Override
    public void scheduleCancelByTenant(Tenant tenantId) {
        TenantSureInsurancePolicy insuranceTenantSure = retrieveActiveInsuranceTenantSure(tenantId);
        if (insuranceTenantSure == null) {
            throw new UserRuntimeException("Failed to retrieve TenantSure status. Probably you don't have active TenantSure insurance.");
        }
        validateIsCancellable(insuranceTenantSure);

        try {
            LogicalDate expiryDate = ServerSideFactory.create(CfcApiAdapterFacade.class).cancel(
                    insuranceTenantSure.certificate().insuranceCertificateNumber().getValue(), CfcApiAdapterFacade.CancellationType.PROACTIVE,
                    getTenantsEmail(tenantId));

            insuranceTenantSure.cancellationDate().setValue(new LogicalDate(SystemDateManager.getDate()));
            insuranceTenantSure.status().setValue(TenantSureStatus.PendingCancellation);
            insuranceTenantSure.cancellation().setValue(CancellationType.CancelledByTenant);
            insuranceTenantSure.certificate().expiryDate().setValue(expiryDate);

            Persistence.service().merge(insuranceTenantSure);
            Persistence.service().commit();
        } catch (CfcApiException e) {
            log.error("Failed to reinstate insurace for tenant " + tenantId.getPrimaryKey() + "'", e);
            throw new UserRuntimeException(i18n.tr("Failed to cancel due to TenantSure interface error"));
        }
    }

    @Override
    public void cancelDueToSkippedPayment(Tenant tenantId) {
        TenantSureInsurancePolicy insuranceTenantSure = retrieveActiveInsuranceTenantSure(tenantId);
        if (insuranceTenantSure.status().getValue() != TenantSureStatus.PendingCancellation
                & insuranceTenantSure.cancellation().getValue() != CancellationType.SkipPayment) {
            throw new IllegalStateException("insurance should ped pending cancellationd due to skipped payment to proceed (insurance pk = "
                    + insuranceTenantSure.getPrimaryKey());
        }
        try {
            LogicalDate expiryDate = ServerSideFactory.create(CfcApiAdapterFacade.class).cancel(
                    insuranceTenantSure.certificate().insuranceCertificateNumber().getValue(), CfcApiAdapterFacade.CancellationType.RETROACTIVE,
                    getTenantsEmail(tenantId));

            insuranceTenantSure.cancellationDate().setValue(new LogicalDate(SystemDateManager.getDate()));
            insuranceTenantSure.status().setValue(TenantSureStatus.Cancelled);
            insuranceTenantSure.cancellation().setValue(CancellationType.SkipPayment);
            insuranceTenantSure.certificate().expiryDate().setValue(expiryDate);

            Persistence.service().merge(insuranceTenantSure);
        } catch (CfcApiException e) {
            throw new Error(e);
        }
    }

    @Override
    public void startCancellationDueToSkippedPayment(Tenant tenantId) {
        TenantSureInsurancePolicy insuranceTenantSure = retrieveActiveInsuranceTenantSure(tenantId);
        validateIsCancellable(insuranceTenantSure);

        insuranceTenantSure.status().setValue(TenantSureStatus.PendingCancellation);
        insuranceTenantSure.cancellation().setValue(CancellationType.SkipPayment);
        Persistence.service().merge(insuranceTenantSure);

        sendPaymentNotProcessedEmail(getTenantsEmail(tenantId), getGracePeriodEndDate(insuranceTenantSure),
                TenantSurePayments.getNextPaymentDate(insuranceTenantSure));

        Persistence.service().commit();
    }

    @Override
    public void reverseCancellationDueToSkippedPayment(Tenant tenantId) {
        TenantSureInsurancePolicy insuranceTenantSure = retrieveActiveInsuranceTenantSure(tenantId);
        //TODO Assert 15 days.
        if (insuranceTenantSure.status().getValue() != TenantSureStatus.PendingCancellation) {
            throw new Error("It's impossible to activate a tenant sure insurance which is not " + TenantSureStatus.PendingCancellation);
        }

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
        TenantSureInsurancePolicy insuranceTenantSure = retrieveActiveInsuranceTenantSure(tenantId);
        validateIsCancellable(insuranceTenantSure);

        insuranceTenantSure.cancellationDate().setValue(new LogicalDate(SystemDateManager.getDate()));
        insuranceTenantSure.status().setValue(expiryDate.compareTo(new LogicalDate()) < 0 ? TenantSureStatus.PendingCancellation : TenantSureStatus.Cancelled);
        insuranceTenantSure.cancellation().setValue(CancellationType.CancelledByTenantSure);
        insuranceTenantSure.cancellationDescriptionReasonFromTenantSure().setValue(cancellationReason);

        insuranceTenantSure.certificate().expiryDate().setValue(expiryDate);
        Persistence.service().merge(insuranceTenantSure);

        Persistence.service().commit();
    }

    @Override
    public void reinstate(Tenant tenantId) {
        TenantSureInsurancePolicy insuranceTenantSure = retrieveActiveInsuranceTenantSure(tenantId);
        if (insuranceTenantSure.status().getValue() != TenantSureStatus.PendingCancellation) {
            throw new UserRuntimeException(i18n.tr("Cannot be reinstanted because it's not cancelled"));
        }
        insuranceTenantSure.cancellationDate().setValue(null);
        insuranceTenantSure.status().setValue(TenantSureStatus.Active);
        insuranceTenantSure.cancellation().setValue(null);
        insuranceTenantSure.certificate().expiryDate().setValue(null);

        Persistence.service().merge(insuranceTenantSure);

        String tenantsEmail = getTenantsEmail(tenantId);
        try {
            ServerSideFactory.create(CfcApiAdapterFacade.class).reinstate(insuranceTenantSure.certificate().insuranceCertificateNumber().getValue(),
                    ReinstatementType.REINSTATEMENT_PROACTIVE, tenantsEmail);

            Persistence.service().commit();
        } catch (CfcApiException e) {
            log.error("Failed to reinstate insurance for tenant " + tenantId.getPrimaryKey() + "'", e);
            throw new UserRuntimeException(i18n.tr("Failed to reinstate due to TenantSure interface error"), e);
        }
    }

    private TenantSureInsurancePolicyClient initializeClient(Tenant tenantId, String name, String phone) {
        Object mutex = TENANT_SURE_CLIENT_INIT_MUTEX[(int) tenantId.getPrimaryKey().asLong() % TENANT_SURE_CLIENT_INIT_MUTEX_COUNT];

        synchronized (mutex) {
            EntityQueryCriteria<TenantSureInsurancePolicyClient> criteria = EntityQueryCriteria.create(TenantSureInsurancePolicyClient.class);
            criteria.eq(criteria.proto().tenant(), tenantId);
            TenantSureInsurancePolicyClient tenantSureClient = Persistence.service().retrieve(criteria);
            if (tenantSureClient == null) {
                tenantSureClient = EntityFactory.create(TenantSureInsurancePolicyClient.class);
                tenantSureClient.tenant().set(Persistence.service().retrieve(Tenant.class, tenantId.getPrimaryKey()));
                String clientReferenceNumber = null;
                try {
                    clientReferenceNumber = ServerSideFactory.create(CfcApiAdapterFacade.class).createClient(tenantId, name, phone);
                } catch (CfcApiException e) {
                    log.error("Failed to register tenant '" + tenantId.getPrimaryKey() + "' via CFC API", e);
                    throw new UserRuntimeException(i18n.tr("Failed to register client via TenantSure interface"), e);
                }
                tenantSureClient.clientReferenceNumber().setValue(clientReferenceNumber);
                Persistence.service().persist(tenantSureClient);
                Persistence.service().commit();
            }
            return tenantSureClient;
        }

    }

    private TenantSureInsurancePolicy retrieveActiveInsuranceTenantSure(Tenant tenantId) {
        EntityQueryCriteria<TenantSureInsurancePolicy> criteria = EntityQueryCriteria.create(TenantSureInsurancePolicy.class);
        criteria.add(PropertyCriterion.ne(criteria.proto().status(), TenantSureStatus.Draft));
        criteria.add(PropertyCriterion.ne(criteria.proto().status(), TenantSureStatus.Failed));
        criteria.or(PropertyCriterion.eq(criteria.proto().status(), TenantSureStatus.Active),
                PropertyCriterion.eq(criteria.proto().status(), TenantSureStatus.PendingCancellation));
        criteria.add(PropertyCriterion.eq(criteria.proto().tenant().lease().leaseParticipants(), tenantId));

        TenantSureInsurancePolicy insurance = Persistence.service().retrieve(criteria);
        return insurance;
    }

    private void validateIsCancellable(TenantSureInsurancePolicy insuranceTenantSure) {
        Validate.notNull(insuranceTenantSure, "no active TenantSure insurance was found");
        if (insuranceTenantSure.status().getValue() != TenantSureStatus.Active) {
            throw new Error("It's impossible to cancel a tenant sure insurance which is not " + TenantSureStatus.Active);
        }
    }

    @Override
    public String sendCertificate(Tenant tenantId, String email) {
        List<String> emails = new ArrayList<String>();
        SMTPMailServiceConfig mailConfig = (SMTPMailServiceConfig) ServerSideConfiguration.instance().getMailServiceConfigConfiguration();
        if (CommonsStringUtils.isStringSet(mailConfig.getForwardAllTo())) {
            emails.add(mailConfig.getForwardAllTo());
        } else {
            emails.add(email != null ? email : getTenantsEmail(tenantId));
        }
        TenantSureInsurancePolicy insuranceTenantSure = retrieveActiveInsuranceTenantSure(tenantId);
        try {
            ServerSideFactory.create(CfcApiAdapterFacade.class).requestDocument(insuranceTenantSure.certificate().insuranceCertificateNumber().getValue(),
                    emails);
        } catch (CfcApiException e) {
            log.error("Failed to send certificate to tenant '" + tenantId.getPrimaryKey() + "'", e);
            throw new UserRuntimeException(i18n.tr("Failed to send email due to TenantSure interface error"));
        }
        return emails.get(0);
    }

    @Override
    public String sendQuote(Tenant tenantId, String quoteId) {
        List<String> emails = Arrays.asList(getTenantsEmail(tenantId));
        try {
            ServerSideFactory.create(CfcApiAdapterFacade.class).requestDocument(quoteId, emails);
        } catch (CfcApiException e) {
            log.error("Failed to send quote to tenant '" + tenantId.getPrimaryKey() + "'", e);
            throw new UserRuntimeException(i18n.tr("Failed to send email due to TenantSure interface error"), e);
        }
        return emails.get(0);
    }

    private String getTenantsEmail(Tenant tenantId) {
        Tenant tenant = Persistence.service().retrieve(Tenant.class, tenantId.getPrimaryKey());

        String tenantsEmail;

        SMTPMailServiceConfig mailConfig = (SMTPMailServiceConfig) ServerSideConfiguration.instance().getMailServiceConfigConfiguration();
        if (CommonsStringUtils.isStringSet(mailConfig.getForwardAllTo())) {
            tenantsEmail = mailConfig.getForwardAllTo();
        } else {
            tenantsEmail = tenant.customer().person().email().getValue();
        }
        return tenantsEmail;
    }

    private LogicalDate getGracePeriodEndDate(TenantSureInsurancePolicy insuranceTenantSure) {
        GregorianCalendar gracePeriodEnd = new GregorianCalendar();
        gracePeriodEnd.setTime(TenantSurePayments.getNextPaymentDate(insuranceTenantSure));
        gracePeriodEnd.add(GregorianCalendar.DATE, TenantSureConstants.TENANTSURE_SKIPPED_PAYMENT_GRACE_PERIOD_DAYS);
        return new LogicalDate(gracePeriodEnd.getTime());
    }

    private void sendPaymentNotProcessedEmail(String tenantEmail, LogicalDate gracePeriodEndDate, LogicalDate cancellationDate) {
        ServerSideFactory.create(CommunicationFacade.class).sendTenantSurePaymentNotProcessedEmail(tenantEmail, gracePeriodEndDate, cancellationDate);
    }

    private void sendPaymentsResumedEmail(String tenantEmail) {
        ServerSideFactory.create(CommunicationFacade.class).sendTenantSurePaymentsResumedEmail(tenantEmail);
    }

    private static TenantSurePaymentItemDTO makePaymentItem(String description, BigDecimal amount, List<TenantSurePaymentItemTaxDTO> taxes) {
        TenantSurePaymentItemDTO paymentItem = EntityFactory.create(TenantSurePaymentItemDTO.class);
        paymentItem.description().setValue(description);
        paymentItem.amount().setValue(amount);
        paymentItem.taxBreakdown().addAll(taxes);
        return paymentItem;
    }

}
