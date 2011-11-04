/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 20, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.rpc.ptapp.dto;

import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IList;

import com.propertyvista.domain.EmergencyContact;
import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.tenant.TenantScreening;

@Transient
public interface TenantInfoDTO extends TenantScreening {

    @ToString
    Person person();

    @Length(3)
    IList<EmergencyContact> emergencyContacts();
}
