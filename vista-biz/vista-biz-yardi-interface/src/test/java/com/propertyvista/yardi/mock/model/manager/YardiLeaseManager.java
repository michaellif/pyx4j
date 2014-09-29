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
package com.propertyvista.yardi.mock.model.manager;

import com.propertyvista.yardi.mock.model.domain.YardiTenant;
import com.propertyvista.yardi.mock.model.manager.YardiBuildingManager.BuildingBuilder;

public interface YardiLeaseManager extends YardiMockManager {

    public interface LeaseBuilder {

        LeaseBuilder setRentAmount(String amount);

        LeaseBuilder setLeaseFrom(String date);

        LeaseBuilder setLeaseTo(String date);

        LeaseBuilder setExpectedMoveIn(String date);

        LeaseBuilder setExpectedMoveOut(String date);

        LeaseBuilder setActualMoveIn(String date);

        LeaseBuilder setActualMoveOut(String date);

        BuildingBuilder done();

        TenantBuilder addTenant(String tenantId, String name);

        LeaseChargeBuilder addCharge(String chargeId, String chargeCode, String amount);

        LeaseChargeBuilder addRentCharge(String chargeId, String chargeCode);

        TransactionBuilder addTransaction(String transId, String chargeCode, String amount);

        TenantBuilder getTenant(String tenantId);

        LeaseChargeBuilder getCharge(String chargeId);

        TransactionBuilder getTransaction(String transId);
    }

    public interface TenantBuilder {

        TenantBuilder setProspectId(String prospectId);

        TenantBuilder setType(YardiTenant.Type type);

        TenantBuilder setName(String name);

        TenantBuilder setEmail(String email);

        TenantBuilder setResponsibleForLease(boolean responsible);

        LeaseBuilder done();
    }

    public interface LeaseChargeBuilder {

        LeaseChargeBuilder setAmount(String amount);

        LeaseChargeBuilder setChargeCode(String chargeCode);

        LeaseChargeBuilder setFromDate(String date);

        LeaseChargeBuilder setToDate(String date);

        LeaseChargeBuilder setGlAccountNumber(String account);

        LeaseChargeBuilder setDescription(String text);

        LeaseChargeBuilder setComment(String text);

        LeaseBuilder done();
    }

    public interface TransactionBuilder {

        TransactionBuilder setAmount(String amount);

        TransactionBuilder setChargeCode(String chargeCode);

        TransactionBuilder setTransactionDate(String date);

        TransactionBuilder setGlAccountNumber(String account);

        TransactionBuilder setAmountPaid(String amount);

        TransactionBuilder setBalanceDue(String amount);

        TransactionBuilder setDescription(String text);

        TransactionBuilder setComment(String text);

        LeaseBuilder done();
    }

    LeaseBuilder addDefaultLease();

    LeaseBuilder addLease(String leaseId, String buildingId, String unitId);

    LeaseBuilder getLease(String leaseId, String buildingId);
}
