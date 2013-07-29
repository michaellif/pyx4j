/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 4, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.dashboard.gadgets;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.dashboard.gadgets.payments.PaymentFeesDTO;
import com.propertyvista.domain.dashboard.gadgets.payments.PaymentFeesDTO.PaymentFeeMeasure;
import com.propertyvista.domain.pmc.PmcPaymentTypeInfo;

public class PaymentFeesHelper {

    public static PaymentFeesDTO extractFees(PmcPaymentTypeInfo typeInfo, PaymentFeeMeasure measure) {
        PaymentFeesDTO fees = EntityFactory.create(PaymentFeesDTO.class);
        fees.paymentFeeMeasure().setValue(measure);

        switch (measure) {
        case absolute:
            fees.cash().setValue(null);
            fees.check().setValue(null);
            fees.eCheck().setValue(typeInfo.eChequeFee().getValue());
            fees.eft().setValue(typeInfo.directBankingFee().getValue());
            fees.interacCaledon().setValue(typeInfo.interacCaledonFee().getValue());
            fees.interacVisa().setValue(typeInfo.interacCaledonFee().getValue());
            break;

        case relative:
            fees.cc().setValue(typeInfo.ccVisaFee().getValue());
            break;
        }

        return fees;
    }
}
