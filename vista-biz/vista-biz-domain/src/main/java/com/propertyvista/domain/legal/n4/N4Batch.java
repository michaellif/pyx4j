/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 30, 2014
 * @author stanp
 */
package com.propertyvista.domain.legal.n4;

import java.util.Date;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.Timestamp.Update;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.contact.InternationalAddress;

public interface N4Batch extends IEntity {

    IPrimitive<String> name();

    @ReadOnly
    @Format("yyyy-MM-dd HH:mm:ss")
    @Timestamp(Update.Created)
    IPrimitive<Date> created();

    IPrimitive<LogicalDate> noticeDate();

    IPrimitive<N4DeliveryMethod> deliveryMethod();

    IPrimitive<String> companyLegalName();

    InternationalAddress companyAddress();

    /** must have the following format: (XXX) XXX-XXXX */
    IPrimitive<String> companyPhoneNumber();

    /** optional, but must have the following format: (XXX) XXX-XXXX */
    IPrimitive<String> companyFaxNumber();

    IPrimitive<String> companyEmailAddress();

    /** Determines if its Landlord's or Agent's signature */
    IPrimitive<Boolean> isLandlord();

    IPrimitive<LogicalDate> signatureDate();

    Employee signingEmployee();

    @Owned
    @Detached
    @OrderBy(PrimaryKey.class)
    IList<N4BatchItem> items();
}
