/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 2, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.util.Collection;
import java.util.Collections;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.BillingAccount.PaymentAccepted;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.policy.policies.PaymentTypeSelectionPolicy;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.security.VistaApplication;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;

class PaymentUtils {

    private static final I18n i18n = I18n.get(PaymentUtils.class);

    static boolean isElectronicPaymentsAllowed(MerchantAccount merchantAccount) {
        if ((merchantAccount == null) || merchantAccount.invalid().getValue(Boolean.TRUE)) {
            return false;
        } else {
            return !merchantAccount.merchantTerminalId().isNull();
        }
    }

    public static boolean isPaymentsAllowed(BillingAccount billingAccountId) {
        if (PaymentRecord.merchantAccountIsRequedForPayments) {
            EntityQueryCriteria<MerchantAccount> criteria = EntityQueryCriteria.create(MerchantAccount.class);
            criteria.add(PropertyCriterion.eq(criteria.proto()._buildings().$().units().$()._Leases().$().billingAccount(), billingAccountId));
            return Persistence.service().retrieve(criteria) != null;
        } else {
            return true;
        }
    }

    public static boolean isElectronicPaymentsAllowed(BillingAccount billingAccountId) {
        EntityQueryCriteria<MerchantAccount> criteria = EntityQueryCriteria.create(MerchantAccount.class);
        criteria.add(PropertyCriterion.eq(criteria.proto()._buildings().$().units().$()._Leases().$().billingAccount(), billingAccountId));
        return isElectronicPaymentsAllowed(Persistence.service().retrieve(criteria));
    }

    public static boolean isElectronicPaymentsAllowed(Lease leaseId) {
        EntityQueryCriteria<MerchantAccount> criteria = EntityQueryCriteria.create(MerchantAccount.class);
        criteria.add(PropertyCriterion.eq(criteria.proto()._buildings().$().units().$()._Leases(), leaseId));
        return isElectronicPaymentsAllowed(Persistence.service().retrieve(criteria));
    }

    public static boolean isElectronicPaymentsAllowed(LeaseTerm leaseTermId) {
        EntityQueryCriteria<MerchantAccount> criteria = EntityQueryCriteria.create(MerchantAccount.class);
        criteria.add(PropertyCriterion.eq(criteria.proto()._buildings().$().units().$()._Leases().$().leaseTerms(), leaseTermId));
        return isElectronicPaymentsAllowed(Persistence.service().retrieve(criteria));
    }

    static MerchantAccount retrieveMerchantAccount(PaymentRecord paymentRecord) {
        EntityQueryCriteria<MerchantAccount> criteria = EntityQueryCriteria.create(MerchantAccount.class);
        criteria.add(PropertyCriterion.eq(criteria.proto()._buildings().$().units().$()._Leases().$().billingAccount(), paymentRecord.billingAccount()));
        return Persistence.service().retrieve(criteria);
    }

    static MerchantAccount retrieveValidMerchantAccount(PaymentRecord paymentRecord) {
        EntityQueryCriteria<MerchantAccount> criteria = EntityQueryCriteria.create(MerchantAccount.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().invalid(), Boolean.FALSE));
        criteria.add(PropertyCriterion.eq(criteria.proto()._buildings().$().units().$()._Leases().$().billingAccount(), paymentRecord.billingAccount()));
        for (MerchantAccount merchantAccount : Persistence.service().query(criteria)) {
            if (!merchantAccount.merchantTerminalId().isNull()) {
                return merchantAccount;
            }
        }
        throw new UserRuntimeException(i18n.tr("No active merchantAccount found to process the payment"));
    }

    static Collection<PaymentType> getAllowedPaymentTypes(BillingAccount billingAccountId, VistaApplication vistaApplication) {
        BillingAccount billingAccount = billingAccountId.duplicate();
        Persistence.ensureRetrieve(billingAccount, AttachLevel.Attached);
        PaymentAccepted paymentAccepted = billingAccount.paymentAccepted().getValue();
        if (paymentAccepted == PaymentAccepted.DoNotAccept) {
            return Collections.emptyList();
        }

        boolean electronicPaymentsAllowed = isElectronicPaymentsAllowed(billingAccountId);
        PaymentTypeSelectionPolicy paymentMethodSelectionPolicy;
        {
            EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
            criteria.add(PropertyCriterion.eq(criteria.proto()._Leases().$().billingAccount(), billingAccountId));
            paymentMethodSelectionPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(Persistence.service().retrieve(criteria),
                    PaymentTypeSelectionPolicy.class);
        }
        return PaymentAcceptanceUtils.getAllowedPaymentTypes(vistaApplication, electronicPaymentsAllowed, paymentAccepted == PaymentAccepted.CashEquivalent,
                paymentMethodSelectionPolicy);
    }

    public static MerchantAccount retrieveMerchantAccount(Building buildingStub) {
        EntityQueryCriteria<MerchantAccount> criteria = EntityQueryCriteria.create(MerchantAccount.class);
        criteria.add(PropertyCriterion.eq(criteria.proto()._buildings(), buildingStub));
        for (MerchantAccount merchantAccount : Persistence.service().query(criteria)) {
            if (!merchantAccount.merchantTerminalId().isNull()) {
                return merchantAccount;
            }
        }
        throw new UserRuntimeException(i18n.tr("No active merchantAccount found to process the payment"));
    }

    public static LeasePaymentMethod retrievePreAuthorizedPaymentMethod(LeaseTermTenant tenant) {
        LeasePaymentMethod method = tenant.leaseParticipant().preauthorizedPayment();
        if (method.isNull()) {
            return null;
        } else {
            Persistence.service().retrieve(method);
            return method;
        }
    }
}
