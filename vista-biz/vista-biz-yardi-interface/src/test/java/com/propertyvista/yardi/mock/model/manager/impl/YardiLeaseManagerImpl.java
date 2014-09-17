/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 17, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.yardi.mock.model.manager.impl;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.yardi.mock.model.domain.YardiBuilding;
import com.propertyvista.yardi.mock.model.domain.YardiLease;
import com.propertyvista.yardi.mock.model.domain.YardiLeaseCharge;
import com.propertyvista.yardi.mock.model.domain.YardiTenant;
import com.propertyvista.yardi.mock.model.domain.YardiUnit;
import com.propertyvista.yardi.mock.model.manager.YardiLeaseManager;

public class YardiLeaseManagerImpl extends YardiMockManagerBase implements YardiLeaseManager {

    @Override
    public LeaseBuilder addDefaultLease() {
        YardiLease lease = EntityFactory.create(YardiLease.class);
        // TODO - add default lease impl
        return new LeaseBuilderImpl(lease);
    }

    @Override
    public LeaseBuilder addLease(String buildingId, String unitId, String leaseId) {
        assert buildingId != null : "building id cannot be null";
        assert unitId != null : "unit id cannot be null";
        assert leaseId != null : "lease id cannot be null";

        YardiBuilding building = findBuilding(buildingId);
        if (building == null) {
            throw new Error("Building not found: " + buildingId);
        }
        YardiUnit unit = findUnit(building, unitId);
        if (unit == null) {
            throw new Error("Unit not found: " + buildingId + ":" + unitId);
        }
        if (findLease(building, leaseId) != null) {
            throw new Error("Lease already exists: " + leaseId);
        }

        YardiLease lease = EntityFactory.create(YardiLease.class);
        lease.leaseId().setValue(leaseId);
        lease.unit().set(unit);
        lease.currentRent().set(unit.rent());

        building.leases().add(lease);

        return new LeaseBuilderImpl(lease);
    }

    public class LeaseBuilderImpl implements LeaseBuilder {

        private final YardiLease lease;

        LeaseBuilderImpl(YardiLease lease) {
            this.lease = lease;
        }

        @Override
        public LeaseBuilder setRentAmount(String amount) {
            lease.currentRent().setValue(toAmount(amount));
            return this;
        }

        @Override
        public LeaseBuilder setLeaseFrom(String date) {
            lease.leaseFromDate().setValue(toDate(date));
            return this;
        }

        @Override
        public LeaseBuilder setLeaseTo(String date) {
            lease.leaseToDate().setValue(toDate(date));
            return this;
        }

        @Override
        public LeaseBuilder setExpectedMoveIn(String date) {
            lease.expectedMoveInDate().setValue(toDate(date));
            return this;
        }

        @Override
        public LeaseBuilder setExpectedMoveOut(String date) {
            lease.expectedMoveOutDate().setValue(toDate(date));
            return this;
        }

        @Override
        public LeaseBuilder setActualMoveIn(String date) {
            lease.actualMoveIn().setValue(toDate(date));
            return this;
        }

        @Override
        public LeaseBuilder setActualMoveOut(String date) {
            lease.actualMoveOut().setValue(toDate(date));
            return this;
        }

        @Override
        public TenantBuilder addTenant(String tenantId, String name) {
            assert tenantId != null : "tenant id cannot be null";
            assert name != null : "name cannot be null";

            if (findTenant(lease, tenantId) != null) {
                throw new Error("Tenant already exists: " + tenantId);
            }

            YardiTenant tenant = EntityFactory.create(YardiTenant.class);
            tenant.tenantId().setValue(tenantId);
            String[] nameParts = name.split(" ", 2);
            tenant.firstName().setValue(nameParts[0]);
            tenant.lastName().setValue(nameParts.length > 1 ? nameParts[1] : "");
            lease.tenants().add(tenant);
            return new TenantBuilderImpl(tenant);
        }

        @Override
        public LeaseChargeBuilder addCharge(String chargeId, String amount) {
            assert chargeId != null : "charge id cannot be null";
            assert amount != null : "amount cannot be null";

            if (findLeaseCharge(lease, chargeId) != null) {
                throw new Error("Lease Charge already exists: " + chargeId);
            }

            YardiLeaseCharge charge = EntityFactory.create(YardiLeaseCharge.class);
            charge.chargeId().setValue(chargeId);
            charge.amount().setValue(toAmount(amount));

            lease.charges().add(charge);
            return new LeaseChargeBuilderImpl(charge);
        }

        public class TenantBuilderImpl implements TenantBuilder {

            private final YardiTenant tenant;

            TenantBuilderImpl(YardiTenant tenant) {
                this.tenant = tenant;
            }

            @Override
            public TenantBuilder setEmail(String email) {
                tenant.email().setValue(email);
                return this;
            }

            @Override
            public TenantBuilder setResponsibleForLease(boolean responsible) {
                tenant.responsibleForLease().setValue(responsible);
                return this;
            }

            @Override
            public LeaseBuilder done() {
                return LeaseBuilderImpl.this;
            }
        }

        public class LeaseChargeBuilderImpl implements LeaseChargeBuilder {

            private final YardiLeaseCharge charge;

            LeaseChargeBuilderImpl(YardiLeaseCharge charge) {
                this.charge = charge;
            }

            @Override
            public LeaseChargeBuilder setFromDate(String date) {
                charge.serviceFromDate().setValue(toDate(date));
                return this;
            }

            @Override
            public LeaseChargeBuilder setToDate(String date) {
                charge.serviceToDate().setValue(toDate(date));
                return this;
            }

            @Override
            public LeaseChargeBuilder setChargeCode(String chargeCode) {
                charge.chargeCode().setValue(chargeCode);
                return this;
            }

            @Override
            public LeaseChargeBuilder setGlAccountNumber(String account) {
                charge.glAccountNumber().setValue(account);
                return this;
            }

            @Override
            public LeaseChargeBuilder setDescription(String text) {
                charge.description().setValue(text);
                return this;
            }

            @Override
            public LeaseChargeBuilder setComment(String text) {
                charge.comment().setValue(text);
                return this;
            }

            @Override
            public LeaseBuilder done() {
                return LeaseBuilderImpl.this;
            }
        }
    }
}
