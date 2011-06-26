/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 26, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.yardi.bean.resident;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import com.propertyvista.yardi.bean.mits.Customer;

public class Customers {
    private List<Customer> customers = new ArrayList<Customer>();

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(customers.size()).append(" customers");
        for (Customer customer : customers) {
            sb.append("\n").append(customer);
        }

        return sb.toString();
    }

    @XmlElement(name = "Customer")
    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }
}
