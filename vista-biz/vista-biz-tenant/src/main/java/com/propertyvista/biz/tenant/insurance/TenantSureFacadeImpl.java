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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.tenant.insurance.InsuranceTenantSure;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSureClient;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSureTransaction;
import com.propertyvista.domain.tenant.lease.Tenant;

public abstract class TenantSureFacadeImpl implements TenantSureFacade {

    private final static Logger log = LoggerFactory.getLogger(TenantSureFacadeImpl.class);

    private static final I18n i18n = I18n.get(TenantSureFacadeImpl.class);

    /**
     * Function implements this: http://jira.birchwoodsoftwaregroup.com/wiki/pages/viewpage.action?pageId=10027234
     */
    @Override
    public void buyInsurance(String quoteId, Tenant tenantId) {
        InsuranceTenantSure ts = EntityFactory.create(InsuranceTenantSure.class);
        ts.quoteId().setValue(quoteId);
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
        new CfcApiCleint().createClient(null);
        return null;
    }

    private void bind(InsuranceTenantSure ts) {
        // new CfcApiCleint().bind(null);
    }
}
