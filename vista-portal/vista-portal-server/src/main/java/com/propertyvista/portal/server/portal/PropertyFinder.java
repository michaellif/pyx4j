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
package com.propertyvista.portal.server.portal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.mutable.MutableInt;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion.Restriction;
import com.pyx4j.geo.GeoBox;
import com.pyx4j.geo.GeoCircle;
import com.pyx4j.geo.GeoPoint;

import com.propertyvista.domain.marketing.PublicVisibilityType;
import com.propertyvista.domain.media.Media;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.FloorplanAmenity;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.portal.rpc.portal.PropertySearchCriteria;
import com.propertyvista.portal.rpc.portal.PropertySearchCriteria.BathroomChoice;
import com.propertyvista.portal.rpc.portal.PropertySearchCriteria.BedroomChoice;
import com.propertyvista.portal.rpc.portal.PropertySearchCriteria.SearchType;

public class PropertyFinder {

    public static boolean isPropertyVisible(Building bld) {
        if (bld.isValueDetached()) {
            Persistence.service().retrieve(bld);
        }
        return PublicVisibilityType.global.equals(bld.marketing().visibility().getValue());
    }

    private static void addSearchCriteria(EntityQueryCriteria<Building> dbCriteria, PropertySearchCriteria searchCriteria) {
        // add search criteria
        if (SearchType.city.equals(searchCriteria.searchType().getValue())) {
            String prov = searchCriteria.province().getValue();
            if (prov != null) {
                dbCriteria.add(PropertyCriterion.eq(dbCriteria.proto().info().address().province().name(), prov));
            }
            String city = searchCriteria.city().getValue();
            if (city != null) {
                dbCriteria.add(PropertyCriterion.eq(dbCriteria.proto().info().address().city(), city));
            }
        } else {
            // Vicinity search within the given searchRadius of the centerPoint
            Integer searchRadiusKm = searchCriteria.distance().getValue();
            GeoPoint centerPoint = searchCriteria.geolocation().getValue();
            if (searchRadiusKm != null && centerPoint != null) {
                // Define dbCriteria here, It works perfectly in North America, TODO test other location
                GeoCircle geoCircle = new GeoCircle(centerPoint, searchRadiusKm);
                GeoBox geoBox = geoCircle.getMinBox();
                dbCriteria.add(PropertyCriterion.le(dbCriteria.proto().info().address().location(), geoBox.getNorthEast()));
                dbCriteria.add(PropertyCriterion.ge(dbCriteria.proto().info().address().location(), geoBox.getSouthWest()));
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
                Key key = am.belongsTo().getPrimaryKey();
                MutableInt count = amCounter.get(key);
                if (count == null) {
                    amCounter.put(key, new MutableInt(1));
                } else {
                    count.increment();
                }
            }
            // prepare building filter 1
            final Set<Key> bldFilter1 = new HashSet<Key>();
            for (Key key : amCounter.keySet()) {
                if (amCounter.get(key).intValue() == amSize) {
                    bldFilter1.add(key);
                }
            }
            // filter buildings with filter 1
            if (bldFilter1.size() == 0) {
                bldFilter1.add(Key.DORMANT_KEY);
            }
            dbCriteria.add(PropertyCriterion.in(dbCriteria.proto().id(), bldFilter1.toArray((Serializable[]) new Key[0])));
        }

        // 2. filter buildings by floorplans
        EntityQueryCriteria<Floorplan> fpCriteria = EntityQueryCriteria.create(Floorplan.class);

        // 2.1. filter floorplans by units
        EntityQueryCriteria<AptUnit> auCriteria = EntityQueryCriteria.create(AptUnit.class);
        // price
        Integer minPrice = searchCriteria.minPrice().getValue();
        if (minPrice != null) {
            auCriteria.add(new PropertyCriterion(auCriteria.proto().financial()._marketRent(), Restriction.GREATER_THAN_OR_EQUAL, minPrice));
        }
        Integer maxPrice = searchCriteria.maxPrice().getValue();
        if (maxPrice != null && maxPrice > 0) {
            auCriteria.add(new PropertyCriterion(auCriteria.proto().financial()._marketRent(), Restriction.LESS_THAN_OR_EQUAL, maxPrice));
        }
        // prepare new floorplan filter 1
        final Set<Key> fpSet1 = new HashSet<Key>();
        for (AptUnit unit : Persistence.service().query(auCriteria)) {
            fpSet1.add(unit.floorplan().getPrimaryKey());
        }
        // add floorplan unit criteria
        if (fpSet1.size() == 0) {
            fpSet1.add(Key.DORMANT_KEY);
        }
        fpCriteria.add(PropertyCriterion.in(fpCriteria.proto().id(), fpSet1.toArray((Serializable[]) new Key[0])));
        // 2.2 filter floorplans by other search criteria
        // beds
        BedroomChoice minBeds = searchCriteria.minBeds().getValue();
        if (minBeds != null && minBeds != BedroomChoice.Any) {
            fpCriteria.add(new PropertyCriterion(fpCriteria.proto().bedrooms(), Restriction.GREATER_THAN_OR_EQUAL, minBeds.getBeds()));
        }
        BedroomChoice maxBeds = searchCriteria.maxBeds().getValue();
        if (maxBeds != null && maxBeds != BedroomChoice.Any) {
            fpCriteria.add(new PropertyCriterion(fpCriteria.proto().bedrooms(), Restriction.LESS_THAN_OR_EQUAL, maxBeds.getBeds()));
        }
        // baths
        BathroomChoice minBaths = searchCriteria.minBaths().getValue();
        if (minBaths != null && minBaths != BathroomChoice.Any) {
            fpCriteria.add(new PropertyCriterion(fpCriteria.proto().bathrooms(), Restriction.GREATER_THAN_OR_EQUAL, minBaths.getBaths()));
        }
        BathroomChoice maxBaths = searchCriteria.maxBaths().getValue();
        if (maxBaths != null && maxBaths != BathroomChoice.Any) {
            fpCriteria.add(new PropertyCriterion(fpCriteria.proto().bathrooms(), Restriction.LESS_THAN_OR_EQUAL, maxBaths.getBaths()));
        }
        // prepare building filter 2
        final Set<Key> bldFilter2 = new HashSet<Key>();
        for (Floorplan fp : Persistence.service().query(fpCriteria)) {
            bldFilter2.add(fp.building().getPrimaryKey());
        }
        // filter buildings with filter 2
        if (bldFilter2.size() == 0) {
            bldFilter2.add(Key.DORMANT_KEY);
        }
        dbCriteria.add(PropertyCriterion.in(dbCriteria.proto().id(), bldFilter2.toArray((Serializable[]) new Key[0])));

    }

