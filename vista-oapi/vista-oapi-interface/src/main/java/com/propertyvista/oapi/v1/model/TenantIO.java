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
 */
package com.propertyvista.oapi.v1.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.propertyvista.oapi.v1.model.types.SexTypeIO;
import com.propertyvista.oapi.xml.AbstractElementIO;
import com.propertyvista.oapi.xml.LogicalDateIO;
import com.propertyvista.oapi.xml.StringIO;

@XmlType(name = "Tenant")
@XmlRootElement(name = "tenant")
public class TenantIO extends AbstractElementIO {

    @XmlAttribute
    public String leaseId;

    @XmlAttribute
    public String tenantId;

    public String firstName;

    public String middleName;

    public String lastName;

    public String newFirstName;

    public String newMiddleName;

    public String newLastName;

    public LogicalDateIO birthDate;

    public SexTypeIO sex;

    public StringIO email;

    public StringIO phone;

    public TenantIO() {
    }

    public TenantIO(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public boolean equals(Object obj) {
        return (firstName == ((TenantIO) obj).firstName && lastName == ((TenantIO) obj).lastName && middleName == ((TenantIO) obj).middleName);
    }

    @Override
    public int hashCode() {
        return (firstName + " " + middleName + " " + lastName).hashCode();
    }

}
