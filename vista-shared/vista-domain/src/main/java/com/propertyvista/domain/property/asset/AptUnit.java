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
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translatable;

import com.propertyvista.domain.Medium;
import com.propertyvista.domain.marketing.AdvertisingBlurb;
import com.propertyvista.domain.marketing.yield.AddOn;
import com.propertyvista.domain.marketing.yield.Concession;
import com.propertyvista.domain.property.asset.building.Building;

@ToStringFormat("{0} {1} {2}")
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

        fiveBathroom,

        fivebathroomAndDen,

        other;

        @Override
        public String toString() {
            return I18nEnum.tr(this);
        }
    }

    // ----------- Infromation --------------------------------------------------------------------------

    IPrimitive<String> name();

    @ToString(index = 0)
    @MemberColumn(name = "unitType")
    IPrimitive<Type> type();

    IPrimitive<String> typeDescription();

    IPrimitive<AptUnitEcomomicStatus> economicStatus();

    IPrimitive<String> economicStatusDescription();

    IPrimitive<Integer> floor();

    @ToString(index = 1)
    @MemberColumn(name = "unitNumber")
    IPrimitive<String> number();

    @ToString(index = 2)
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
     */
    IList<AptUnitOccupancy> currentOccupancies();

    @Transient
    IPrimitive<Double> numberOfOccupants();

    @Format("MM/dd/yyyy")
    @Caption(name = "Available")
    @Indexed
    /**
     * Denormalizied field used for search, derived from @see AptUnitOccupancy
     * TODO should be calculated during Entity save 
     * @deprecated remove deprecated once it is calulated and filled properly.
     */
    IPrimitive<java.sql.Date> avalableForRent();

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

    IList<AptUnitAmenity> amenities();

    IList<Concession> concessions();

    IList<AddOn> addOns();

    @Detached
    IList<Medium> media();
}
