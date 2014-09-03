/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 13, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.v1.model;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.propertyvista.oapi.v1.model.types.LeaseStatusIO;
import com.propertyvista.oapi.v1.model.types.LeaseTermIO;
import com.propertyvista.oapi.v1.model.types.LeaseTypeIO;
import com.propertyvista.oapi.v1.model.types.PaymentFrequencyIO;
import com.propertyvista.oapi.xml.AbstractElementIO;
import com.propertyvista.oapi.xml.BigDecimalIO;
import com.propertyvista.oapi.xml.LogicalDateIO;

@XmlRootElement(name = "lease")
public class LeaseIO extends AbstractElementIO {

    @XmlAttribute
    public String propertyCode;

    @XmlAttribute
    public String unitNumber;

    @XmlAttribute
    public String leaseId;

    public BigDecimalIO price;

    public LeaseStatusIO status;

    public LeaseTypeIO leaseType;

    public LeaseTermIO leaseTerm;

    public PaymentFrequencyIO paymentFrequency;

    public LogicalDateIO leaseFrom;

    // output only. Input done through leaseTerm.
    public LogicalDateIO leaseTo;

    public ArrayList<TenantIO> tenants;

    public LeaseIO() {
    }

    public LeaseIO(String leaseId) {
        this.leaseId = leaseId;
    }

    @Override
    public boolean equals(Object obj) {
        return (propertyCode == ((LeaseIO) obj).propertyCode && unitNumber == ((LeaseIO) obj).unitNumber && leaseId == ((LeaseIO) obj).leaseId);
    }

    @Override
    public int hashCode() {
        return (propertyCode + " " + unitNumber + " " + leaseId).hashCode();
    }
}
