/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 13, 2014
 * @author stanp
 */
package com.propertyvista.oapi.v1.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.domain.person.Name.Prefix;
import com.propertyvista.domain.tenant.lead.Lead.DayPart;
import com.propertyvista.domain.tenant.lead.Lead.LeaseTerm;

@XmlType(name = "AppointmentRequest")
@XmlRootElement(name = "appointmentRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class AppointmentRequestIO {

    public Prefix namePrefix;

    public String firstName;

    public String lastName;

    public String homePhone;

    public String mobilePhone;

    public String workPhone;

    public String email;

    public LogicalDate moveInDate;

    public LeaseTerm leaseTerm;

    public String propertyId;

    public String floorplanId;

    public String comments;

    public LogicalDate preferredDate1;

    public DayPart preferredTime1;

    public LogicalDate preferredDate2;

    public DayPart preferredTime2;
}
