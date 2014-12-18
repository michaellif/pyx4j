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
 */
package com.propertyvista.oapi.v1.searchcriteria;

import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.dto.PropertySearchCriteria;
import com.propertyvista.dto.PropertySearchCriteria.BathroomChoice;
import com.propertyvista.dto.PropertySearchCriteria.BedroomChoice;
import com.propertyvista.dto.PropertySearchCriteria.SearchType;

@XmlAccessorType(XmlAccessType.FIELD)
public class PropertySearchCriteriaIO {

    public String city;

    public String province;

    public BedroomChoice minBeds;

    public BedroomChoice maxBeds;

    public BathroomChoice minBaths;

    public BathroomChoice maxBaths;

    public Integer minPrice;

    public Integer maxPrice;

    public Set<BuildingAmenity.Type> amenities;

    public PropertySearchCriteriaIO() {
    }

    public PropertySearchCriteriaIO( //
            String city, String province, //
            BedroomChoice minBeds, BedroomChoice maxBeds, //
            BathroomChoice minBaths, BathroomChoice maxBaths, //
            Integer minPrice, Integer maxPrice, //
            Set<BuildingAmenity.Type> amenities//
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

    public PropertySearchCriteria getDbCriteria() {
        PropertySearchCriteria criteria = EntityFactory.create(PropertySearchCriteria.class);
        criteria.searchType().setValue(SearchType.city);
        criteria.city().setValue(city);
        criteria.province().setValue(province);
        criteria.minBeds().setValue(minBeds);
        criteria.maxBeds().setValue(maxBeds);
        criteria.minBaths().setValue(minBaths);
        criteria.maxBaths().setValue(maxBaths);
        criteria.minPrice().setValue(minPrice);
        criteria.maxPrice().setValue(maxPrice);
        if (amenities != null) {
            criteria.amenities().addAll(amenities);
        }
        return criteria;
    }
}
