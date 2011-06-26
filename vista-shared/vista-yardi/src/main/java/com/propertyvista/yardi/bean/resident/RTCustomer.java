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
package com.propertyvista.yardi.bean.resident;

import javax.xml.bind.annotation.XmlElement;

public class RTCustomer {
    private String customerId;

    private Customers customers;

    private RTUnit rtunit;

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(customerId);
        sb.append("\n").append(rtunit);
        sb.append("\n").append(customers);

        return sb.toString();
    }

    @XmlElement(name = "CustomerID")
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    @XmlElement(name = "RT_Unit")
    public RTUnit getRtunit() {
        return rtunit;
    }

    public void setRtunit(RTUnit rtunit) {
        this.rtunit = rtunit;
    }

    @XmlElement(name = "Customers")
    public Customers getCustomers() {
        return customers;
    }

    public void setCustomers(Customers customers) {
        this.customers = customers;
    }
}
