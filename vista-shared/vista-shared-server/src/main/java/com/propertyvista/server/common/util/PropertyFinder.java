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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.MinMaxPair;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
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

    /**
     * Populate db query criteria based on the given PropertySearchCriteria on Building level
     * 
     * @param dbCriteria
     *            - db query criteria to update
     * @param searchCriteria
     *            - given search criteria
     * @return false if no buildings found to match the search criteria
     */
    private static void addSearchCriteria(EntityQueryCriteria<Building> dbCriteria, PropertySearchCriteria searchCriteria) {
        // add visibility check
        dbCriteria.eq(dbCriteria.proto().marketing().visibility(), PublicVisibilityType.global);
        // add sanity check
        dbCriteria.isNotNull(dbCriteria.proto().productCatalog());
        dbCriteria.isNotNull(dbCriteria.proto().info().location());

        // add search criteria
        if (searchCriteria != null) {
            if (SearchType.city.equals(searchCriteria.searchType().getValue())) {
                String prov = searchCriteria.province().getValue();
                if (!StringUtils.isEmpty(prov)) {
                    dbCriteria.like(dbCriteria.proto().info().address().province(), prov);
                }
                String city = searchCriteria.city().getValue();
                if (!StringUtils.isEmpty(city)) {
                    dbCriteria.like(dbCriteria.proto().info().address().city(), city);
                }
            } else {
                // Vicinity search within the given searchRadius of the centerPoint
                Integer searchRadiusKm = searchCriteria.distance().getValue();
                GeoPoint centerPoint = searchCriteria.geolocation().getValue();
                if (searchRadiusKm != null && centerPoint != null) {
                    // Define dbCriteria here, It works perfectly in North America, TODO test other location
                    GeoCircle geoCircle = new GeoCircle(centerPoint, searchRadiusKm);
                    GeoBox geoBox = geoCircle.getMinBox();
                    dbCriteria.le(dbCriteria.proto().info().location(), geoBox.getNorthEast());
                    dbCriteria.ge(dbCriteria.proto().info().location(), geoBox.getSouthWest());
                }
            }

            // filter buildings by amenities (must match all items in the set)
            int amSize = searchCriteria.amenities().size();
            if (amSize > 0) {
                EntityQueryCriteria<BuildingAmenity> amCriteria = EntityQueryCriteria.create(BuildingAmenity.class);
                amCriteria.in(amCriteria.proto().type(), searchCriteria.amenities());
                // prepare building amenity filter
                Map<Key, Set<BuildingAmenity.Type>> amFilter = new HashMap<>();
                for (BuildingAmenity am : Persistence.service().query(amCriteria)) {
                    Key bldId = am.building().getPrimaryKey();
                    Set<BuildingAmenity.Type> amSet = amFilter.get(bldId);
                    if (amSet == null) {
                        amFilter.put(bldId, amSet = new HashSet<BuildingAmenity.Type>());
                    }
                    amSet.add(am.type().getValue());
                }
                // filter buildings that have matched all items from the search criteria
                for (Iterator<Key> it = amFilter.keySet().iterator(); it.hasNext();) {
                    if (amFilter.get(it.next()).size() != amSize) {
                        it.remove();
                    }
                }
                dbCriteria.add(PropertyCriterion.in(dbCriteria.proto().id(), amFilter.keySet()));
            }

            // add Floorplan related criteria
            dbCriteria.addAll(getFloorplanQueryCriteria(dbCriteria.proto().floorplans().$(), searchCriteria));
        }
    }

    /**
     * Populate db query criteria based on the given PropertySearchCriteria on Floorplan level
     * 
     * @param fpCriteria
     *            - db query criteria to update
     * @param searchCriteria
     *            - given search criteria
     * @return false if no floorplans found to match the search criteria
     */
    private static List<Criterion> getFloorplanQueryCriteria(Floorplan proto, PropertySearchCriteria searchCriteria) {
        List<Criterion> criteria = new ArrayList<>();

        // filter units by finalized products only:
        criteria.add(PropertyCriterion.isNotNull(proto.units().$().productItems().$().product().fromDate()));
        criteria.add(PropertyCriterion.isNull(proto.units().$().productItems().$().product().toDate()));

        // filter by search criteria
        if (searchCriteria != null) {
            // filter floorplans by unit price
            Integer minPrice = searchCriteria.minPrice().getValue();
            if (minPrice != null) {
                criteria.add(PropertyCriterion.ge(proto.units().$().financial()._marketRent(), new BigDecimal(minPrice)));
            }
            Integer maxPrice = searchCriteria.maxPrice().getValue();
            if (maxPrice != null && maxPrice > 0) {
                criteria.add(PropertyCriterion.le(proto.units().$().financial()._marketRent(), new BigDecimal(maxPrice)));
            }

            // filter floorplans by other search criteria
            // beds
            BedroomChoice minBeds = searchCriteria.minBeds().getValue();
            if (minBeds != null && minBeds != BedroomChoice.Any) {
                criteria.add(PropertyCriterion.ge(proto.bedrooms(), minBeds.getBeds()));
            }
            BedroomChoice maxBeds = searchCriteria.maxBeds().getValue();
            if (maxBeds != null && maxBeds != BedroomChoice.Any) {
                criteria.add(PropertyCriterion.le(proto.bedrooms(), maxBeds.getBeds()));
            }
            // baths
            BathroomChoice minBaths = searchCriteria.minBaths().getValue();
            if (minBaths != null && minBaths != BathroomChoice.Any) {
                criteria.add(PropertyCriterion.ge(proto.bathrooms(), minBaths.getBaths()));
            }
            BathroomChoice maxBaths = searchCriteria.maxBaths().getValue();
            if (maxBaths != null && maxBaths != BathroomChoice.Any) {
                criteria.add(PropertyCriterion.le(proto.bathrooms(), maxBaths.getBaths()));
            }
        }
        return criteria;
    }

    public static List<Building> getPropertyList(PropertySearchCriteria searchCriteria) {
        return getPropertyList(searchCriteria, null);
    }

    public static List<Building> getPropertyList(PropertySearchCriteria searchCriteria, EntityQueryCriteria<Building> dbCriteria) {
        if (dbCriteria == null) {
            dbCriteria = EntityQueryCriteria.create(Building.class);
        }

        if (searchCriteria != null) {
            addSearchCriteria(dbCriteria, searchCriteria);
        }

        // get buildings with filtered floorplans and media
        List<Building> buildings = Persistence.service().query(dbCriteria);
        // prepare floorplan filter per search criteria
        EntityQueryCriteria<Floorplan> fpCriteria = EntityQueryCriteria.create(Floorplan.class);
        fpCriteria.addAll(getFloorplanQueryCriteria(fpCriteria.proto(), searchCriteria));
        Criterion parentBld = null;
        for (Building building : buildings) {
            // remove previous parent building criterion, if any
            if (parentBld != null) {
                fpCriteria.removeCriterions(parentBld);
            }
            // add new parent building criterion
            parentBld = PropertyCriterion.eq(fpCriteria.proto().building(), building);
            fpCriteria.add(parentBld);
            // set filtered floorplans
            building.floorplans().setAttachLevel(AttachLevel.Attached);
            building.floorplans().clear();
            building.floorplans().addAll(Persistence.service().query(fpCriteria));
            // retrieve media
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

        if (getFloorplanUnitCount(fp) < 1) {
            return null;
        }
        Persistence.service().retrieveMember(fp.media());
        return fp;
    }

    public static List<AptUnit> getFloorplanUnits(Floorplan fp) {
        if (!isPropertyVisible(fp.building())) {
            return Collections.emptyList();
        }

        return Persistence.service().query(getFloorplanUnitsCriteria(fp));
    }

    public static int getFloorplanUnitCount(Floorplan fp) {
        if (!isPropertyVisible(fp.building())) {
            return 0;
        }

        return Persistence.service().count(getFloorplanUnitsCriteria(fp));
    }

    public static List<FloorplanAmenity> getFloorplanAmenities(Floorplan fp) {
        if (!isPropertyVisible(fp.building())) {
            return Collections.emptyList();
        }
        EntityQueryCriteria<FloorplanAmenity> criteria = EntityQueryCriteria.create(FloorplanAmenity.class);
        criteria.eq(criteria.proto().floorplan(), fp);
        return Persistence.service().query(criteria);
    }

    // ---------------- internals ---------------------
    private static EntityQueryCriteria<AptUnit> getFloorplanUnitsCriteria(Floorplan fp) {
        EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
        criteria.eq(criteria.proto().building(), fp.building());
        criteria.eq(criteria.proto().floorplan(), fp);
        return criteria;
    }

}
