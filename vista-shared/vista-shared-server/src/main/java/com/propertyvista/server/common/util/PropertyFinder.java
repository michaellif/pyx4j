/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 18, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.server.common.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.mutable.MutableInt;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.MinMaxPair;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion.Restriction;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.geo.GeoBox;
import com.pyx4j.geo.GeoCircle;
import com.pyx4j.geo.GeoPoint;

import com.propertyvista.domain.MediaFile;
import com.propertyvista.domain.PublicVisibilityType;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.FloorplanAmenity;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.util.DomainUtil;
import com.propertyvista.dto.PropertySearchCriteria;
import com.propertyvista.dto.PropertySearchCriteria.BathroomChoice;
import com.propertyvista.dto.PropertySearchCriteria.BedroomChoice;
import com.propertyvista.dto.PropertySearchCriteria.SearchType;

public class PropertyFinder {

    public static boolean isPropertyVisible(Building bld) {
        if (bld.isValueDetached()) {
            Persistence.service().retrieve(bld);
        }
        return PublicVisibilityType.global.equals(bld.marketing().visibility().getValue());
    }

    // returns false if no results are expected based on the search criteria
    private static boolean addSearchCriteria(EntityQueryCriteria<Building> dbCriteria, PropertySearchCriteria searchCriteria) {
        // evaluate search criteria
        if (!updateBuildingQueryCriteria(dbCriteria, searchCriteria)) {
            return false;
        }

        // 2. filter buildings by floorplans
        EntityQueryCriteria<Floorplan> fpCriteria = EntityQueryCriteria.create(Floorplan.class);
        if (!updateFloorplanQueryCriteria(fpCriteria, searchCriteria)) {
            return false;
        }

        // prepare building filter
        final HashSet<Key> bldFilter2 = new HashSet<Key>();
        for (Floorplan fp : Persistence.service().query(fpCriteria)) {
            bldFilter2.add(fp.building().getPrimaryKey());
        }
        // filter buildings with filter
        if (bldFilter2.size() == 0) {
            // quit early
            return false;
        }
        dbCriteria.add(PropertyCriterion.in(dbCriteria.proto().id(), bldFilter2));

        return true;
    }

    /**
     * Populate db query criteria based on the given PropertySearchCriteria on Building level
     * 
     * @param dbCriteria
     *            - db query criteria to update
     * @param searchCriteria
     *            - given search criteria
     * @return false if no buildings found to match the search criteria
     */
    private static boolean updateBuildingQueryCriteria(EntityQueryCriteria<Building> dbCriteria, PropertySearchCriteria searchCriteria) {
        // add visibility check
        dbCriteria.eq(dbCriteria.proto().marketing().visibility(), PublicVisibilityType.global);
        // add sanity check
        dbCriteria.isNotNull(dbCriteria.proto().productCatalog());
        dbCriteria.isNotNull(dbCriteria.proto().info().location());
        // add search criteria
        if (SearchType.city.equals(searchCriteria.searchType().getValue())) {
            String prov = searchCriteria.province().getValue();
            if (!StringUtils.isEmpty(prov)) {
                dbCriteria.add(PropertyCriterion.like(dbCriteria.proto().info().address().province(), prov));
            }
            String city = searchCriteria.city().getValue();
            if (!StringUtils.isEmpty(city)) {
                dbCriteria.add(PropertyCriterion.like(dbCriteria.proto().info().address().city(), city));
            }
        } else {
            // Vicinity search within the given searchRadius of the centerPoint
            Integer searchRadiusKm = searchCriteria.distance().getValue();
            GeoPoint centerPoint = searchCriteria.geolocation().getValue();
            if (searchRadiusKm != null && centerPoint != null) {
                // Define dbCriteria here, It works perfectly in North America, TODO test other location
                GeoCircle geoCircle = new GeoCircle(centerPoint, searchRadiusKm);
                GeoBox geoBox = geoCircle.getMinBox();
                dbCriteria.add(PropertyCriterion.le(dbCriteria.proto().info().location(), geoBox.getNorthEast()));
                dbCriteria.add(PropertyCriterion.ge(dbCriteria.proto().info().location(), geoBox.getSouthWest()));
            }
        }

        // 1. filter buildings by amenities (must match all items in the set)
        EntityQueryCriteria<BuildingAmenity> amCriteria = EntityQueryCriteria.create(BuildingAmenity.class);
        int amSize = searchCriteria.amenities().size();
        if (amSize > 0) {
            amCriteria.add(PropertyCriterion.in(amCriteria.proto().type(), searchCriteria.amenities().toArray((Serializable[]) new BuildingAmenity.Type[0])));
            // count matched buildings
            Map<Key, MutableInt> amCounter = new HashMap<Key, MutableInt>();
            for (BuildingAmenity am : Persistence.service().query(amCriteria)) {
                Key key = am.building().getPrimaryKey();
                MutableInt count = amCounter.get(key);
                if (count == null) {
                    amCounter.put(key, new MutableInt(1));
                } else {
                    count.increment();
                }
            }
            // prepare building filter 1
            final HashSet<Key> bldFilter1 = new HashSet<Key>();
            for (Key key : amCounter.keySet()) {
                if (amCounter.get(key).intValue() == amSize) {
                    bldFilter1.add(key);
                }
            }
            // filter buildings with filter 1
            if (bldFilter1.size() == 0) {
                // empty set here means no match was found, so we can quit early
                return false;
            }
            dbCriteria.add(PropertyCriterion.in(dbCriteria.proto().id(), bldFilter1));
        }
        return true;
    }

