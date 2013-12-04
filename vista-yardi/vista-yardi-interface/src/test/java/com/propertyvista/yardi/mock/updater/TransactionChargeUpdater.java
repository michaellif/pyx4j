/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 18, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.yardi.mock.updater;

import com.yardi.entity.resident.ChargeDetail;

public class TransactionChargeUpdater extends Updater<ChargeDetail, TransactionChargeUpdater> {

    private final String customerID;

    private final String propertyID;

    public enum Name implements com.propertyvista.yardi.mock.updater.Name {

        Description,

        TransactionDate,

        TransactionID,

        ChargeCode,

        GLAccountNumber,

        CustomerID,

        AmountPaid,

        BalanceDue,

        Amount,

        Comment;

    }

    public TransactionChargeUpdater(String propertyID, String customerID) {
        assert propertyID != null : "propertyID should not be null";
        this.propertyID = propertyID;
        assert customerID != null : "customerID should not be null";
        this.customerID = customerID;
    }

    public String getPropertyID() {
        return propertyID;
    }

    public String getCustomerID() {
        return customerID;
    }
}
