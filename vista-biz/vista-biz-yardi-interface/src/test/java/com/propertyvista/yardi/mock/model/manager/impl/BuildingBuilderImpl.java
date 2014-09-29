/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 22, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.yardi.mock.model.manager.impl;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.yardi.mock.model.YardiMock;
import com.propertyvista.yardi.mock.model.domain.YardiAddress;
import com.propertyvista.yardi.mock.model.domain.YardiBuilding;
import com.propertyvista.yardi.mock.model.domain.YardiFloorplan;
import com.propertyvista.yardi.mock.model.domain.YardiLease;
import com.propertyvista.yardi.mock.model.domain.YardiRentableItem;
import com.propertyvista.yardi.mock.model.domain.YardiUnit;
import com.propertyvista.yardi.mock.model.manager.YardiBuildingManager;
import com.propertyvista.yardi.mock.model.manager.YardiBuildingManager.BuildingBuilder;
import com.propertyvista.yardi.mock.model.manager.YardiBuildingManager.RentableItemBuilder;
import com.propertyvista.yardi.mock.model.manager.YardiLeaseManager;
import com.propertyvista.yardi.mock.model.manager.YardiLeaseManager.LeaseBuilder;

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
    public BuildingBuilder addUnit(String id, String fpId, String unitRent, String depositLMR) {
        building.units().add(makeUnit(id, YardiMockModelUtils.findFloorplan(building, fpId), unitRent, depositLMR));
        return this;
    }

    @Override
    public BuildingBuilder setDepositLMR(String unitId, String amount) {
        assert unitId != null : "unit id cannot be null";

        YardiUnit unit = YardiMockModelUtils.findUnit(building, unitId);
        if (unit == null) {
            throw new Error("Unit not found: " + unitId);
        }

        unit.depositLMR().setValue(YardiMockModelUtils.toAmount(amount));
        return this;
    }

    @Override
    public RentableItemBuilder addRentableItem(String itemId, String price, String chargeCode) {
        assert itemId != null : "item id cannot be null";
        assert price != null : "price cannot be null";
        assert chargeCode != null : "charge code cannot be null";

        if (YardiMockModelUtils.findRentableItem(building, itemId) != null) {
            throw new Error("Rentable Item already exists: " + itemId);
        }

        YardiRentableItem item = EntityFactory.create(YardiRentableItem.class);
        item.itemId().setValue(itemId);
        item.price().setValue(YardiMockModelUtils.toAmount(price));
        item.chargeCode().setValue(chargeCode);

        building.rentableItems().add(item);

        return new RentableItemBuilderImpl(item, this);
    }

    @Override
    public RentableItemBuilder getRentableItem(String itemId) {
        YardiRentableItem item = YardiMockModelUtils.findRentableItem(building, itemId);

        return item == null ? null : new RentableItemBuilderImpl(item, this);
    }

    @Override
    public LeaseBuilder addLease(String leaseId, String unitId) {
        return YardiMock.server().getManager(YardiLeaseManager.class).addLease(leaseId, building.buildingId().getValue(), unitId);
    }

    @Override
    public LeaseBuilder getLease(String leaseId) {
        YardiLease lease = YardiMockModelUtils.findLease(building, leaseId);

        return lease == null ? null : new LeaseBuilderImpl(lease, this);
    }

    // Factory methods
    private YardiUnit makeUnit(String id, YardiFloorplan fp, String unitRent, String depositLMR) {
        assert id != null : "unit id cannot be null";
        assert fp != null : "floorplan cannot be null";
        assert unitRent != null : "unit rent cannot be null";

        YardiUnit unit = EntityFactory.create(YardiUnit.class);
        unit.unitId().setValue(id);
        unit.floorplan().set(fp);
        unit.rent().setValue(YardiMockModelUtils.toAmount(unitRent));
        unit.depositLMR().setValue(YardiMockModelUtils.toAmount(depositLMR));
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
        address.street().setValue(parts.length > 0 ? parts[0].trim() : YardiBuildingManager.DEFAULT_ADDR_STREET);
        address.city().setValue(parts.length > 1 ? parts[1].trim() : YardiBuildingManager.DEFAULT_ADDR_CITY);
        address.province().setValue(parts.length > 2 ? parts[2].trim() : YardiBuildingManager.DEFAULT_ADDR_PROV);
        address.country().setValue(parts.length > 3 ? parts[3].trim() : YardiBuildingManager.DEFAULT_ADDR_COUNTRY);
        address.postalCode().setValue(parts.length > 4 ? parts[4].trim() : YardiBuildingManager.DEFAULT_ADDR_POSTCODE);
        return address;
    }
}
