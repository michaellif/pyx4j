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
package com.propertyvista.portal.rpc.portal;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.IPrimitiveSet;

@Transient
public interface PropertySearchCriteria extends IEntity {

    public enum SearchType {
        city, proximity;
    }

    public enum DisplayMode {
        map, list
    }

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
            String result = "";
            Integer maxPrice = getMaxPrice();
            if (minPrice == null) {
                result = "Any";
            } else if (minPrice == 0) {
                result = "Less than $" + maxPrice;
            } else {
                if (maxPrice == null) {
                    result = "Over $" + minPrice;
                } else {
                    result = "$" + minPrice + " - $" + maxPrice;
                }
            }
            return result;
        }
    }

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
            if (minBeds == null) {
                return super.toString();
            }
            return minBeds + (maxBeds == null ? " or more" : "");
        }
    }

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
    }

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
    }

    public static enum AmenityType {
        Elevator, Fitness, Parking, Pool, Garage
    }

    @NotNull
    @Caption(name = "SEARCH BY")
    IPrimitive<SearchType> searchType();

    IPrimitive<String> city();

    IPrimitive<String> province();

    IPrimitive<String> location();

    IPrimitive<Integer> distance();

    IPrimitive<BedroomChoice> minBeds();

    IPrimitive<BedroomChoice> maxBeds();

    IPrimitive<BathroomChoice> minBath();

    IPrimitive<BathroomChoice> maxBath();

    IPrimitive<Integer> minPrice();

    IPrimitive<Integer> maxPrice();

    IPrimitiveSet<AmenityType> amenities();

/*
 * IPrimitive<Boolean> elevator();
 * 
 * IPrimitive<Boolean> fitness();
 * 
 * IPrimitive<Boolean> parking();
 * 
 * IPrimitive<Boolean> pool();
 */
    /**
     * don't use in criteria
     * 
     * @return
     */
    IPrimitive<BedroomRange> bedsRange();

    /**
     * don't use in criteria
     * 
     * @return
     */
    IPrimitive<PriceRange> priceRange();

}
