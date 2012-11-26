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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.propertyvista.oapi.xml.LogicalDateIO;
import com.propertyvista.oapi.xml.StringIO;

@XmlRootElement(name = "tenant")
public class TenantIO {

    @XmlAttribute
    public String leaseId;

    @XmlAttribute
    public String firstName;

    @XmlAttribute
    public String middleName;

    @XmlAttribute
    public String lastName;

    public StringIO newFirstName;

    public StringIO newLastName;

    public StringIO newMiddleName;

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

}
