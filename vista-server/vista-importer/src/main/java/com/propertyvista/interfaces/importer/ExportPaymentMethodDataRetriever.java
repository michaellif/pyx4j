/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 17, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.EcheckInfo;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.interfaces.importer.converter.AddressSimpleConverter;
import com.propertyvista.interfaces.importer.model.CreditCardIO;
import com.propertyvista.interfaces.importer.model.EcheckIO;
import com.propertyvista.interfaces.importer.model.PaymentMethodIO;

public class ExportPaymentMethodDataRetriever {

    public PaymentMethodIO getModel(LeasePaymentMethod paymentMethod) {
        PaymentMethodIO model = EntityFactory.create(PaymentMethodIO.class);

        switch (paymentMethod.type().getValue()) {
        case Echeck:
            EcheckInfo echeckInfo = paymentMethod.details().cast();

            EcheckIO ec = EntityFactory.create(EcheckIO.class);
            ec.nameOnAccount().setValue(echeckInfo.nameOn().getValue());
            ec.bankId().setValue(echeckInfo.bankId().getValue());
            ec.transitNumber().setValue(echeckInfo.branchTransitNumber().getValue());
            ec.accountNumber().setValue(echeckInfo.accountNo().number().getValue());
            model.details().set(ec);

            break;
        case CreditCard:
            CreditCardInfo ccInfo = paymentMethod.details().cast();

            CreditCardIO cc = EntityFactory.create(CreditCardIO.class);
            cc.propertyVistaId().setValue(paymentMethod.id().getValue().toString());
            cc.nameOnAccount().setValue(ccInfo.nameOn().getValue());
            cc.cardType().setValue(ccInfo.cardType().getValue());
            cc.cardNumber().setValue(ccInfo.card().obfuscatedNumber().getValue());
            cc.expiryDate().setValue(ccInfo.expiryDate().getValue());

            model.details().set(cc);

            break;
        default:
            break;
        }

        model.billingAddress().set(new AddressSimpleConverter().createTO(paymentMethod.billingAddress()));

        return model;
    }
}
