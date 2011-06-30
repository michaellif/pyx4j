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

import com.propertyvista.yardi.bean.mits.Customers;

public class RTCustomer {
    private String customerId;

    private Customers customers;

    private RTUnit rtunit;

    private Integer paymentAccepted;

    private RTServiceTransactions serviceTransactions;

    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (customers != null) {
            sb.append(customers).append("\n");
        }
        sb.append("Unit: ").append(rtunit);
        if (paymentAccepted != null) {
            sb.append("\n").append(paymentAccepted).append(" payments accepted\n");
        }
        if (serviceTransactions != null) {
            sb.append(serviceTransactions);
        }

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

    @XmlElement(name = "PaymentAccepted")
    public Integer getPaymentAccepted() {
        return paymentAccepted;
    }

    public void setPaymentAccepted(Integer paymentAccepted) {
        this.paymentAccepted = paymentAccepted;
    }

    @XmlElement(name = "RTServiceTransactions")
    public RTServiceTransactions getServiceTransactions() {
        return serviceTransactions;
    }

    public void setServiceTransactions(RTServiceTransactions serviceTransactions) {
        this.serviceTransactions = serviceTransactions;
    }
}
