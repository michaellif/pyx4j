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
package com.propertyvista.domain.legal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.contact.AddressSimple;

@Transient
public interface N4LandlordsData extends IEntity {

    AddressSimple landlordsAddress();

    IPrimitive<String> landlordsPhoneNumber();

    IPrimitive<String> faxNumber();

    IPrimitive<String> emailAddress();

    /** Determines if its Landlord's or Agent's signature */
    IPrimitive<Boolean> isLandlord();

    IPrimitive<LogicalDate> signatureDate();

    IPrimitive<byte[]> signature();
}
