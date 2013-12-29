/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 22, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.site.rpc.dto;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.IPrimitiveSet;
import com.pyx4j.geo.GeoPoint;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.I18nComment;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.property.asset.building.BuildingAmenity;

@Transient
public interface PropertySearchCriteria extends IEntity {

    static final com.pyx4j.i18n.shared.I18n i18n = com.pyx4j.i18n.shared.I18n.get(PropertySearchCriteria.class);

    public enum SearchType {
        city, proximity;
    }

    public enum DisplayMode {
        map, list
    }

    @I18nComment("Price Selection")
    public static enum PriceRange {

        Any(null), lt600(0), gt600(600), gt800(800), gt1000(1000), gt1200(1200);

        private final Integer minPrice;

        private PriceRange(Integer minPrice) {
            this.minPrice = minPrice;
        }

        public Integer getMinPrice() {
            return minPrice;
        }

        public Integer getMaxPrice() {
            if (minPrice == null) {
                return null;
            }
            int idx = ordinal();
            if (idx < values().length - 1) {
                return values()[idx + 1].minPrice - 1;
            } else {
                return null;
            }
        }

        @Override
        public String toString() {
            Integer maxPrice = getMaxPrice();
            final String format = "{0,choice,null#Any|0#Less than ${1}|0<{1,choice,null#Over ${0}|0<${0} - ${1}}}";
            return i18n.tr(format, minPrice, maxPrice);
        }
    }

    @I18nComment("Number Of Bedrooms")
    public static enum BedroomRange {

        Any(null, null), One(1, 1), OneOrMore(1), Two(2, 2), TwoOrMore(2), Three(3, 3), ThreeOrMore(3), Four(4, 4), FourOrMore(4);

        private final Integer minBeds;

        private final Integer maxBeds;

        private BedroomRange(Integer minBeds, Integer maxBeds) {
            this.minBeds = minBeds;
            this.maxBeds = maxBeds;
        }

        private BedroomRange(Integer minBeds) {
            this(minBeds, null);
        }

        public Integer getMinBeds() {
            return minBeds;
        }

        public Integer getMaxBeds() {
            return maxBeds;
        }

        @Override
        public String toString() {
            final String format = "{0,choice,null#Any|0#Less than {1}|0<{0}{1,choice,null# and more|0<}}";
            return i18n.tr(format, minBeds, maxBeds);
        }
    }

    @I18n(strategy = I18n.I18nStrategy.IgnoreThis)
    public static enum BedroomChoice {

        Any(0), One(1), Two(2), Three(3), Four(4);

        Integer beds = 0;

        private BedroomChoice(Integer beds) {
            this.beds = beds;
        }

        public static BedroomChoice getChoice(Integer beds) {
            BedroomChoice choice = null;
            for (BedroomChoice c : BedroomChoice.values()) {
                if (c.beds == beds) {
                    choice = c;
                    break;
                }
            }
            return choice;
        }

        public Integer getBeds() {
            return beds;
        }

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @I18n(strategy = I18n.I18nStrategy.IgnoreThis)
    public static enum BathroomChoice {

        Any(0), One(1), Two(2), Three(3), Four(4), Five(5);

        Integer bath = 0;

        private BathroomChoice(Integer bath) {
            this.bath = bath;
        }

        public static BathroomChoice getChoice(Integer bath) {
            BathroomChoice choice = null;
            for (BathroomChoice c : BathroomChoice.values()) {
                if (c.bath == bath) {
                    choice = c;
                    break;
                }
            }
            return choice;
        }

        public Integer getBaths() {
            return bath;
        }

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    public static final BuildingAmenity.Type[] AmenityChoice = { BuildingAmenity.Type.elevator, BuildingAmenity.Type.fitness, BuildingAmenity.Type.garage,
            BuildingAmenity.Type.laundry, BuildingAmenity.Type.parking, BuildingAmenity.Type.pool, BuildingAmenity.Type.concierge,
            BuildingAmenity.Type.childCare };

    @NotNull
    @Caption(name = "Search By")
    IPrimitive<SearchType> searchType();

    IPrimitive<String> city();

    IPrimitive<String> province();

    IPrimitive<String> location();

    IPrimitive<GeoPoint> geolocation();

    IPrimitive<Integer> distance();

    IPrimitive<BedroomChoice> minBeds();

    IPrimitive<BedroomChoice> maxBeds();

    IPrimitive<BathroomChoice> minBaths();

    IPrimitive<BathroomChoice> maxBaths();

    IPrimitive<Integer> minPrice();

    IPrimitive<Integer> maxPrice();

    IPrimitiveSet<BuildingAmenity.Type> amenities();

    /**
     * don't use in criteria
     */
    IPrimitive<BedroomRange> bedsRange();

    /**
     * don't use in criteria
     */
    IPrimitive<PriceRange> priceRange();

}
