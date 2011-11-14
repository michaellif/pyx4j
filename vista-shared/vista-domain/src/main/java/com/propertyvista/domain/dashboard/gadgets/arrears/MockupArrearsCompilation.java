/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 3, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.dashboard.gadgets.arrears;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.contact.AddressStructured.StreetType;
import com.propertyvista.domain.dashboard.gadgets.CommonGadgetColumns;
import com.propertyvista.domain.dashboard.gadgets.ComparableComparator;
import com.propertyvista.domain.dashboard.gadgets.CustomComparator;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;

/**
 * Represents mockup of pre-comuptated arrears of a tenant on a specific date.
 * 
 * @author artyom
 * 
 */
public interface MockupArrearsCompilation extends IEntity {
    /**
     * The time when the status has been taken.
     */
    IPrimitive<LogicalDate> statusTimestamp();

    @Owner
    @Detached
    @ReadOnly
    MockupTenant belongsTo();

    // the unit, building, and tenant references here are for search optimization purposes
    @Detached
    @ReadOnly
    AptUnit unit();

    @Detached
    @ReadOnly
    Building building();

    @EmbeddedEntity
    CommonGadgetColumns common();

    // ARREARS
    @EmbeddedEntity
    Arrears rentArrears();

    @EmbeddedEntity
    Arrears parkingArrears();

    @EmbeddedEntity
    Arrears otherArrears();

    @EmbeddedEntity
    Arrears totalArrears();

    // FIXME all the following fields should be just references, but we keep them here for the performance improvements, but this is not RIGHT!
    // ID
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<String> propertyCode();

    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<String> buildingName();

    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<String> complexName();

    // ADDRESS
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<String> streetNumber();

    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<String> streetName();

    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<StreetType> streetType();

    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<String> city();

    // TODO special comparator
    Province province();

    // TODO special comparator
    Country country();

    @Caption(name = "Unit")
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<String> unitNumber();

    // TENANT INFORMATION
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<String> firstName();

    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<String> lastName();

    // TODO add contact information?
}