    /**
     * Populate db query criteria based on the given PropertySearchCriteria on Floorplan level
     * 
     * @param dbCriteria
     *            - db query criteria to update
     * @param searchCriteria
     *            - given search criteria
     * @return false if no floorplans found to match the search criteria
     */
    private static boolean updateFloorplanQueryCriteria(EntityQueryCriteria<Floorplan> dbCriteria, PropertySearchCriteria searchCriteria) {
        // 2.1. filter floorplans by units
        EntityQueryCriteria<AptUnit> auCriteria = EntityQueryCriteria.create(AptUnit.class);
        // price
        Integer minPrice = searchCriteria.minPrice().getValue();
        if (minPrice != null) {
            auCriteria.add(new PropertyCriterion(auCriteria.proto().financial()._marketRent(), Restriction.GREATER_THAN_OR_EQUAL, new BigDecimal(minPrice)));
        }
        Integer maxPrice = searchCriteria.maxPrice().getValue();
        if (maxPrice != null && maxPrice > 0) {
            auCriteria.add(new PropertyCriterion(auCriteria.proto().financial()._marketRent(), Restriction.LESS_THAN_OR_EQUAL, new BigDecimal(maxPrice)));
        }
        // filter units by finalized products only:
        auCriteria.add(PropertyCriterion.isNotNull(auCriteria.proto().productItems().$().product().fromDate()));
        auCriteria.add(PropertyCriterion.isNull(auCriteria.proto().productItems().$().product().toDate()));

        // prepare new floorplan filter 1
        final HashSet<Key> fpSet1 = new HashSet<Key>();
        for (AptUnit unit : Persistence.service().query(auCriteria)) {
            fpSet1.add(unit.floorplan().getPrimaryKey());
        }
        // add floorplan unit criteria
        if (fpSet1.size() == 0) {
            // quit early
            return false;
        }
        dbCriteria.add(PropertyCriterion.in(dbCriteria.proto().id(), fpSet1));

        // 2.2 filter floorplans by other search criteria
        // beds
        BedroomChoice minBeds = searchCriteria.minBeds().getValue();
        if (minBeds != null && minBeds != BedroomChoice.Any) {
            dbCriteria.add(new PropertyCriterion(dbCriteria.proto().bedrooms(), Restriction.GREATER_THAN_OR_EQUAL, minBeds.getBeds()));
        }
        BedroomChoice maxBeds = searchCriteria.maxBeds().getValue();
        if (maxBeds != null && maxBeds != BedroomChoice.Any) {
            dbCriteria.add(new PropertyCriterion(dbCriteria.proto().bedrooms(), Restriction.LESS_THAN_OR_EQUAL, maxBeds.getBeds()));
        }
        // baths
        BathroomChoice minBaths = searchCriteria.minBaths().getValue();
        if (minBaths != null && minBaths != BathroomChoice.Any) {
            dbCriteria.add(new PropertyCriterion(dbCriteria.proto().bathrooms(), Restriction.GREATER_THAN_OR_EQUAL, minBaths.getBaths()));
        }
        BathroomChoice maxBaths = searchCriteria.maxBaths().getValue();
        if (maxBaths != null && maxBaths != BathroomChoice.Any) {
            dbCriteria.add(new PropertyCriterion(dbCriteria.proto().bathrooms(), Restriction.LESS_THAN_OR_EQUAL, maxBaths.getBaths()));
        }
        return Persistence.service().exists(dbCriteria);
    }

