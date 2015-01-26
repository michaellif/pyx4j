/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-09-20
 * @author ArtyomB
 */
package com.propertyvista.domain.legal.n4.pdf;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.domain.legal.n4.N4DeliveryMethod;

/** This is data common to all N4 forms filled in a single batch */
@Transient
public interface N4PdfBatchData extends IEntity {

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

    @RpcTransient
    /** optional */
    IPrimitive<byte[]> signature();
}
