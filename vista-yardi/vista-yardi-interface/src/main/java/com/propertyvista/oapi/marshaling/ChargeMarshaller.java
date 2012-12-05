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
    public ChargeIO marshal(ChargeDTO charge) {
        ChargeIO chargeIO = new ChargeIO();
        chargeIO.amount = new BigDecimalIO(charge.amount().getValue());
        chargeIO.description = new StringIO(charge.description().getValue());
        chargeIO.fromDate = new LogicalDateIO(charge.fromDate().getValue());
        chargeIO.toDate = new LogicalDateIO(charge.toDate().getValue());
        return chargeIO;
    }

    @Override
    public ChargeDTO unmarshal(ChargeIO chargeIO) {
        ChargeDTO charge = EntityFactory.create(ChargeDTO.class);
        charge.transactionId().setValue(chargeIO.transactionId);
        charge.leaseId().setValue(chargeIO.leaseId);
        charge.amount().setValue(chargeIO.amount.value);
        charge.description().setValue(chargeIO.description.value);
        charge.fromDate().setValue(chargeIO.fromDate.value); // Transaction.ChargeDetail.ServiceFromDate
        charge.toDate().setValue(chargeIO.toDate.value); // Transaction.ChargeDetail.ServiceToDate
        return charge;
    }

}
