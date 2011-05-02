/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 26, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.domain;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;

import com.propertyvista.portal.domain.pt.PropertyProfile;

@ToStringFormat("{0} {1}")
//TODO rename to Property
public interface Building extends IEntity {

    public enum BuildingType {

        agricultural,

        commercial,

        residential,

        industrial,

        military,

        parking_storage,

        other
    }

    /**
     * Legal name of the property (max 120 char)
     */
    IPrimitive<String> name();

    /**
     * Property name used for marketing purposes (max 120 char)
     */
    IPrimitive<String> marketingName();

    @Detached
    @Deprecated
    //TODO VladS to clean it up
    ISet<Picture> pictures();

    Complex complex();

    @Caption(name = "Building Code")
    IPrimitive<String> propertyCode();

    @Caption(name = "Type")
    @ToString(index = 0)
    IPrimitive<BuildingType> buildingType();

    IPrimitive<String> structureDescription();

    @Length(100)
    IPrimitive<String> website();

    @Caption(name = "Address")
    @ToString(index = 1)
    Address address();

    @Owned
    IList<Phone> phoneList();

    @Owned
    Email email(); // email business is not clear at the moment, we need a bit more detail on this

    IList<OrganizationContacts> contactsList();

    PropertyProfile propertyProfile();
}
