/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 12, 2012
 * @author michaellif
 */
package com.propertyvista.oapi.v1.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import com.propertyvista.oapi.xml.AbstractElementIO;
import com.propertyvista.oapi.xml.BigDecimalIO;
import com.propertyvista.oapi.xml.StringIO;

@XmlType(name = "Transaction")
@XmlRootElement(name = "transaction")
@XmlSeeAlso({ ChargeIO.class, PaymentIO.class })
public abstract class TransactionIO extends AbstractElementIO {

    @XmlAttribute
    public String transactionId;

    @XmlAttribute
    public String leaseId;

    public StringIO description;

    public BigDecimalIO amount;

    public TransactionIO() {
    }

    public TransactionIO(StringIO description, BigDecimalIO amount) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description + " " + amount;
    }

    @Override
    public boolean equals(Object obj) {
        return (transactionId == ((TransactionIO) obj).transactionId && leaseId == ((TransactionIO) obj).leaseId);
    }

    @Override
    public int hashCode() {
        return (transactionId + " " + leaseId).hashCode();
    }
}
