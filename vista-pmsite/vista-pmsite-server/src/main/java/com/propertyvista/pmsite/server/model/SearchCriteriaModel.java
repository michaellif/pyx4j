/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 27, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.pmsite.server.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.propertyvista.domain.ref.City;
import com.propertyvista.pmsite.server.PMSiteContentManager;

//Beds 1 - Enum (Any, 1+, 2+ ... 5+)
//Beds 2 - two Integer (1-5)

public class SearchCriteriaModel implements Serializable {
    static final long serialVersionUID = 1L;

    /*
     * SearchType (City, Proximity)
     * City: City, Province
     * Proximity: Location, Distance
     * Bedrooms
     * Bathrooms
     * Price
     * Amenities
     */

    private SearchType searchType;

    private String province;

    private String city;

    private String location;

    private Integer distance;

    //If null - open range
    private BedroomChoice bedsMin;

    private BedroomChoice bedsMax;

    private BathroomChoice bathsMin;

    private BathroomChoice bathsMax;

    private Integer priceMin;

    private Integer priceMax;

    private List<AmenitySet> amenities;

    public enum SearchType {
        City, Proximity
    }

    public enum BedroomChoice {
        Any(null), One(1), Two(2), Three(3), Four(4);
        private final Integer rooms;

        private BedroomChoice(Integer rooms) {
            this.rooms = rooms;
        }

        public int getRooms() {
            return rooms;
        }
    }

    public enum BathroomChoice {
        Any(null), One(1), Two(2), Three(3), Four(4);
        private final Integer rooms;

        private BathroomChoice(Integer rooms) {
            this.rooms = rooms;
        }

        public int getRooms() {
            return rooms;
        }
    }

    public enum AmenitySet {
        Elevator, Fitness, Parking, Pool
    }

    public SearchType getSearchType() {
        return searchType;
    }

    public String getProvince() {
        return province;
    }

    public String getCity() {
        return city;
    }

    public String getLocation() {
        return location;
    }

    public Integer getDistance() {
        return distance;
    }

    public BedroomChoice getBedsMin() {
        return bedsMin;
    }

    public BedroomChoice getBedsMax() {
        return bedsMax;
    }

    public BathroomChoice getBathsMin() {
        return bathsMin;
    }

    public BathroomChoice getBathsMax() {
        return bathsMax;
    }

    public Integer getPriceMin() {
        return priceMin;
    }

    public Integer getPriceMax() {
        return priceMax;
    }

    public List<AmenitySet> getAmenities() {
        return amenities;
    }

    public Map<String, List<String>> getProvinceCityMap() {
        Map<String, List<String>> provCityMap = new HashMap<String, List<String>>();
        List<City> cities = PMSiteContentManager.getCities();
        for (City city : cities) {
            String cityName = city.name().getValue();
            if (cityName == null) {
                continue;
            }
            String provName = city.province().name().getValue();
            if (provName == null) {
                continue;
            }
            List<String> cityList = provCityMap.get(provName);
            if (cityList == null) {
                cityList = new ArrayList<String>();
                provCityMap.put(provName, cityList);
            }
            cityList.add(cityName);
        }
        return provCityMap;
    }
}
