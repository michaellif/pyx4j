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
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translatable;

import com.propertyvista.domain.Medium;
import com.propertyvista.domain.marketing.AdvertisingBlurb;
import com.propertyvista.domain.marketing.yield.AddOn;
import com.propertyvista.domain.marketing.yield.Amenity;
import com.propertyvista.domain.marketing.yield.Concession;

public interface AptUnit extends IEntity {

    @Translatable
    public enum Type {

        oneBathroom,

        oneBathroomAndDen,

        twoBathroom,

        twoBathroomAndDen,

        threeBathroom,

        threeBathroomAndDen,

        fourBathroom,

        fourBathroomAndDen,

        fivebathroom,

        fivebathroomAndDen,

        other;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    // ----------- Infromation --------------------------------------------------------------------------

    IPrimitive<String> name();

    @MemberColumn(name = "unitType")
    IPrimitive<Type> type();

    IPrimitive<String> typeDescription();

    IPrimitive<AptUnitEcomomicStatus> economicStatus();

    IPrimitive<String> economicStatusDescription();

    IPrimitive<Integer> floor();

    @MemberColumn(name = "unitNumber")
    IPrimitive<String> number();

    Building building();

    // ----------- Details --------------------------------------------------------------------------

    IPrimitive<Double> area();

    IPrimitive<AreaMeasurementUnit> areaUnits();

    @Format("#0.#")
    @Caption(name = "Beds")
    IPrimitive<Double> bedrooms();

    @Format("#0.#")
    @Caption(name = "Baths")
    IPrimitive<Double> bathrooms();

    IList<Utility> utilities();

    IList<AptUnitDetail> details();

    /**
     * Keeps current and future occupancy data
     * Used for DB Denormalization
     */
    IList<AptUnitOccupancy> currentOccupancies();

    IPrimitive<Double> numberOfOccupants();

    // ------------------ Financials ------------------------------------------------------------------
    @Format("#0.00")
    IPrimitive<Double> unitRent();

    @Format("#0.00")
    IPrimitive<Double> netRent();

    @Format("#0.00")
    IPrimitive<Double> marketRent();

    // ----------------------- Marketing --------------------------------------------------------------

    IPrimitive<String> marketingName();

    IPrimitive<String> marketingDescription();

    IList<AdvertisingBlurb> addBlurbs();

    /**
     * Object used as part of a marketing campaign to demonstrate the design, structure,
     * and appearance of unit.
     */
    @Detached
    Floorplan floorplan();

    IList<Amenity> amenities();

    IList<Concession> concessions();

    IList<AddOn> addOns();

    @Detached
    IList<Medium> media();
}
