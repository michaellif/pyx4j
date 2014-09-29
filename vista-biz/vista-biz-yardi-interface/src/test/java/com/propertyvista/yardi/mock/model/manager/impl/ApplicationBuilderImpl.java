/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 23, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.yardi.mock.model.manager.impl;

import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.yardi.mock.model.YardiMock;
import com.propertyvista.yardi.mock.model.domain.YardiBuilding;
import com.propertyvista.yardi.mock.model.domain.YardiFee;
import com.propertyvista.yardi.mock.model.domain.YardiGuestEvent;
import com.propertyvista.yardi.mock.model.domain.YardiGuestEvent.Type;
import com.propertyvista.yardi.mock.model.domain.YardiLease;
import com.propertyvista.yardi.mock.model.domain.YardiRentableItem;
import com.propertyvista.yardi.mock.model.domain.YardiTenant;
import com.propertyvista.yardi.mock.model.domain.YardiUnit;
import com.propertyvista.yardi.mock.model.manager.YardiGuestManager.ApplicationBuilder;
import com.propertyvista.yardi.mock.model.manager.YardiGuestManager.GuestBuilder;
import com.propertyvista.yardi.mock.model.manager.YardiGuestManager.GuestEventBuilder;

public class ApplicationBuilderImpl extends LeaseBuilderImpl implements ApplicationBuilder {

    private final YardiBuilding building;

    private final YardiLease lease;

    ApplicationBuilderImpl(YardiLease lease, YardiBuilding building) {
        super(lease, new BuildingBuilderImpl(building));
        this.building = building;
        this.lease = lease;
    }

    @Override
    public ApplicationBuilder setUnit(String unitId) {
        YardiUnit unit = YardiMockModelUtils.findUnit(building, unitId);
        if (unit == null) {
            throw new Error("Unit not found: " + unitId);
        }
        lease.unit().set(unit);
        lease.currentRent().set(unit.rent());

        return this;
    }

    @Override
    public ApplicationBuilder addRentableItem(String itemId) {
        assert itemId != null : "item id cannot be null";

        YardiRentableItem item = YardiMockModelUtils.findRentableItem(building, itemId);
        if (item == null) {
            throw new Error("Item not found: " + itemId);
        }
        lease.application().rentableItems().add(item);

        return this;
    }

    @Override
    public ApplicationBuilder addFee(String amount, String chargeCode, String description) {
        assert amount != null : "amount cannot be null";
        assert chargeCode != null : "charge code cannot be null";

        YardiFee fee = EntityFactory.create(YardiFee.class);
        fee.amount().setValue(YardiMockModelUtils.toAmount(amount));
        fee.chargeCode().setValue(chargeCode);
        fee.description().setValue(description);
        lease.application().charges().add(fee);

        addTransaction("appFee" + lease.application().charges().size(), chargeCode, amount) //
                .setTransactionDate(YardiMockModelUtils.format(SystemDateManager.getLogicalDate())) //
                .setDescription(description);

        return this;
    }

    @Override
    public GuestBuilder addGuest(String guestId, String name) {
        assert guestId != null : "guest id cannot be null";
        assert name != null : "name cannot be null";

        if (YardiMockModelUtils.findGuest(lease, guestId) != null) {
            throw new Error("Guest already exists: " + guestId);
        }

        YardiTenant tenant = EntityFactory.create(YardiTenant.class);
        tenant.guestId().setValue(guestId);
        lease.tenants().add(tenant);

        return (GuestBuilder) new GuestBuilderImpl(tenant, this).setName(name);
    }

    @Override
    public GuestBuilder getGuest(String guestId) {
        assert guestId != null : "guest id cannot be null";

        YardiTenant guest = YardiMockModelUtils.findGuest(lease, guestId);

        return guest == null ? null : new GuestBuilderImpl(guest, this);
    }

    @Override
    public GuestEventBuilder addEvent(Type type) {
        assert type != null : "type cannot be null";

        YardiGuestEvent event = EntityFactory.create(YardiGuestEvent.class);
        event.type().setValue(type);
        lease.application().events().add(event);

        // process event; TODO - handle lease status
        if (Type.LEASE_SIGN.equals(type)) {
            boolean isMainTenant = true;
            for (YardiTenant tenant : lease.tenants()) {
                // set resident id and type; first prospect will be the main tenant
                tenant.tenantId().setValue(YardiMock.server().getModel().toResidentId(tenant.prospectId().getValue(), isMainTenant));
                tenant.type().setValue(isMainTenant ? YardiTenant.Type.FUTURE_RESIDENT : YardiTenant.Type.CUSTOMER);
                // set lease id to main tenant id
                if (isMainTenant) {
                    lease.leaseId().set(tenant.tenantId());
                }
                isMainTenant = false;
            }
        }

        return new GuestEventBuilderImpl(event, this);
    }
}
