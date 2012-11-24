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
 * @version $Id$
 */
package com.propertyvista.oapi.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import com.propertyvista.oapi.xml.AbstractElementIO;
import com.propertyvista.oapi.xml.BigDecimalIO;
import com.propertyvista.oapi.xml.StringIO;

@XmlRootElement
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
}
