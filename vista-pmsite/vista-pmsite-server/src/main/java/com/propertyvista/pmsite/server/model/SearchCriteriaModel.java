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

    private SearchType searchType = SearchType.City;

    private final DisplayMode displayMode = DisplayMode.Map;

    private String province;

    private String city;

    private String location;

    private Integer distance;

    //If null - open range
    private BedroomChoice bedsMin;

    private BedroomChoice bedsMax;

    private BathroomChoice bathsMin;

    private BathroomChoice bathsMax;

    private PriceChoice priceRange;

    private Integer priceMin;

    private Integer priceMax;

    private List<AmenitySet> amenities;

    public enum SearchType {
        City, Proximity
    }

    public enum DisplayMode {
        Map, List
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

    public enum PriceChoice {
        Any(null), lt600(0), gt600(600), gt800(800), gt1000(1000), gt1200(1200);
        private final Integer minPrice;

        private PriceChoice(Integer price) {
            this.minPrice = price;
        }

        public Integer[] getPriceRange() {
            Integer[] range = new Integer[2];
            range[0] = minPrice;
            PriceChoice[] valArr = values();
            int idx = ordinal();
            if (idx < valArr.length - 1) {
                range[1] = valArr[idx + 1].minPrice;
            } else {
                range[1] = null;
            }
            return range;
        }

        @Override
        public String toString() {
            String result = "";
            Integer[] range = getPriceRange();
            if (range[0] == null || range[0] == 0) {
                if (range[1] == null || range[1] == 0) {
                    result = "Any";
                } else {
                    result = "Less than $" + range[1];
                }
            } else {
                if (range[1] == null) {
                    result = "Over $" + range[0];
                } else {
                    result = "$" + range[0] + " - $" + range[1];
                }
            }
            return result;
        }
    }

    public enum AmenitySet {
        Elevator, Fitness, Parking, Pool
    }

    public SearchType getSearchType() {
        return searchType;
    }

    public DisplayMode getDisplayMode() {
        return displayMode;
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

    public BedroomChoice getBedrooms() {
        return bedsMin;
    }

    public BathroomChoice getBathsMin() {
        return bathsMin;
    }

    public BathroomChoice getBathsMax() {
        return bathsMax;
    }

    public BathroomChoice getBathrooms() {
        return bathsMin;
    }

    public PriceChoice getPriceRange() {
        return priceRange;
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

    public void setSearchType(SearchType searchType) {
        this.searchType = searchType;
    }

    public void setProvinceCity(String prov, String city) {
        if (prov == null) {
            return;
        }
        Map<String, List<String>> provCityMap = getProvinceCityMap();
        List<String> cities = provCityMap.get(prov);
        if (cities != null) {
            setSearchType(SearchType.City);
            this.province = prov;
            if (city != null && cities.contains(city)) {
                this.city = city;
            }
        }
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public void setBedsMin(BedroomChoice bedsMin) {
        this.bedsMin = bedsMin;
    }

    public void setBedsMax(BedroomChoice bedsMax) {
        this.bedsMax = bedsMax;
    }

    public void setBedrooms(BedroomChoice beds) {
        this.bedsMin = this.bedsMax = beds;
    }

    public void setBathsMin(BathroomChoice bathsMin) {
        this.bathsMin = bathsMin;
    }

    public void setBathsMax(BathroomChoice bathsMax) {
        this.bathsMax = bathsMax;
    }

    public void setBathrooms(BathroomChoice baths) {
        this.bathsMin = this.bathsMax = baths;
    }

    public void setPriceRange(PriceChoice priceRange) {
        this.priceRange = priceRange;
    }

    public void setPriceMin(Integer priceMin) {
        this.priceMin = priceMin;
    }

    public void setPriceMax(Integer priceMax) {
        this.priceMax = priceMax;
    }

    public void setAmenities(List<AmenitySet> amenities) {
        this.amenities = amenities;
    }

    public void addAmenity(AmenitySet amenity) {
        if (!amenities.contains(amenity)) {
            amenities.add(amenity);
        }
    }

    public void removeAmenity(AmenitySet amenity) {
        if (amenities.contains(amenity)) {
            amenities.remove(amenity);
        }
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
