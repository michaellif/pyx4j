/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 12, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.v1.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.propertyvista.oapi.xml.AbstractElementIO;
import com.propertyvista.oapi.xml.BigDecimalIO;
import com.propertyvista.oapi.xml.LogicalDateIO;
import com.propertyvista.oapi.xml.StringIO;

@XmlType(name = "PaymentRecord")
@XmlRootElement(name = "paymentRecord")
public class PaymentRecordIO extends AbstractElementIO {

    @XmlAttribute
    public String transactionId;

    public StringIO externalTransactionId;

    public StringIO leaseId;

    public LogicalDateIO transactionDate;

    public BigDecimalIO amount;

    public StringIO paymentType;

    @Override
    public boolean equals(Object obj) {
        return transactionId == ((PaymentRecordIO) obj).transactionId;
    }

    @Override
    public int hashCode() {
        return transactionId.hashCode();
    }
}
