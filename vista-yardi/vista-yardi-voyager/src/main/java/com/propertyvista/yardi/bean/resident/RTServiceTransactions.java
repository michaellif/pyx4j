/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 29, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.yardi.bean.resident;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class RTServiceTransactions {
    private List<Services> services = new ArrayList<Services>();

    private List<Transactions> transactions = new ArrayList<Transactions>();

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(services.size()).append(" services\n");
        for (Services service : services) {
            sb.append(service).append("\n");
        }

        sb.append(transactions.size()).append(" transactions\n");
        for (Transactions transaction : transactions) {
            sb.append(transaction).append("\n");
        }

        return sb.toString();
    }

    @XmlElement(name = "Services")
    public List<Services> getServices() {
        return services;
    }

    public void setServices(List<Services> services) {
        this.services = services;
    }

    @XmlElement(name = "Transactions")
    public List<Transactions> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transactions> transactions) {
        this.transactions = transactions;
    }
}
