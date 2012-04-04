/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-04-04
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.tenant;

import java.util.Date;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.EmergencyContact;
import com.propertyvista.domain.contact.AddressStructured;

public interface _Customer extends IEntity {

    @Caption(name = "E-Mail")
    @Editor(type = EditorType.email)
    IPrimitive<String> email();

    @Caption(name = "Name")
    IPrimitive<String> _name();

    @Owned
    AddressStructured address();

    @Editor(type = EditorType.phone)
    IPrimitive<String> homePhone();

    @Editor(type = EditorType.phone)
    IPrimitive<String> mobilePhone();

    @Editor(type = EditorType.phone)
    IPrimitive<String> workPhone();

    @Owned
    @Detached
    @Length(3)
    IList<EmergencyContact> emergencyContacts();

    @Timestamp
    IPrimitive<Date> updated();

}
