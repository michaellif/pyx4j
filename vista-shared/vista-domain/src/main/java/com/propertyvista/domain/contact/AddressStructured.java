/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-13
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain.contact;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Reference;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.geo.GeoPoint;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.CountryReferenceAdapter;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.domain.ref.ProvinceReferenceAdapter;

@EmbeddedEntity
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface AddressStructured extends IEntity {

    IPrimitive<String> suiteNumber();

    IPrimitive<String> streetNumber();

    IPrimitive<String> streetNumberSuffix();

    @NotNull
    IPrimitive<String> streetName();

    @I18n
    public enum StreetType {

        street,

        avenue,

        boulevard,

        road,

        lane,

        court,

        crescent,

        way,

        highway,

        drive,

        place,

        other;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @NotNull
    IPrimitive<StreetType> streetType();

    @I18n
    public enum StreetDirection {

        east,

        north,

        south,

        west,

        southEast,

        southWest,

        northEast,

        northWest;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    IPrimitive<StreetDirection> streetDirection();

    @NotNull
    IPrimitive<String> city();

    IPrimitive<String> county();

    @NotNull
    @Caption(name = "Province/State")
    @Editor(type = EditorType.combo)
    @Reference(adapter = ProvinceReferenceAdapter.class)
    Province province();

    @NotNull
    @Editor(type = EditorType.suggest)
    @Reference(adapter = CountryReferenceAdapter.class)
    Country country();

    @NotNull
    @Caption(name = "Zip/Postal Code")
    IPrimitive<String> postalCode();

    IPrimitive<GeoPoint> location();
}
