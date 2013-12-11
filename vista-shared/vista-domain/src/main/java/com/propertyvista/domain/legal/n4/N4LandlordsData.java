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
 * @version $Id$
 */
package com.propertyvista.domain.legal.n4;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.contact.AddressSimple;

/** This is data common to all N4 forms filled in a single batch */
@Transient
public interface N4LandlordsData extends IEntity {

    IPrimitive<String> landlordsLegalName();

    Employee signingEmployee();

    AddressSimple landlordsAddress();

    /** must have the following format: (XXX) XXX-XXXX */
    IPrimitive<String> landlordsPhoneNumber();

    /** optional, but must have the following format: (XXX) XXX-XXXX */
    IPrimitive<String> faxNumber();

    IPrimitive<String> emailAddress();

    /** Determines if its Landlord's or Agent's signature */
    IPrimitive<Boolean> isLandlord();

    IPrimitive<LogicalDate> signatureDate();

    /** optional */
    IPrimitive<byte[]> signature();
}
