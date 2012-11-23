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
import java.util.Random;

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
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSure;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSure.Status;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSureClient;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSureTransaction;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureCoverageDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureCoverageDTO.PreviousClaims;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureMessageDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureQuoteDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureTenantInsuranceStatusDetailedDTO;

public class TenantSureFacadeImpl implements TenantSureFacade {

    private final boolean TODO_MOCKUP = true;

    private final static Logger log = LoggerFactory.getLogger(TenantSureFacadeImpl.class);

    private static final I18n i18n = I18n.get(TenantSureFacadeImpl.class);

    @Override
    public InsurancePaymentMethod getPaymentMethod(Tenant tenantId) {
        return TenantSurePayments.getPaymentMethod(tenantId);
    }

    @Override
    public TenantSureQuoteDTO getQuote(TenantSureCoverageDTO coverage, Tenant tenantId) {
        if (TODO_MOCKUP) {
            TenantSureQuoteDTO quote = EntityFactory.create(TenantSureQuoteDTO.class);
            if (coverage.numberOfPreviousClaims().getValue() == PreviousClaims.MoreThanTwo) {
                quote.specialQuote().setValue(i18n.tr("Please call TenantSure 1-800-1234-567 to get your quote."));
            } else {
                quote.grossPremium().setValue(new BigDecimal(10 + new Random().nextInt() % 50));
                quote.underwriterFee().setValue(new BigDecimal(10 + new Random().nextInt() % 50));
                quote.totalMonthlyPayable().setValue(new BigDecimal(10 + new Random().nextInt() % 50));
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new Error(e);
            }
            return quote;
        } else {

            InsuranceTenantSureClient client = initializeCleint(tenantId);
            TenantSureQuoteDTO quote = new CfcApiClient().getQuote(client, coverage);

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
        if (quote.quoteId().isNull()) {
            throw new Error("it's impossible to buy insurance with no quote id!!!");
        }
        InsuranceTenantSure ts = EntityFactory.create(InsuranceTenantSure.class);
        ts.quoteId().setValue(quote.quoteId().getValue());
        ts.client().set(initializeCleint(tenantId));
        ts.status().setValue(InsuranceTenantSure.Status.Draft);
        Persistence.service().persist(ts);

        // Start payment
        InsuranceTenantSureTransaction transaction = EntityFactory.create(InsuranceTenantSureTransaction.class);
        transaction.insurance().set(ts);
        transaction.paymentMethod().set(TenantSurePayments.getPaymentMethod(tenantId));
        transaction.status().setValue(InsuranceTenantSureTransaction.TransactionStatus.Draft);
        // TODO
        transaction.amount().setValue(null);
        Persistence.service().persist(transaction);

        Persistence.service().commit();

        // Like two phase commit transaction
        {
            try {
                transaction = TenantSurePayments.preAuthorization(transaction);
            } catch (Throwable e) {
                log.error("Error", e);
                transaction.status().setValue(InsuranceTenantSureTransaction.TransactionStatus.Rejected);
                Persistence.service().persist(transaction);
                ts.status().setValue(InsuranceTenantSure.Status.Failed);
                Persistence.service().persist(ts);
                Persistence.service().commit();
                if (e instanceof UserRuntimeException) {
                    throw (UserRuntimeException) e;
                } else {
                    throw new UserRuntimeException(i18n.tr("Credit Card Authorization failed"));
                }
            }
            transaction.status().setValue(InsuranceTenantSureTransaction.TransactionStatus.Authorized);
            Persistence.service().persist(transaction);

            try {
                bind(ts);
            } catch (Throwable e) {
                log.error("Error", e);
                ts.status().setValue(InsuranceTenantSure.Status.Failed);
                Persistence.service().persist(ts);
                Persistence.service().commit();
                if (e instanceof UserRuntimeException) {
                    throw (UserRuntimeException) e;
                } else {
                    throw new UserRuntimeException(i18n.tr("Insurance bind failed"));
                }
            }

            ts.status().setValue(InsuranceTenantSure.Status.Active);
            Persistence.service().persist(ts);
            Persistence.service().commit();
        }

        try {
            TenantSurePayments.compleateTransaction(transaction);
        } catch (Throwable e) {
            log.error("Error", e);
            transaction.status().setValue(InsuranceTenantSureTransaction.TransactionStatus.Rejected);
            Persistence.service().persist(transaction);
            ts.status().setValue(InsuranceTenantSure.Status.Pending);
            Persistence.service().persist(ts);
            Persistence.service().commit();
            if (e instanceof UserRuntimeException) {
                throw (UserRuntimeException) e;
            } else {
                throw new UserRuntimeException(i18n.tr("Credit Card payment failed, payment transaction would be compleated later"));
            }
        }
        transaction.status().setValue(InsuranceTenantSureTransaction.TransactionStatus.Cleared);
        Persistence.service().commit();

    }

    InsuranceTenantSureClient initializeCleint(Tenant tenantId) {
        InsuranceTenantSureClient client = new CfcApiClient().createClient(null);
        return client;
    }

    @Override
    public TenantSureTenantInsuranceStatusDetailedDTO getStatus(Tenant tenantId) {
        InsuranceTenantSure insurance = retrieveInsuranceTenantSure(tenantId);

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
            messageHolder.message().setValue(
                    i18n.tr("Your payment couln't be processed, please update your credit card info or else your insurance will expire"));
        }

        return tenantSureInsurance;

    }

    @Override
    public void cancel(Tenant tenantId) {
        // TODO Auto-generated method stub
        throw new Error("Not Implemented!!!");
    }

    private void bind(InsuranceTenantSure insuranceTenantSure) {
        if (TODO_MOCKUP) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            new CfcApiClient().bindQuote(insuranceTenantSure.quoteId().getValue());
        }
    }

    private InsuranceTenantSure retrieveInsuranceTenantSure(Tenant tenantId) {
        if (TODO_MOCKUP) {
            InsuranceTenantSure insurance = EntityFactory.create(InsuranceTenantSure.class);
            insurance.status().setValue(Status.Active);
            insurance.startDate().setValue(new LogicalDate());
            insurance.expiryDate().setValue(null);
            insurance.details().liabilityCoverage().getValue();
            insurance.monthlyPayable().setValue(new BigDecimal("999.99"));
            insurance.details().liabilityCoverage().setValue(new BigDecimal("1000000"));
            insurance.details().contentsCoverage().setValue(new BigDecimal("10000"));
            insurance.details().deductible().setValue(new BigDecimal("500"));
            insurance.details().grossPremium().setValue(new BigDecimal("40"));
            insurance.details().underwriterFee().setValue(new BigDecimal("10"));
            return insurance;
        } else {
            EntityQueryCriteria<InsuranceTenantSure> criteria = EntityQueryCriteria.create(InsuranceTenantSure.class);
            criteria.add(PropertyCriterion.ne(criteria.proto().status(), InsuranceTenantSure.Status.Draft));
            criteria.add(PropertyCriterion.ne(criteria.proto().status(), InsuranceTenantSure.Status.Failed));
            criteria.add(PropertyCriterion.eq(criteria.proto().client().tenant(), tenantId));
            InsuranceTenantSure insurance = Persistence.service().retrieve(criteria);
            return insurance;
        }
    }
}
