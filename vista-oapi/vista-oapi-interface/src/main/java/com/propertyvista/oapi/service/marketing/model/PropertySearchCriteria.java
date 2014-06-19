/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 13, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.oapi.service.marketing.model;

import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.propertyvista.domain.property.asset.building.BuildingAmenity;

@XmlAccessorType(XmlAccessType.FIELD)
public class PropertySearchCriteria {
    public enum SearchType {
        city, proximity;
    }

    public enum DisplayMode {
        map, list
    }

    public static enum PriceRange {

        Any(null), lt600(0), gt600(600), gt800(800), gt1000(1000), gt1200(1200);

        public final Integer minPrice;

        private PriceRange(Integer minPrice) {
            this.minPrice = minPrice;
        }
    }

    public static enum BedroomRange {

        Any(null, null), One(1, 1), OneOrMore(1), Two(2, 2), TwoOrMore(2), Three(3, 3), ThreeOrMore(3), Four(4, 4), FourOrMore(4);

        public final Integer minBeds;

        public final Integer maxBeds;

        private BedroomRange(Integer minBeds, Integer maxBeds) {
            this.minBeds = minBeds;
            this.maxBeds = maxBeds;
        }

        private BedroomRange(Integer minBeds) {
            this(minBeds, null);
        }
    }

    public static enum BedroomChoice {

        Any(0), One(1), Two(2), Three(3), Four(4);

        public final Integer beds;

        private BedroomChoice(Integer beds) {
            this.beds = beds;
        }
    }

    public static enum BathroomChoice {

        Any(0), One(1), Two(2), Three(3), Four(4), Five(5);

        public final Integer bath;

        private BathroomChoice(Integer bath) {
            this.bath = bath;
        }
    }

    public static enum AmenityChoice {
        Elevator(BuildingAmenity.Type.elevator), Fitness(BuildingAmenity.Type.fitness), Garage(BuildingAmenity.Type.garage), //
        Laundry(BuildingAmenity.Type.laundry), Parking(BuildingAmenity.Type.parking), Pool(BuildingAmenity.Type.pool), //
        Concierge(BuildingAmenity.Type.concierge), ChildCare(BuildingAmenity.Type.childCare);

        public final BuildingAmenity.Type type;

        private AmenityChoice(BuildingAmenity.Type type) {
            this.type = type;
        }
    };

    public PropertySearchCriteria( //
            String city, String province, //
            BedroomChoice minBeds, BedroomChoice maxBeds, //
            BathroomChoice minBaths, BathroomChoice maxBaths, //
            Integer minPrice, Integer maxPrice, //
            Set<AmenityChoice> amenities//
    ) {
        this.city = city;
        this.province = province;
        this.minBeds = minBeds;
        this.maxBeds = maxBeds;
        this.minBaths = minBaths;
        this.maxBaths = maxBaths;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.amenities = amenities;
    }

    public String city;

    public String province;

    public BedroomChoice minBeds;

    public BedroomChoice maxBeds;

    public BathroomChoice minBaths;

    public BathroomChoice maxBaths;

    public Integer minPrice;

    public Integer maxPrice;

    public Set<AmenityChoice> amenities;

}