    public static List<Building> getPropertyList(PropertySearchCriteria searchCriteria) {
        EntityQueryCriteria<Building> dbCriteria = EntityQueryCriteria.create(Building.class);
        if (searchCriteria != null) {
            addSearchCriteria(dbCriteria, searchCriteria);
        }

        // get buildings
        final List<Building> buildings = Persistence.service().query(dbCriteria);
        // sanity check
        ArrayList<Building> remove = new ArrayList<Building>();
        for (Building bld : buildings) {
            // do some sanity check
            if (!isPropertyVisible(bld)) {
                remove.add(bld);
                continue;
            }
            if (bld.info().address().location().isNull() || bld.info().address().location().getValue().getLat() == 0) {
                remove.add(bld);
                continue;
            }
            if (getBuildingFloorplans(bld).size() < 1) {
                remove.add(bld);
                continue;
            }
        }
        buildings.removeAll(remove);

        return buildings;
    }

    public static boolean isPublicFileMedia(Media m) {
        return (m.type().getValue() == Media.Type.file && m.visibility().getValue() == PublicVisibilityType.global);
    }

    public static Building getBuildingDetails(long propId) {
        EntityQueryCriteria<Building> dbCriteria = EntityQueryCriteria.create(Building.class);
        dbCriteria.add(PropertyCriterion.eq(dbCriteria.proto().id(), new Key(propId)));
        List<Building> buildings = Persistence.service().query(dbCriteria);
        if (buildings.size() != 1) {
            return null;
        }
        Building bld = buildings.get(0);
        if (!isPropertyVisible(bld)) {
            return null;
        }
        // check if we have any valid floorplans
        if (getBuildingFloorplans(bld).size() < 1) {
            return null;
        }
        // attach phone info
        Persistence.service().retrieve(bld.contacts().phones());
        return bld;
    }

    public static Map<Floorplan, List<AptUnit>> getBuildingFloorplans(Building bld) {
        if (!isPropertyVisible(bld)) {
            return null;
        }
        final Map<Floorplan, List<AptUnit>> floorplans = new HashMap<Floorplan, List<AptUnit>>();
        EntityQueryCriteria<Floorplan> criteria = EntityQueryCriteria.create(Floorplan.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().building(), bld));
        for (Floorplan fp : Persistence.service().query(criteria)) {
            List<AptUnit> units = getBuildingAptUnits(bld, fp);
            // do some sanity check so we don't render incomplete floorplans
            if (units.size() > 0) {
                floorplans.put(fp, units);
            }
        }
        return floorplans;
    }

    public static List<AptUnit> getBuildingAptUnits(Building bld, Floorplan fp) {
        if (!isPropertyVisible(bld)) {
            return null;
        }

        EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().belongsTo(), bld));
        criteria.add(PropertyCriterion.eq(criteria.proto().floorplan(), fp));
        return Persistence.service().query(criteria);
    }

    public static List<BuildingAmenity> getBuildingAmenities(Building bld) {
        if (!isPropertyVisible(bld)) {
            return null;
        }

        EntityQueryCriteria<BuildingAmenity> criteria = EntityQueryCriteria.create(BuildingAmenity.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().belongsTo(), bld));
        return Persistence.service().query(criteria);
    }

    public static Floorplan getFloorplanDetails(long planId) {
        EntityQueryCriteria<Floorplan> dbCriteria = EntityQueryCriteria.create(Floorplan.class);
        dbCriteria.add(PropertyCriterion.eq(dbCriteria.proto().id(), new Key(planId)));
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
        return fp;
    }

    public static List<AptUnit> getFloorplanUnits(Floorplan fp) {
        if (!isPropertyVisible(fp.building())) {
            return null;
        }

        EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().belongsTo(), fp.building()));
        criteria.add(PropertyCriterion.eq(criteria.proto().floorplan(), fp));
        return Persistence.service().query(criteria);
    }

    public static List<FloorplanAmenity> getFloorplanAmenities(Floorplan fp) {
        if (!isPropertyVisible(fp.building())) {
            return null;
        }
        EntityQueryCriteria<FloorplanAmenity> criteria = EntityQueryCriteria.create(FloorplanAmenity.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().belongsTo(), fp));
        return Persistence.service().query(criteria);
    }

}
