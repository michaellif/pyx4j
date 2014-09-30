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

import java.math.BigDecimal;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.yardi.mock.model.domain.YardiLease;
import com.propertyvista.yardi.mock.model.domain.YardiLeaseCharge;
import com.propertyvista.yardi.mock.model.domain.YardiTenant;
import com.propertyvista.yardi.mock.model.domain.YardiTenant.Type;
import com.propertyvista.yardi.mock.model.domain.YardiTransaction;
import com.propertyvista.yardi.mock.model.manager.YardiBuildingManager.BuildingBuilder;
import com.propertyvista.yardi.mock.model.manager.YardiLeaseManager.LeaseBuilder;
import com.propertyvista.yardi.mock.model.manager.YardiLeaseManager.LeaseChargeBuilder;
import com.propertyvista.yardi.mock.model.manager.YardiLeaseManager.TenantBuilder;
import com.propertyvista.yardi.mock.model.manager.YardiLeaseManager.TransactionBuilder;

public class LeaseBuilderImpl implements LeaseBuilder {

    private final YardiLease lease;

    private final BuildingBuilder parent;

    LeaseBuilderImpl(YardiLease lease, BuildingBuilder parent) {
        this.lease = lease;
        this.parent = parent;
    }

    @Override
    public LeaseBuilder setRentAmount(String amount) {
        lease.currentRent().setValue(YardiMockModelUtils.toAmount(amount));
        return this;
    }

    @Override
    public LeaseBuilder setLeaseFrom(String date) {
        lease.leaseFrom().setValue(YardiMockModelUtils.toLogicalDate(date));
        return this;
    }

    @Override
    public LeaseBuilder setLeaseTo(String date) {
        lease.leaseTo().setValue(YardiMockModelUtils.toLogicalDate(date));
        return this;
    }

    @Override
    public LeaseBuilder setExpectedMoveIn(String date) {
        lease.expectedMoveIn().setValue(YardiMockModelUtils.toLogicalDate(date));
        return this;
    }

    @Override
    public LeaseBuilder setExpectedMoveOut(String date) {
        lease.expectedMoveOut().setValue(YardiMockModelUtils.toLogicalDate(date));
        return this;
    }

    @Override
    public LeaseBuilder setActualMoveIn(String date) {
        lease.actualMoveIn().setValue(YardiMockModelUtils.toLogicalDate(date));
        return this;
    }

    @Override
    public LeaseBuilder setActualMoveOut(String date) {
        lease.actualMoveOut().setValue(YardiMockModelUtils.toLogicalDate(date));
        return this;
    }

    @Override
    public BuildingBuilder done() {
        return parent;
    }

    @Override
    public TenantBuilder addTenant(String tenantId, String name) {
        assert tenantId != null : "tenant id cannot be null";
        assert tenantId.matches("^[rt].*") : "tenant id should start with 'r' or 't' :" + tenantId;
        assert name != null : "name cannot be null";

        if (YardiMockModelUtils.findTenant(lease, tenantId) != null) {
            throw new Error("Tenant already exists: " + tenantId);
        }

        YardiTenant tenant = EntityFactory.create(YardiTenant.class);
        tenant.tenantId().setValue(tenantId);
        tenant.responsibleForLease().setValue(true);
        if (tenantId.startsWith("t")) {
            lease.leaseId().setValue(tenantId);
            tenant.type().setValue(Type.CURRENT_RESIDENT);
        } else {
            tenant.type().setValue(Type.CUSTOMER);
        }

        lease.tenants().add(tenant);
        return new TenantBuilderImpl(tenant, this).setName(name);
    }

    @Override
    public LeaseChargeBuilder addCharge(String chargeId, String chargeCode, String amount) {
        assert chargeId != null : "charge id cannot be null";
        assert amount != null : "amount cannot be null";

        if (YardiMockModelUtils.findLeaseCharge(lease, chargeId) != null) {
            throw new Error("Lease Charge already exists: " + chargeId);
        }

        YardiLeaseCharge charge = EntityFactory.create(YardiLeaseCharge.class);
        charge.chargeId().setValue(chargeId);
        charge.amount().setValue(YardiMockModelUtils.toAmount(amount));
        charge.chargeCode().setValue(chargeCode);

        lease.charges().add(charge);
        return new LeaseChargeBuilderImpl(charge, this);
    }

    @Override
    public LeaseChargeBuilder addRentCharge(String chargeId, String chargeCode) {
        return addCharge(chargeId, chargeCode, YardiMockModelUtils.format(lease.currentRent().getValue())) //
                .setFromDate(YardiMockModelUtils.format(lease.leaseFrom().getValue())) //
                .setToDate(YardiMockModelUtils.format(lease.leaseTo().getValue())) //
                .setDescription("Monthly Rent");
    }

    @Override
    public TransactionBuilder addTransaction(String transId, String chargeCode, String amount) {
        assert transId != null : "transaction id cannot be null";
        assert amount != null : "amount cannot be null";

        if (YardiMockModelUtils.findTransaction(lease, transId) != null) {
            throw new Error("Transaction already exists: " + transId);
        }

        YardiTransaction trans = EntityFactory.create(YardiTransaction.class);
        trans.transactionId().setValue(transId);
        trans.amount().setValue(YardiMockModelUtils.toAmount(amount));
        trans.chargeCode().setValue(chargeCode);
        // defaults
        trans.amountPaid().setValue(BigDecimal.ZERO);
        trans.balanceDue().set(trans.amount());

        lease.transactions().add(trans);
        return new TransactionBuilderImpl(trans, this);
    }

    @Override
    public TenantBuilder getTenant(String tenantId) {
        assert tenantId != null : "tenant id cannot be null";

        YardiTenant tenant = YardiMockModelUtils.findTenant(lease, tenantId);

        return tenant == null ? null : new TenantBuilderImpl(tenant, this);
    }

    @Override
    public LeaseChargeBuilder getCharge(String chargeId) {
        assert chargeId != null : "charge id cannot be null";

        YardiLeaseCharge charge = YardiMockModelUtils.findLeaseCharge(lease, chargeId);

        return charge == null ? null : new LeaseChargeBuilderImpl(charge, this);
    }

    @Override
    public TransactionBuilder getTransaction(String transId) {
        assert transId != null : "transaction id cannot be null";

        YardiTransaction charge = YardiMockModelUtils.findTransaction(lease, transId);

        return charge == null ? null : new TransactionBuilderImpl(charge, this);
    }
}
