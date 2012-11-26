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

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.billingext.dto.ChargeDTO;
import com.propertyvista.oapi.model.ChargeIO;
import com.propertyvista.oapi.xml.BigDecimalIO;
import com.propertyvista.oapi.xml.LogicalDateIO;
import com.propertyvista.oapi.xml.StringIO;

public class ChargeMarshaller implements Marshaller<ChargeDTO, ChargeIO> {

    private static class SingletonHolder {
        public static final ChargeMarshaller INSTANCE = new ChargeMarshaller();
    }

    private ChargeMarshaller() {
    }

    public static ChargeMarshaller getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public ChargeIO unmarshal(ChargeDTO charge) {
        ChargeIO chargeRS = new ChargeIO();
        chargeRS.amount = new BigDecimalIO(charge.amount().getValue());
        chargeRS.description = new StringIO(charge.description().getValue());
        chargeRS.fromDate = new LogicalDateIO(charge.fromDate().getValue());
        chargeRS.toDate = new LogicalDateIO(charge.toDate().getValue());
        return chargeRS;
    }

    @Override
    public ChargeDTO marshal(ChargeIO c) {
        ChargeDTO charge = EntityFactory.create(ChargeDTO.class);
        charge.transactionId().setValue(c.transactionId);
        charge.leaseId().setValue(c.leaseId);
        charge.amount().setValue(c.amount.value);
        charge.description().setValue(c.description.value);
        charge.fromDate().setValue(c.fromDate.value); // Transaction.ChargeDetail.ServiceFromDate
        charge.toDate().setValue(c.toDate.value); // Transaction.ChargeDetail.ServiceToDate
        return charge;
    }

}
