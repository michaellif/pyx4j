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

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.Tenant;

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
        EntityQueryCriteria<MerchantAccount> criteria = EntityQueryCriteria.create(MerchantAccount.class);
        criteria.add(PropertyCriterion.eq(criteria.proto()._buildings().$()._Units().$()._Leases().$().billingAccount(), billingAccountId));
        return Persistence.service().retrieve(criteria) != null;
    }

    public static boolean isElectronicPaymentsAllowed(BillingAccount billingAccountId) {
        EntityQueryCriteria<MerchantAccount> criteria = EntityQueryCriteria.create(MerchantAccount.class);
        criteria.add(PropertyCriterion.eq(criteria.proto()._buildings().$()._Units().$()._Leases().$().billingAccount(), billingAccountId));
        return isElectronicPaymentsAllowed(Persistence.service().retrieve(criteria));
    }

    static MerchantAccount retrieveMerchantAccount(PaymentRecord paymentRecord) {
        EntityQueryCriteria<MerchantAccount> criteria = EntityQueryCriteria.create(MerchantAccount.class);
        criteria.add(PropertyCriterion.eq(criteria.proto()._buildings().$()._Units().$()._Leases().$().billingAccount(), paymentRecord.billingAccount()));
        return Persistence.service().retrieve(criteria);
    }

    static MerchantAccount retrieveValidMerchantAccount(PaymentRecord paymentRecord) {
        EntityQueryCriteria<MerchantAccount> criteria = EntityQueryCriteria.create(MerchantAccount.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().invalid(), Boolean.FALSE));
        criteria.add(PropertyCriterion.eq(criteria.proto()._buildings().$()._Units().$()._Leases().$().billingAccount(), paymentRecord.billingAccount()));
        for (MerchantAccount merchantAccount : Persistence.service().query(criteria)) {
            if (!merchantAccount.merchantTerminalId().isNull()) {
                return merchantAccount;
            }
        }
        throw new UserRuntimeException(i18n.tr("No active merchantAccount found to process the payment"));
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

    public static PaymentMethod retrievePreAuthorizedPaymentMethod(Tenant tenant) {
        return tenant.preauthorizedPayment();
    }
}
