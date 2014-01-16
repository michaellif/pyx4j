/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-10-21
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto.legal.n4;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.legal.n4.N4DeliveryMethod;
import com.propertyvista.domain.tenant.lease.Lease;

@Transient
public interface N4BatchRequestDTO extends IEntity {

    @NotNull
    Employee agent();

    IList<Lease> targetDelinquentLeases();

    @NotNull
    IPrimitive<LogicalDate> noticeDate();

    IPrimitive<N4DeliveryMethod> deliveryMethod();

    // this is filled from N4Policy can can be overridden by user    
    IPrimitive<String> companyName();

    // this is filled from N4Policy can can be overridden by user
    @NotNull
    AddressSimple mailingAddress();

    // this is filled from N4Policy can can be overridden by user
    @Editor(type = EditorType.phone)
    IPrimitive<String> phoneNumber();

    // this is filled from N4Policy can can be overridden by user
    @Editor(type = EditorType.phone)
    IPrimitive<String> faxNumber();

    // this is filled from N4Policy can can be overridden by user
    @Editor(type = EditorType.email)
    IPrimitive<String> emailAddress();

}
