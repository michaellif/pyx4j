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

import javax.xml.ws.WebServiceException;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.CompensationHandler;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.server.mail.SMTPMailServiceConfig;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.biz.communication.OperationsNotificationFacade;
import com.propertyvista.biz.financial.payment.CreditCardTransactionResponse;
import com.propertyvista.biz.tenant.insurance.CfcApiAdapterFacade.ReinstatementType;
import com.propertyvista.biz.tenant.insurance.errors.CfcApiException;
import com.propertyvista.biz.tenant.insurance.errors.TooManyPreviousClaimsException;
import com.propertyvista.biz.tenant.insurance.tenantsure.rules.TenantSurePaymentScheduleFactory;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.security.CustomerSignature;
import com.propertyvista.domain.tenant.insurance.TenantSureConstants;
import com.propertyvista.domain.tenant.insurance.TenantSureCoverage;
import com.propertyvista.domain.tenant.insurance.TenantSureInsuranceCertificate;
import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicy;
import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicy.CancellationType;
import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicy.TenantSureStatus;
import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicyClient;
import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicyReport;
import com.propertyvista.domain.tenant.insurance.TenantSureTransaction;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.operations.domain.tenantsure.TenantSureSubscribers;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.TenantSureCoverageDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.TenantSureCoverageDTO.PreviousClaims;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.TenantSureInsurancePolicyDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.TenantSureQuoteDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureMessageDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSurePaymentItemDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSurePaymentItemTaxDTO;
import com.propertyvista.server.TaskRunner;

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

    private static final int TENANT_SURE_PURCHASE_MUTEX_COUNT = 20;

    private static final Object[] TENANT_SURE_PURCHASE_MUTEX;
    static {
        TENANT_SURE_PURCHASE_MUTEX = new Object[TENANT_SURE_PURCHASE_MUTEX_COUNT];
        for (int i = 0; i < TENANT_SURE_PURCHASE_MUTEX_COUNT; ++i) {
            TENANT_SURE_PURCHASE_MUTEX[i] = new Object();
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
        } catch (CfcApiException | WebServiceException e) {
            log.error("failed to get a quote due to CFCAPI error", e);
            ServerSideFactory.create(OperationsNotificationFacade.class).sendTenantSureCfcOperationProblem(e);
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
        log.debug("PaymentMethod updated for TenantSureInsurance {}", insuranceTenantSure.quoteId());
        if (insuranceTenantSure.status().getValue() == TenantSureStatus.PendingCancellation) {
            TenantSurePayments.performOutstandingPayment(insuranceTenantSure);
            reverseCancellationDueToSkippedPayment(tenantId);
        }
        return paymentMethod;
    }

    @Override
    public TenantSureInsurancePolicy createDraftPolicy(TenantSureQuoteDTO quote, Tenant tenantId) {
        TenantSureInsurancePolicy tenantSurePolicy = EntityFactory.create(TenantSureInsurancePolicy.class);
        tenantSurePolicy.tenant().set(tenantId);
        tenantSurePolicy.quoteId().setValue(quote.quoteId().getValue());
        tenantSurePolicy.status().setValue(TenantSureStatus.Draft);
        tenantSurePolicy.isDeleted().setValue(Boolean.TRUE);
        tenantSurePolicy.client().set(getClient(tenantId));

        tenantSurePolicy.coverage().set(quote.coverage().duplicate(TenantSureCoverage.class));

        tenantSurePolicy.contentsCoverage().setValue(quote.coverage().contentsCoverage().getValue());
        tenantSurePolicy.deductible().setValue(quote.coverage().deductible().getValue());

        tenantSurePolicy.paymentSchedule().setValue(quote.paymentSchedule().getValue());
        tenantSurePolicy.annualPremium().setValue(quote.annualPremium().getValue());
        tenantSurePolicy.underwriterFee().setValue(quote.underwriterFee().getValue());
        tenantSurePolicy.brokerFee().setValue(quote.brokerFee().getValue());
        tenantSurePolicy.totalAnnualTax().setValue(quote.totalAnnualTax().getValue());
        tenantSurePolicy.totalAnnualPayable().setValue(quote.totalAnnualPayable().getValue());
        tenantSurePolicy.totalMonthlyPayable().setValue(quote.totalMonthlyPayable().getValue());
        tenantSurePolicy.totalAnniversaryFirstMonthPayable().setValue(quote.totalAnniversaryFirstMonthPayable().getValue());
        tenantSurePolicy.totalFirstPayable().setValue(quote.totalFirstPayable().getValue());

        TenantSureInsuranceCertificate certificate = EntityFactory.create(TenantSureInsuranceCertificate.class);
        certificate.insuranceCertificateNumber().setValue(null); // we will get certificate number later: after we have managed to preauthorize a payment transaction
        certificate.insuranceProvider().setValue(TenantSureConstants.TENANTSURE_LEGAL_NAME);
        certificate.liabilityCoverage().setValue(quote.coverage().personalLiabilityCoverage().getValue());
        certificate.inceptionDate().setValue(quote.coverage().inceptionDate().getValue());
        certificate.expiryDate().setValue(null); // TODO actually this is one year, but maybe it's supposed to be renewed somehow
        tenantSurePolicy.certificate().set(certificate);
        return tenantSurePolicy;
    }

    /**
     * Function implements this: http://jira.birchwoodsoftwaregroup.com/wiki/pages/viewpage.action?pageId=10027234
     */
    @Override
    public Key buyInsurance(TenantSureQuoteDTO quote, Tenant tenantId, CustomerSignature signature) {
        Validate.isTrue(!quote.quoteId().isNull(), "it's impossible to buy insurance with no quote id!!!");

        Object mutex = TENANT_SURE_PURCHASE_MUTEX[(int) tenantId.getPrimaryKey().asLong() % TENANT_SURE_PURCHASE_MUTEX_COUNT];
        synchronized (mutex) {
            if (retrieveActiveInsuranceTenantSure(tenantId) != null) {
                throw new UserRuntimeException(i18n.tr("Cannot Buy Insurance: Insurance for this tenant already exists!"));
            }

            TenantSureInsurancePolicy tenantSurePolicy = createDraftPolicy(quote, tenantId);
            tenantSurePolicy.personalDisclaimerSignature().set(signature);

            buyInsurance(tenantSurePolicy);

            return tenantSurePolicy.getPrimaryKey();
        }
    }

    @Override
    public void buyInsurance(final TenantSureInsurancePolicy tenantSurePolicy) {
        // Start payment
        InsurancePaymentMethod paymentMethod = TenantSurePayments.getPaymentMethod(tenantSurePolicy.tenant());
        final TenantSureTransaction transaction = TenantSurePaymentScheduleFactory.create(tenantSurePolicy.paymentSchedule().getValue()).initFirstTransaction(
                tenantSurePolicy, paymentMethod, SystemDateManager.getLogicalDate());

        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {
            @Override
            public Void execute() throws RuntimeException {
                boolean newPolicy = tenantSurePolicy.getPrimaryKey() == null;
                Persistence.service().persist(tenantSurePolicy);
                Persistence.service().persist(transaction);
                if (newPolicy) {
                    createTenantSureSubscriberRecord(tenantSurePolicy);
                }
                return null;
            }
        });

        // Like two phase commit transaction

        try {
            new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {
                @Override
                public Void execute() throws RuntimeException {

                    UnitOfWork.addTransactionCompensationHandler(new CompensationHandler() {

                        @Override
                        public Void execute() {
                            transaction.status().setValue(TenantSureTransaction.TransactionStatus.AuthorizationRejected);
                            Persistence.service().persist(transaction);
                            tenantSurePolicy.status().setValue(TenantSureStatus.Failed);

                            transaction.paymentMethod().isDeleted().setValue(Boolean.TRUE);
                            Persistence.service().merge(transaction.paymentMethod());
                            Persistence.service().persist(tenantSurePolicy);
                            return null;
                        }
                    });

                    CreditCardTransactionResponse response = TenantSurePayments.preAuthorization(transaction);
                    if (response.success().getValue()) {
                        transaction.status().setValue(TenantSureTransaction.TransactionStatus.Authorized);
                    } else {
                        transaction.status().setValue(TenantSureTransaction.TransactionStatus.AuthorizationRejected);
                    }
                    transaction.transactionAuthorizationNumber().setValue(response.authorizationNumber().getValue());
                    transaction.transactionErrorMessage().setValue(response.message().getValue());
                    transaction.transactionDate().setValue(SystemDateManager.getDate());
                    Persistence.service().persist(transaction);

                    if (!response.success().getValue()) {
                        tenantSurePolicy.status().setValue(TenantSureStatus.Failed);
                        transaction.paymentMethod().isDeleted().setValue(Boolean.TRUE);
                        Persistence.service().merge(transaction.paymentMethod());
                        Persistence.service().persist(tenantSurePolicy);
                    }

                    return null;
                }
            });
        } catch (Throwable e) {
            log.error("Error", e);
            if (e instanceof UserRuntimeException) {
                throw (UserRuntimeException) e;
            } else {
                throw new UserRuntimeException(i18n.tr("Credit Card Authorization failed"), e);
            }
        }

        if (transaction.status().getValue() != TenantSureTransaction.TransactionStatus.Authorized) {
            throw new UserRuntimeException(i18n.tr("Credit Card Authorization failed\nTransaction Error Message: {0}", transaction.transactionErrorMessage()));
        }

        try {
            new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, CfcApiException>() {
                @Override
                public Void execute() throws CfcApiException {

                    UnitOfWork.addTransactionCompensationHandler(new CompensationHandler() {

                        @Override
                        public Void execute() {
                            tenantSurePolicy.status().setValue(TenantSureStatus.Failed);

                            TenantSurePayments.preAuthorizationReversal(transaction);
                            transaction.status().setValue(TenantSureTransaction.TransactionStatus.AuthorizationReversal);
                            Persistence.service().persist(transaction);

                            Persistence.service().persist(tenantSurePolicy);
                            return null;
                        }
                    });

                    String tenantSureCertificateNumber = ServerSideFactory.create(CfcApiAdapterFacade.class).bindQuote(tenantSurePolicy.quoteId().getValue());

                    tenantSurePolicy.certificate().insuranceCertificateNumber().setValue(tenantSureCertificateNumber);
                    tenantSurePolicy.status().setValue(TenantSureStatus.Active);
                    tenantSurePolicy.paymentDay().setValue(TenantSurePayments.calulatePaymentDay(tenantSurePolicy.certificate().inceptionDate().getValue()));
                    tenantSurePolicy.isDeleted().setValue(Boolean.FALSE);

                    Persistence.service().merge(tenantSurePolicy);

                    updateTenantSureSubscriberRecord(tenantSurePolicy);

                    TenantSureInsurancePolicyReport tsReportStatusHolder = EntityFactory.create(TenantSureInsurancePolicyReport.class);
                    tsReportStatusHolder.insurance().set(tenantSurePolicy);
                    Persistence.service().persist(tsReportStatusHolder);

                    return null;
                }
            });
        } catch (Throwable e) {
            log.error("failed to bind quote. ", e);
            if (e instanceof UserRuntimeException) {
                throw (UserRuntimeException) e;
            } else {
                throw new UserRuntimeException(i18n.tr("Insurance bind failed"), e);
            }
        }

        try {
            List<String> emails = new ArrayList<String>();
            emails.add(getTenantsEmail(tenantSurePolicy.tenant()));
            ServerSideFactory.create(CfcApiAdapterFacade.class).requestDocument(tenantSurePolicy.certificate().insuranceCertificateNumber().getValue(), emails);
        } catch (Throwable e) {
            log.error("Error sending TenantSure document", e);
        }

        try {
            new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {
                @Override
                public Void execute() throws RuntimeException {

                    UnitOfWork.addTransactionCompensationHandler(new CompensationHandler() {

                        @Override
                        public Void execute() {
                            transaction.status().setValue(TenantSureTransaction.TransactionStatus.AuthorizedPaymentRejectedRetry);
                            Persistence.service().persist(transaction);

                            tenantSurePolicy.status().setValue(TenantSureStatus.Pending);
                            Persistence.service().persist(tenantSurePolicy);
                            return null;
                        }
                    });

                    TenantSurePayments.compleateTransaction(transaction);
                    transaction.status().setValue(TenantSureTransaction.TransactionStatus.Cleared);
                    Persistence.service().persist(transaction);
                    return null;
                }
            });
        } catch (Throwable e) {
            log.error("Error", e);
            if (e instanceof UserRuntimeException) {
                throw (UserRuntimeException) e;
            } else {
                throw new UserRuntimeException(i18n.tr("Credit Card payment failed, payment transaction would be completed later"), e);
            }
        }
    }

    private void updateTenantSureSubscriberRecord(final TenantSureInsurancePolicy tenantSurePolicy) {
        final Pmc pmc = VistaDeployment.getCurrentPmc();
        TaskRunner.runInOperationsNamespace(new Callable<VoidSerializable>() {
            @Override
            public VoidSerializable call() throws Exception {
                TenantSureSubscribers tenantSureSubscriber = Persistence.service().retrieve(TenantSureSubscribers.class, tenantSurePolicy.getPrimaryKey());
                tenantSureSubscriber.pmc().set(pmc);
                tenantSureSubscriber.certificateNumber().setValue(tenantSurePolicy.certificate().insuranceCertificateNumber().getValue());
                Persistence.service().persist(tenantSureSubscriber);
                return null;
            }
        });
    }

    void createTenantSureSubscriberRecord(final TenantSureInsurancePolicy tenantSurePolicy) {
        final Pmc pmc = VistaDeployment.getCurrentPmc();
        TaskRunner.runInOperationsNamespace(new Callable<VoidSerializable>() {
            @Override
            public VoidSerializable call() throws Exception {
                TenantSureSubscribers tenantSureSubscriber = EntityFactory.create(TenantSureSubscribers.class);
                tenantSureSubscriber.setPrimaryKey(tenantSurePolicy.getPrimaryKey());
                tenantSureSubscriber.pmc().set(pmc);
                tenantSureSubscriber.certificateNumber().setValue(tenantSurePolicy.certificate().insuranceCertificateNumber().getValue());
                Persistence.service().persist(tenantSureSubscriber);
                return null;
            }
        });
    }

    @Override
    public TenantSureInsurancePolicyDTO getStatus(Tenant tenantId) {
        TenantSureInsurancePolicy insuranceTenantSure = retrieveActiveInsuranceTenantSure(tenantId);
        if (insuranceTenantSure == null) {
            return null;
        }

        TenantSureInsurancePolicyDTO status = EntityFactory.create(TenantSureInsurancePolicyDTO.class);
        status.setPrimaryKey(insuranceTenantSure.getPrimaryKey());
        status.certificate().insuranceCertificateNumber().setValue(insuranceTenantSure.certificate().insuranceCertificateNumber().getValue());
        status.certificate().expiryDate().setValue(insuranceTenantSure.certificate().expiryDate().getValue());
        status.certificate().inceptionDate().setValue(insuranceTenantSure.certificate().inceptionDate().getValue());
        status.certificate().liabilityCoverage().setValue(insuranceTenantSure.certificate().liabilityCoverage().getValue());
        status.contentsCoverage().setValue(insuranceTenantSure.contentsCoverage().getValue());
        status.deductible().setValue(insuranceTenantSure.deductible().getValue());

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
                status.isCancelled().setValue(true);

                // TODO not sure it's supposed to be here, i think 'TenantSurePayments' shouldn't return values related to next payment if cancelled
                // clear next payment
                status.nextPaymentDetails().set(null);
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

            insuranceTenantSure.cancellationDate().setValue(SystemDateManager.getLogicalDate());
            insuranceTenantSure.status().setValue(TenantSureStatus.PendingCancellation);
            insuranceTenantSure.cancellation().setValue(CancellationType.CancelledByTenant);
            insuranceTenantSure.certificate().expiryDate().setValue(expiryDate);

            Persistence.service().merge(insuranceTenantSure);
            Persistence.service().commit();
        } catch (CfcApiException | WebServiceException e) {
            log.error("Failed to reinstate TenantSure  for tenant " + tenantId.getPrimaryKey() + "'", e);
            ServerSideFactory.create(OperationsNotificationFacade.class).sendTenantSureCfcOperationProblem(e);
            throw new UserRuntimeException(i18n.tr("Failed to cancel due to TenantSure interface error"), e);
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

            insuranceTenantSure.cancellationDate().setValue(SystemDateManager.getLogicalDate());
            insuranceTenantSure.status().setValue(TenantSureStatus.Cancelled);
            insuranceTenantSure.cancellation().setValue(CancellationType.SkipPayment);
            insuranceTenantSure.certificate().expiryDate().setValue(expiryDate);

            Persistence.service().merge(insuranceTenantSure);
        } catch (CfcApiException | WebServiceException e) {
            log.error("failed to cancel TenantSure policy due to skipped payment", e);
            ServerSideFactory.create(OperationsNotificationFacade.class).sendTenantSureCfcOperationProblem(e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void startCancellationDueToSkippedPayment(Tenant tenantId) {
        TenantSureInsurancePolicy insuranceTenantSure = retrieveActiveInsuranceTenantSure(tenantId);
        validateIsCancellable(insuranceTenantSure);

        insuranceTenantSure.status().setValue(TenantSureStatus.PendingCancellation);
        insuranceTenantSure.cancellation().setValue(CancellationType.SkipPayment);
        Persistence.service().merge(insuranceTenantSure);

        log.debug("TenantSureInsurance {} is now PendingCancellation due to SkipPayment", insuranceTenantSure.quoteId());

        sendPaymentNotProcessedEmail(tenantId, getGracePeriodEndDate(insuranceTenantSure), TenantSurePayments.getNextPaymentDate(insuranceTenantSure));

        Persistence.service().commit();
    }

    @Override
    public void reverseCancellationDueToSkippedPayment(Tenant tenantId) {
        TenantSureInsurancePolicy insuranceTenantSure = retrieveActiveInsuranceTenantSure(tenantId);
        //TODO Assert 15 days.
        if (insuranceTenantSure.status().getValue() != TenantSureStatus.PendingCancellation) {
            throw new Error("It's impossible to activate a tenant sure insurance which is not " + TenantSureStatus.PendingCancellation);
        }
        log.debug("TenantSureInsurance {} resumed", insuranceTenantSure.quoteId());

        sendPaymentsResumedEmail(tenantId);

        insuranceTenantSure.status().setValue(TenantSureStatus.Active);
        insuranceTenantSure.cancellation().setValue(null);
        Persistence.service().merge(insuranceTenantSure);

    }

    /** Warning: this method is not implemented properly */
    @Deprecated
    @Override
    public void cancelByTenantSure(Tenant tenantId, String cancellationReason, LogicalDate expiryDate) {
        if (true) {
            throw new RuntimeException("this is not implemented!!!");
        }
        TenantSureInsurancePolicy insuranceTenantSure = retrieveActiveInsuranceTenantSure(tenantId);
        validateIsCancellable(insuranceTenantSure);

        insuranceTenantSure.cancellationDate().setValue(SystemDateManager.getLogicalDate());
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
            throw new UserRuntimeException(i18n.tr("Cannot be reinstated because it's not cancelled"));
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
        } catch (CfcApiException | WebServiceException e) {
            log.error("Failed to reinstate TenantSure for tenant " + tenantId.getPrimaryKey() + "'", e);
            ServerSideFactory.create(OperationsNotificationFacade.class).sendTenantSureCfcOperationProblem(e);
            throw new UserRuntimeException(i18n.tr("Failed to reinstate due to TenantSure interface error"), e);
        }
    }

    private TenantSureInsurancePolicyClient getClient(Tenant tenantId) {
        EntityQueryCriteria<TenantSureInsurancePolicyClient> criteria = EntityQueryCriteria.create(TenantSureInsurancePolicyClient.class);
        criteria.eq(criteria.proto().tenant(), tenantId);
        return Persistence.service().retrieve(criteria);
    }

    private TenantSureInsurancePolicyClient initializeClient(Tenant tenantId, String name, String phone) {
        Object mutex = TENANT_SURE_CLIENT_INIT_MUTEX[(int) tenantId.getPrimaryKey().asLong() % TENANT_SURE_CLIENT_INIT_MUTEX_COUNT];

        synchronized (mutex) {
            TenantSureInsurancePolicyClient tenantSureClient = getClient(tenantId);
            if (tenantSureClient == null) {
                tenantSureClient = EntityFactory.create(TenantSureInsurancePolicyClient.class);
                tenantSureClient.tenant().set(Persistence.service().retrieve(Tenant.class, tenantId.getPrimaryKey()));
                String clientReferenceNumber = null;

                try {
                    clientReferenceNumber = ServerSideFactory.create(CfcApiAdapterFacade.class).createClient(tenantId, name, phone);
                    tenantSureClient.clientReferenceNumber().setValue(clientReferenceNumber);
                    Persistence.service().persist(tenantSureClient);
                    Persistence.service().commit();
                } catch (CfcApiException | WebServiceException e) {
                    log.error("Failed to register tenant '" + tenantId.getPrimaryKey() + "' via CFC API", e);
                    ServerSideFactory.create(OperationsNotificationFacade.class).sendTenantSureCfcOperationProblem(e);
                    throw new UserRuntimeException(i18n.tr("Failed to register client via TenantSure interface"), e);
                }
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
            throw new RuntimeException("It's impossible to cancel a tenant sure insurance which is not " + TenantSureStatus.Active);
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
        } catch (CfcApiException | WebServiceException e) {
            log.error("Failed to send TenantSure certificate to tenant '" + tenantId.getPrimaryKey() + "'", e);
            ServerSideFactory.create(OperationsNotificationFacade.class).sendTenantSureCfcOperationProblem(e);
            throw new UserRuntimeException(i18n.tr("Failed to send email due to TenantSure interface error"), e);
        }

        return emails.get(0);
    }

    @Override
    public String sendQuote(Tenant tenantId, String quoteId) {
        List<String> emails = Arrays.asList(getTenantsEmail(tenantId));
        try {
            ServerSideFactory.create(CfcApiAdapterFacade.class).requestDocument(quoteId, emails);
        } catch (CfcApiException | WebServiceException e) {
            log.error("Failed to send quote to tenant '" + tenantId.getPrimaryKey() + "'", e);
            ServerSideFactory.create(OperationsNotificationFacade.class).sendTenantSureCfcOperationProblem(e);
            throw new UserRuntimeException(i18n.tr("Failed to send email due to TenantSure interface error"), e);
        }
        return emails.get(0);
    }

    private String getTenantsEmail(Tenant tenantId) {
        Tenant tenant = Persistence.service().retrieve(Tenant.class, tenantId.getPrimaryKey());
        String tenantsEmail = null;

        SMTPMailServiceConfig mailConfig = (SMTPMailServiceConfig) ServerSideConfiguration.instance().getMailServiceConfigConfiguration();
        if ((mailConfig != null) && CommonsStringUtils.isStringSet(mailConfig.getForwardAllTo())) {
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

    private void sendPaymentNotProcessedEmail(Tenant tenant, LogicalDate gracePeriodEndDate, LogicalDate cancellationDate) {
        ServerSideFactory.create(CommunicationFacade.class).sendTenantSurePaymentNotProcessedEmail(tenant, gracePeriodEndDate, cancellationDate);
    }

    private void sendPaymentsResumedEmail(Tenant tenant) {
        ServerSideFactory.create(CommunicationFacade.class).sendTenantSurePaymentsResumedEmail(tenant);
    }

    private static TenantSurePaymentItemDTO makePaymentItem(String description, BigDecimal amount, List<TenantSurePaymentItemTaxDTO> taxes) {
        TenantSurePaymentItemDTO paymentItem = EntityFactory.create(TenantSurePaymentItemDTO.class);
        paymentItem.description().setValue(description);
        paymentItem.amount().setValue(amount);
        paymentItem.taxBreakdown().addAll(taxes);
        return paymentItem;
    }

}
