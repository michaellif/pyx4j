/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 30, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.policies.policy;

import java.util.List;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.financial.billingcycle.BillingCycleFacade;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.crm.rpc.services.policies.policy.LeaseBillingPolicyCrudService;
import com.propertyvista.crm.server.services.policies.GenericPolicyCrudService;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.policy.dto.LeaseBillingPolicyDTO;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;
import com.propertyvista.domain.property.asset.building.Building;

public class LeaseBillingPolicyCrudServiceImpl extends GenericPolicyCrudService<LeaseBillingPolicy, LeaseBillingPolicyDTO> implements
        LeaseBillingPolicyCrudService {

    public LeaseBillingPolicyCrudServiceImpl() {
        super(LeaseBillingPolicy.class, LeaseBillingPolicyDTO.class);
    }

    @Override
    protected LeaseBillingPolicyDTO init(InitializationData initializationData) {
        LeaseBillingPolicyDTO entity = super.init(initializationData);
        entity.prorationMethod().setValue(BillingAccount.ProrationMethod.Standard);
        return entity;
    }

    @Override
    protected void persist(LeaseBillingPolicy dbo, LeaseBillingPolicyDTO in) {
        super.persist(dbo, in);

        ServerSideFactory.create(BillingCycleFacade.class).onLeaseBillingPolicyChange(dbo);
    }

    @Override
    protected void delete(LeaseBillingPolicy actualEntity) {
        // retrieve affected buildings
        List<Building> buildings = ServerSideFactory.create(PolicyFacade.class).getGovernedNodesOfType(actualEntity, Building.class);

        super.delete(actualEntity);

        // update billing cycles for the affected buildings
        ServerSideFactory.create(BillingCycleFacade.class).onLeaseBillingPolicyDelete(buildings);
    }
}
