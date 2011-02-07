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
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

@ToStringFormat("{0} {1}")
public interface Building extends Property {

    public enum BuildingType {

        agricultural,

        commercial,

        residential,

        industrial,

        military,

        parking_storage,

        other
    }

    Complex complex();

    @Caption(name = "Type")
    @ToString(index = 0)
    IPrimitive<BuildingType> buildingType();

    /**
     * (max 100 char)
     */
    IPrimitive<String> webSite();

    @Caption(name = "Address")
    @ToString(index = 1)
    Address address();

    @Owned
    IList<Phone> phoneList();

    Email email(); // email business is not clear at the moment, we need a bit more detail on this

    IList<OrganizationContacts> contactsList();
}
