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

public interface YardiLeaseManager extends YardiMockManager {

    public interface LeaseBuilder {

        LeaseBuilder setRentAmount(String amount);

        LeaseBuilder setLeaseFrom(String date);

        LeaseBuilder setLeaseTo(String date);

        LeaseBuilder setExpectedMoveIn(String date);

        LeaseBuilder setExpectedMoveOut(String date);

        LeaseBuilder setActualMoveIn(String date);

        LeaseBuilder setActualMoveOut(String date);

        TenantBuilder addTenant(String tenantId, String name);

        LeaseChargeBuilder addCharge(String chargeId, String amount);
    }

    public interface TenantBuilder {

        TenantBuilder setEmail(String email);

        TenantBuilder setResponsibleForLease(boolean responsible);

        LeaseBuilder done();
    }

    public interface LeaseChargeBuilder {

        LeaseChargeBuilder setFromDate(String date);

        LeaseChargeBuilder setToDate(String date);

        LeaseChargeBuilder setChargeCode(String chargeCode);

        LeaseChargeBuilder setGlAccountNumber(String account);

        LeaseChargeBuilder setDescription(String text);

        LeaseChargeBuilder setComment(String text);

        LeaseBuilder done();
    }

    LeaseBuilder addDefaultLease();

    LeaseBuilder addLease(String buildingId, String unitId, String leaseId);
}
