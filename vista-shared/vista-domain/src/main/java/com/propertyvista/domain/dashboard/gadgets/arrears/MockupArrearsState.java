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
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18n;

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
public interface MockupArrearsState extends IEntity {
    public enum LegalStatus {
        Clean {
            @Override
            public String toString() {
                return tr("Clean");
            }
        },

        FirstMissedPaymentLetterServed {
            @Override
            public String toString() {
                return tr("1st Missed Payment Letter Served");
            }
        },

        SecondMissedPaymentLetterServed {
            @Override
            public String toString() {
                return tr("2nd Missed Payment Letter Served");
            }
        },

        ThirdMissedPaymentLetterServed {
            @Override
            public String toString() {
                return tr("3rd Missed Payment Letter Served");
            }
        },

        EvictionLetterServed {
            @Override
            public String toString() {
                return tr("Eviction Letter Served");
            }
        },

        CourtHearingDate {
            @Override
            public String toString() {
                return tr("Court Hearing Date");
            }
        };

        public String tr(String translatableString) {
            return I18n.get(LegalStatus.class).tr(translatableString);
        }
    }

    /**
     * The time when the status has been taken.
     */
    IPrimitive<LogicalDate> statusTimestamp();

    // the unit, building, and tenant references here are for search optimization purposes
    @Detached
    @ReadOnly
    AptUnit unit();

    @Detached
    @ReadOnly
    Building building();

    @EmbeddedEntity
    CommonGadgetColumns common();

    // ARREARS STATUS: 
    @EmbeddedEntity
    Arrears rentArrears();

    @EmbeddedEntity
    Arrears parkingArrears();

    @EmbeddedEntity
    Arrears otherArrears();

    @EmbeddedEntity
    Arrears totalArrears();

    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<LegalStatus> legalStatus();

    @Caption(name = "LMR/Unit Rent, in $")
    @Format("#0.00")
    IPrimitive<Double> lmrUnitRentDifference();

    // FIXME all the following fields should be just references, but we keep them here for the performance improvements, but this is not RIGHT!
    // IDENTIFICATION INFO
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
    @EmbeddedEntity
    Province province();

    @EmbeddedEntity
    // TODO special comparator
    Country country();

    @Caption(name = "Unit")
    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<String> unitNumber();

    // TENANT INFORMATION
    @Owner
    @Detached
    @ReadOnly
    MockupTenant belongsTo();

    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<String> firstName();

    @CustomComparator(clazz = ComparableComparator.class)
    IPrimitive<String> lastName();

    // TODO add contact information?
}
