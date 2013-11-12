/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 19, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.tenant.prospect;

import java.util.Date;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.tenant.Customer;

public interface DigitalSignature extends IEntity {

    @ToString
    Customer person();

    @Format("MM/dd/yyyy hh:mm a")
    @MemberColumn(name = "signDate")
    IPrimitive<Date> timestamp();

    @Caption(name = "IP Address")
    IPrimitive<String> ipAddress();

    @NotNull
    @Caption(name = "Type Your Full Name")
    IPrimitive<String> fullName();
}
