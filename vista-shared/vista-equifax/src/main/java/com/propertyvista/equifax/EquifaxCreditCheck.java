/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-11
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.equifax;

import ca.equifax.uat.from.EfxTransmit;
import ca.equifax.uat.to.CNConsAndCommRequestType;

import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.PersonCreditCheck;
import com.propertyvista.domain.tenant.PersonCreditCheck.CreditCheckResult;

public class EquifaxCreditCheck {

    public static PersonCreditCheck runCreditCheck(Customer customer, PersonCreditCheck pcc, int strategyNumber) {
        CNConsAndCommRequestType requestMessage = createRequest(customer, pcc, strategyNumber);
        EfxTransmit efxResponse = execute(requestMessage);

        // TODO
        pcc.creditCheckResult().setValue(CreditCheckResult.Decline);

        return pcc;
    }

    private static EfxTransmit execute(CNConsAndCommRequestType requestMessage) {
        // TODO Auto-generated method stub
        return null;
    }

    private static CNConsAndCommRequestType createRequest(Customer customer, PersonCreditCheck pcc, int strategyNumber) {
        return null;
    }
}