    public static List<Building> getPropertyList(PropertySearchCriteria searchCriteria) {
        return getPropertyList(searchCriteria, null);
    }

    public static List<Building> getPropertyList(PropertySearchCriteria searchCriteria, EntityQueryCriteria<Building> dbCriteria) {
        if (dbCriteria == null) {
            dbCriteria = EntityQueryCriteria.create(Building.class);
        }
        // if search criteria returns nothing, quit now!
        if (searchCriteria != null && !addSearchCriteria(dbCriteria, searchCriteria)) {
            return Collections.emptyList();
        }

        // get buildings with floorplans and media
        final List<Building> buildings = Persistence.service().query(dbCriteria);
        for (Iterator<Building> it = buildings.iterator(); it.hasNext();) {
            Building building = it.next();
            EntityQueryCriteria<Floorplan> fpCriteria = EntityQueryCriteria.create(Floorplan.class);
            fpCriteria.eq(fpCriteria.proto().building(), building);
            if (!updateFloorplanQueryCriteria(fpCriteria, searchCriteria)) {
                it.remove();
                continue;
            }
            building.floorplans().setAttachLevel(AttachLevel.Attached);
            building.floorplans().clear();
            building.floorplans().addAll(Persistence.service().query(fpCriteria));
            Persistence.service().retrieveMember(building.media());
        }

        return buildings;
    }

    public static boolean isPublicFileMedia(MediaFile m) {
        return (m.visibility().getValue() == PublicVisibilityType.global);
    }

    public static Building getBuildingDetails(long propId) {
        EntityQueryCriteria<Building> dbCriteria = EntityQueryCriteria.create(Building.class);
        dbCriteria.eq(dbCriteria.proto().id(), new Key(propId));
        return getBuildingDetails(dbCriteria);
    }

    public static Building getBuildingDetails(String propCode) {
        EntityQueryCriteria<Building> dbCriteria = EntityQueryCriteria.create(Building.class);
        dbCriteria.eq(dbCriteria.proto().propertyCode(), propCode);
        return getBuildingDetails(dbCriteria);
    }

    private static Building getBuildingDetails(EntityQueryCriteria<Building> dbCriteria) {
        dbCriteria.isNotNull(dbCriteria.proto().productCatalog());
        List<Building> buildings = Persistence.service().query(dbCriteria);
        if (buildings.size() != 1) {
            return null;
        }
        Building building = buildings.get(0);
        if (!isPropertyVisible(building)) {
            return null;
        }
        // check if we have any valid floorplans
        if (getBuildingFloorplans(building).size() < 1) {
            return null;
        }
        // attach phone info
        Persistence.service().retrieve(building.contacts().propertyContacts());
        Persistence.service().retrieveMember(building.media());
        return building;
    }

