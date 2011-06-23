/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 17, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.yardi.mapper;

import com.propertyvista.yardi.bean2.Property;
import com.propertyvista.yardi.bean2.RTCustomer;
import com.propertyvista.yardi.bean2.ResidentTransactions;

public class GetResidentTransactionsMapper {

    public void map(ResidentTransactions transactions) {
        for (Property property : transactions.getProperties()) {
            map(property);
        }
    }

    public void map(Property property) {
        for (RTCustomer customer : property.getCustomers()) {
            map(customer);
        }
    }

    public void map(RTCustomer customer) {

    }
}
