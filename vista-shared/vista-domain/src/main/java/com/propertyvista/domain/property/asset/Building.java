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
package com.propertyvista.domain.property.asset;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translatable;
import com.pyx4j.i18n.shared.Translation;

import com.propertyvista.domain.Address;
import com.propertyvista.domain.Email;
import com.propertyvista.domain.OrganizationContacts;
import com.propertyvista.domain.Phone;
import com.propertyvista.domain.Picture;
import com.propertyvista.domain.marketing.yield.Amenity;
import com.propertyvista.portal.domain.pt.PropertyProfile;

@ToStringFormat("{0} {1}")
//TODO rename to Property
public interface Building extends IEntity {

    // TODO: To be auto-generated by system unless user enters his own value. (Can be modified (by accounting)).
    //          It seems that two buttons are required: Validate (on Uniqueness) and Generate (new one).
    @Caption(name = "Building Code")
    IPrimitive<String> propertyCode();

    /**
     * Legal name of the property (max 120 char)
     */
    IPrimitive<String> name();

    /**
     * Property name used for marketing purposes (max 120 char)
     */
    IPrimitive<String> marketingName();

    /**
     * Property description used for marketing purposes
     */
    IPrimitive<String> marketingDescription();

    // TODO - add list of property owners here...

    @ToString(index = 1)
    Address address();

// --------------------------------------------------------------------------------------

    @Translatable
    public enum Type {

        agricultural,

        commercial,

        mixed_residential,

        residential,

        industrial,

        military,

        parking_storage,

        other;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @ToString(index = 0)
    @MemberColumn(name = "buildingType")
    IPrimitive<Type> type();

    @Translatable
    public enum Shape {

        regular,

        @Translation("L-shape")
        lShape,

        @Translation("T-shape")
        tShape,

        @Translation("U-shape")
        uShape,

        irregular;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    @ToString(index = 0)
    IPrimitive<Type> shape();

    IPrimitive<String> totalStories();

    IPrimitive<String> residentialStories();

    IPrimitive<String> structureDescription();

    // there is a drop-down box with create new complex  
    Complex complex();

// --------------------------------------------------------------------------------------

    IList<Elevator> elevators();

    IList<Boiler> boilers();

    Roof roofData();

    IList<Parking> parkings();

    IList<Amenity> amenities();

// TODO General unit attributes (via unit setup)?

// --------------------------------------------------------------------------------------

    @Length(100)
    IPrimitive<String> website();

    @Owned
    Email email(); // email business is not clear at the moment, we need a bit more detail on this

    @Owned
    IList<Phone> phoneList();

// TODO discuss with Artur which contacts fill here!..    
    IList<OrganizationContacts> contactsList();

    PropertyProfile propertyProfile();

    @Detached
    @Deprecated
    //TODO VladS to clean it up
    ISet<Picture> pictures();
}
