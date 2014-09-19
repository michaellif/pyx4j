/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 15, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.yardi.mock.model.manager.impl;

import java.math.BigDecimal;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.yardi.mock.model.YardiMock;
import com.propertyvista.yardi.mock.model.domain.YardiAddress;
import com.propertyvista.yardi.mock.model.domain.YardiBuilding;
import com.propertyvista.yardi.mock.model.domain.YardiFloorplan;
import com.propertyvista.yardi.mock.model.domain.YardiLease;
import com.propertyvista.yardi.mock.model.domain.YardiRentableItem;
import com.propertyvista.yardi.mock.model.domain.YardiUnit;
import com.propertyvista.yardi.mock.model.manager.YardiBuildingManager;
import com.propertyvista.yardi.mock.model.manager.YardiLeaseManager;
import com.propertyvista.yardi.mock.model.manager.YardiLeaseManager.LeaseBuilder;

public class YardiBuildingManagerImpl extends YardiMockManagerBase implements YardiBuildingManager {

    @Override
    public BuildingBuilder addDefaultBuilding() {
        YardiBuilding building = EntityFactory.create(YardiBuilding.class);
        building.buildingId().setValue(DEFAULT_PROPERTY_CODE);
        building.address().set(makeAddress(null));
        building.floorplans().add(makeFloorplan(DEFAULT_FP_NAME, DEFAULT_FP_BATHS, DEFAULT_FP_BEDS));
        building.units().add(makeUnit(DEFAULT_UNIT_NO, building.floorplans().get(0), new BigDecimal(DEFAULT_UNIT_RENT)));
        // save
        addBuilding(building);

        return new BuildingBuilderImpl(building);
    }

    @Override
    public BuildingBuilder addBuilding(String propertyId) {
        assert propertyId != null : "property id cannot be null";

        YardiBuilding building = EntityFactory.create(YardiBuilding.class);
        building.buildingId().setValue(propertyId);

        addBuilding(building);

        return new BuildingBuilderImpl(building);
    }

    @Override
    public BuildingBuilder getBuilding(String propertyId) {
        assert propertyId != null : "property id cannot be null";

        YardiBuilding building = findBuilding(propertyId);
        if (building == null) {
            throw new Error("Building not found: " + propertyId);
        }
        return new BuildingBuilderImpl(building);
    }

    // Factory methods
    private YardiUnit makeUnit(String id, YardiFloorplan fp, BigDecimal unitRent) {
        assert id != null : "unit id cannot be null";
        assert fp != null : "floorplan cannot be null";
        assert unitRent != null : "unit rent cannot be null";

        YardiUnit unit = EntityFactory.create(YardiUnit.class);
        unit.unitId().setValue(id);
        unit.floorplan().set(fp);
        unit.rent().setValue(unitRent);
        return unit;
    }

    private YardiFloorplan makeFloorplan(String id, int beds, int baths) {
        assert id != null : "floorplan id cannot be null";

        YardiFloorplan fp = EntityFactory.create(YardiFloorplan.class);
        fp.floorplanId().setValue(id);
        fp.bathrooms().setValue(baths);
        fp.bedrooms().setValue(beds);
        return fp;
    }

    /** Use addrStr in the form of "Street Address, City, Province, Country, PostalCode" */
    private YardiAddress makeAddress(String addrStr) {
        YardiAddress address = EntityFactory.create(YardiAddress.class);
        String[] parts = addrStr == null ? new String[0] : addrStr.split(",");
        address.street().setValue(parts.length > 0 ? parts[0].trim() : DEFAULT_ADDR_STREET);
        address.city().setValue(parts.length > 1 ? parts[1].trim() : DEFAULT_ADDR_CITY);
        address.province().setValue(parts.length > 2 ? parts[2].trim() : DEFAULT_ADDR_PROV);
        address.country().setValue(parts.length > 3 ? parts[3].trim() : DEFAULT_ADDR_COUNTRY);
        address.postalCode().setValue(parts.length > 4 ? parts[4].trim() : DEFAULT_ADDR_POSTCODE);
        return address;
    }

    public class BuildingBuilderImpl implements BuildingBuilder {

        private final YardiBuilding building;

        BuildingBuilderImpl(YardiBuilding building) {
            this.building = building;
        }

        @Override
        public BuildingBuilder setAddress(String address) {
            building.address().set(makeAddress(address));
            return this;
        }

        @Override
        public BuildingBuilder addFloorplan(String id, int beds, int baths) {
            building.floorplans().add(makeFloorplan(id, beds, baths));
            return this;
        }

        @Override
        public BuildingBuilder addUnit(String id, String fpId, BigDecimal unitRent) {
            building.units().add(makeUnit(id, findFloorplan(building, fpId), unitRent));
            return this;
        }

        @Override
        public RentableItemBuilder addRentableItem(String itemId, String price, String chargeCode) {
            assert itemId != null : "item id cannot be null";
            assert price != null : "price cannot be null";
            assert chargeCode != null : "charge code cannot be null";

            YardiRentableItem item = EntityFactory.create(YardiRentableItem.class);
            item.itemId().setValue(itemId);
            item.price().setValue(toAmount(price));
            item.chargeCode().setValue(chargeCode);

            return new RentableItemBuilderImpl(item);
        }

        @Override
        public LeaseBuilder getLease(String leaseId) {
            YardiLease lease = findLease(building, leaseId);
            if (lease == null) {
                throw new Error("Lease not found: " + leaseId);
            }

            return ((YardiLeaseManagerImpl) YardiMock.server().getManager(YardiLeaseManager.class)).new LeaseBuilderImpl(lease);
        }

        public class RentableItemBuilderImpl implements RentableItemBuilder {

            private final YardiRentableItem item;

            RentableItemBuilderImpl(YardiRentableItem item) {
                this.item = item;
            }

            @Override
            public RentableItemBuilder setDescription(String text) {
                item.description().setValue(text);
                return this;
            }

            @Override
            public BuildingBuilder done() {
                return BuildingBuilderImpl.this;
            }
        }
    }
}
