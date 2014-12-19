/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 18, 2011
 * @author Dad
 */
package com.propertyvista.portal.rpc.portal.resident.dto;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.tenant.CustomerPicture;
import com.propertyvista.domain.tenant.EmergencyContact;

@Transient
public interface ResidentProfileDTO extends IEntity {

    Person person();

    CustomerPicture picture();

    @Length(3)
    @Caption(name = "Emergency Contacts")
    IList<EmergencyContact> emergencyContacts();

    // Restrictions policy:
    IPrimitive<Boolean> emergencyContactsIsMandatory();

    IPrimitive<Integer> emergencyContactsNumberRequired();
}