    public static Map<Floorplan, List<AptUnit>> getBuildingFloorplans(Building bld) {
        if (!isPropertyVisible(bld)) {
            return Collections.emptyMap();
        }
        final Map<Floorplan, List<AptUnit>> floorplans = new HashMap<Floorplan, List<AptUnit>>();
        EntityQueryCriteria<Floorplan> criteria = EntityQueryCriteria.create(Floorplan.class);
        criteria.eq(criteria.proto().building(), bld);
        for (Floorplan fp : Persistence.service().query(criteria)) {
            List<AptUnit> units = getBuildingAptUnits(bld, fp);
            // do some sanity check so we don't render incomplete floorplans
            if (units.size() > 0) {
                floorplans.put(fp, units);
                Persistence.service().retrieveMember(fp.media());
            }
        }
        return floorplans;
    }

    public static List<AptUnit> getBuildingAptUnits(Building bld, Floorplan fp) {
        if (!isPropertyVisible(bld)) {
            return Collections.emptyList();
        }

        EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().building(), bld));
        criteria.add(PropertyCriterion.eq(criteria.proto().floorplan(), fp));
        return Persistence.service().query(criteria);
    }

    public static MinMaxPair<BigDecimal> getMinMaxMarketRent(Collection<AptUnit> aptUnits) {
        BigDecimal minPrice = null, maxPrice = null;
        for (AptUnit u : aptUnits) {
            BigDecimal price = u.financial()._marketRent().getValue();
            minPrice = DomainUtil.min(minPrice, price);
            maxPrice = DomainUtil.max(maxPrice, price);
        }
        return new MinMaxPair<BigDecimal>(minPrice, maxPrice);
    }

    public static MinMaxPair<Integer> getMinMaxAreaInSqFeet(Collection<AptUnit> aptUnits) {
        Integer minArea = null, maxArea = null;
        for (AptUnit u : aptUnits) {
            minArea = DomainUtil.min(minArea, DomainUtil.getAreaInSqFeet(u.info().area(), u.info().areaUnits()));
            maxArea = DomainUtil.max(maxArea, DomainUtil.getAreaInSqFeet(u.info().area(), u.info().areaUnits()));
        }
        return new MinMaxPair<Integer>(minArea, maxArea);
    }

    public static List<BuildingAmenity> getBuildingAmenities(Building bld) {
        if (!isPropertyVisible(bld)) {
            return Collections.emptyList();
        }

        EntityQueryCriteria<BuildingAmenity> criteria = EntityQueryCriteria.create(BuildingAmenity.class);
        criteria.eq(criteria.proto().building(), bld);
        return Persistence.service().query(criteria);
    }

    public static Floorplan getFloorplanDetails(long planId) {
        EntityQueryCriteria<Floorplan> dbCriteria = EntityQueryCriteria.create(Floorplan.class);
        dbCriteria.eq(dbCriteria.proto().id(), new Key(planId));
        List<Floorplan> plans = Persistence.service().query(dbCriteria);
        if (plans.size() != 1) {
            return null;
        }
        Floorplan fp = plans.get(0);
        if (!isPropertyVisible(fp.building())) {
            return null;
        }

        if (getFloorplanUnits(fp).size() < 1) {
            return null;
        }
        Persistence.service().retrieveMember(fp.media());
        return fp;
    }

    public static List<AptUnit> getFloorplanUnits(Floorplan fp) {
        if (!isPropertyVisible(fp.building())) {
            return Collections.emptyList();
        }

        EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
        criteria.eq(criteria.proto().building(), fp.building());
        criteria.eq(criteria.proto().floorplan(), fp);
        return Persistence.service().query(criteria);
    }

    public static List<FloorplanAmenity> getFloorplanAmenities(Floorplan fp) {
        if (!isPropertyVisible(fp.building())) {
            return Collections.emptyList();
        }
        EntityQueryCriteria<FloorplanAmenity> criteria = EntityQueryCriteria.create(FloorplanAmenity.class);
        criteria.eq(criteria.proto().floorplan(), fp);
        return Persistence.service().query(criteria);
    }

}
