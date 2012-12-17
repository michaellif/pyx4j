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
package com.propertyvista.oapi.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.propertyvista.oapi.model.types.LeaseStatusIO;
import com.propertyvista.oapi.model.types.PaymentFrequencyIO;
import com.propertyvista.oapi.xml.AbstractElementIO;
import com.propertyvista.oapi.xml.LogicalDateIO;

@XmlRootElement(name = "lease")
public class LeaseIO extends AbstractElementIO {

    @XmlAttribute
    public String propertyCode;

    @XmlAttribute
    public String unitNumber;

    @XmlAttribute
    public String leaseId;

    public LeaseStatusIO status;

    public PaymentFrequencyIO paymentFrequency;

    public LogicalDateIO leaseFrom;

    public LogicalDateIO leaseTo;

    public List<TenantIO> tenants;

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
