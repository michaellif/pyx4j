/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 26, 2015
 * @author stanp
 */
package com.propertyvista.domain.legal.n4;

import java.util.Date;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.Timestamp.Update;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.contact.InternationalAddress;

@AbstractEntity
public interface N4Data extends IEntity {

    @ReadOnly
    @Format("yyyy-MM-dd HH:mm:ss")
    @Timestamp(Update.Created)
    IPrimitive<Date> issueDate();

    @ReadOnly
    @Format("yyyy-MM-dd")
    IPrimitive<LogicalDate> serviceDate();

    IPrimitive<N4DeliveryMethod> deliveryMethod();

    @ReadOnly
    @Format("yyyy-MM-dd")
    IPrimitive<LogicalDate> deliveryDate();

    IPrimitive<String> companyLegalName();

    InternationalAddress companyAddress();

    /** must have the following format: (XXX) XXX-XXXX */
    @Editor(type = EditorType.phone)
    @Caption(name = "Phone")
    IPrimitive<String> phoneNumber();

    /** optional, but must have the following format: (XXX) XXX-XXXX */
    @Editor(type = EditorType.phone)
    @Caption(name = "Fax")
    IPrimitive<String> faxNumber();

    @Editor(type = EditorType.email)
    @Caption(name = "E-Mail")
    IPrimitive<String> emailAddress();

    @Editor(type = EditorType.phone)
    @Caption(name = "Phone")
    IPrimitive<String> phoneNumberCS();

    /** Determines if its Landlord's or Agent's signature */
    IPrimitive<Boolean> isLandlord();

    IPrimitive<LogicalDate> signatureDate();

    Employee signingAgent();

    Employee servicingAgent();

}
