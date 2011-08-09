/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 19, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.property.asset.building;

import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.company.OrganizationContacts;
import com.propertyvista.domain.contact.Email;
import com.propertyvista.domain.contact.Phone;

public interface BuildingContactInfo extends IEntity {

    @Length(100)
    IPrimitive<String> website();

    @EmbeddedEntity
    Email email(); // email business is not clear at the moment, we need a bit more detail on this

    @Owned
    IList<Phone> phones();

// TODO discuss with Artur which contacts fill here!..    
    IList<OrganizationContacts> contacts();
}
