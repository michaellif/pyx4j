/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 28, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.yardi.bean2;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class Property {
    private List<RTCustomer> customers = new ArrayList<RTCustomer>();

    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (RTCustomer customer : customers) {
            sb.append("\n").append(customer);
        }

        return sb.toString();
    }

    @XmlElement(name = "RT_Customer")
    public List<RTCustomer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<RTCustomer> customers) {
        this.customers = customers;
    }
}
