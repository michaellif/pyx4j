/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 19, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.oapi.marshaling;

import java.math.BigDecimal;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.billing.InvoiceProductCharge;
import com.propertyvista.oapi.model.ChargeIO;

public class ChargeMarshaller implements Marshaller<InvoiceProductCharge, ChargeIO> {

    @Override
    public ChargeIO unmarshal(InvoiceProductCharge v) {
        ChargeIO chargeRS = new ChargeIO();
        chargeRS.amount = v.amount().getValue();
        chargeRS.description = v.description().getValue();
        chargeRS.fromDate = v.fromDate().getValue();
        chargeRS.toDate = v.toDate().getValue();
        return chargeRS;
    }

    @Override
    public InvoiceProductCharge marshal(ChargeIO c) {
        InvoiceProductCharge charge = EntityFactory.create(InvoiceProductCharge.class);
        charge.amount().setValue(c.amount);
        charge.description().setValue(c.description);
        charge.fromDate().setValue(c.fromDate); // Transaction.ChargeDetail.ServiceFromDate
        charge.toDate().setValue(c.toDate); // Transaction.ChargeDetail.ServiceToDate
        charge.taxTotal().setValue(new BigDecimal("0.00")); // Tax ??
        return charge;
    }

}
