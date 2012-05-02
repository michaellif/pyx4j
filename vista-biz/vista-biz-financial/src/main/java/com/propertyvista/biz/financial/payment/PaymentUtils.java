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

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.PaymentRecord;

class PaymentUtils {

    private static final I18n i18n = I18n.get(PaymentUtils.class);

    static MerchantAccount retrieveMerchantAccount(PaymentRecord paymentRecord) {
        BillingAccount billingAccount = paymentRecord.billingAccount().duplicate();

        // TODO use query of BuildingMerchantAccount
        Persistence.service().retrieve(billingAccount.lease());
        Persistence.service().retrieve(billingAccount.lease().unit());
        Persistence.service().retrieve(billingAccount.lease().unit().belongsTo());

        for (MerchantAccount merchantAccount : billingAccount.lease().unit().belongsTo().merchantAccounts()) {
            Persistence.service().retrieve(merchantAccount);
            if (merchantAccount.active().isBooleanTrue() && (!merchantAccount.merchantTerminalId().isNull())) {
                return merchantAccount;
            }
        }
        throw new UserRuntimeException(i18n.tr("No active merchantAccount found to process the payment"));

    }
}
