/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 14, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.test.mock.models;

import java.util.HashMap;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.billing.InvoiceDebit.DebitType;
import com.propertyvista.domain.policy.policies.PADPolicy;
import com.propertyvista.domain.policy.policies.PADPolicy.OwingBalanceType;
import com.propertyvista.domain.policy.policies.PADPolicy.PADChargeType;
import com.propertyvista.domain.policy.policies.PADPolicyItem;
import com.propertyvista.test.mock.MockDataModel;

public class PADPolicyDataModel extends MockDataModel<PADPolicy> {
    private PADPolicy policy;

    private PmcDataModel pmcDataModel;

    public PADPolicyDataModel() {

    }

    @Override
    protected void generate() {

        pmcDataModel = getDataModel(PmcDataModel.class);

        policy = EntityFactory.create(PADPolicy.class);
        if (getConfig().padChargeType == null) {
            getConfig().padChargeType = PADChargeType.OwingBalance;
        }
        policy.chargeType().setValue(getConfig().padChargeType);

        if (getConfig().padChargeType == PADChargeType.OwingBalance) {
            policy.chargeType().setValue(PADChargeType.OwingBalance);
            if (getConfig().padBalanceTypeMap == null) {
                getConfig().padBalanceTypeMap = new HashMap<DebitType, OwingBalanceType>();
                getConfig().padBalanceTypeMap.put(DebitType.lease, OwingBalanceType.LastBill);
                getConfig().padBalanceTypeMap.put(DebitType.parking, OwingBalanceType.ToDateTotal);
                getConfig().padBalanceTypeMap.put(DebitType.locker, OwingBalanceType.ToDateTotal);
            }
            for (DebitType debitType : getConfig().padBalanceTypeMap.keySet()) {
                PADPolicyItem item = EntityFactory.create(PADPolicyItem.class);
                item.debitType().setValue(debitType);
                item.owingBalanceType().setValue(getConfig().padBalanceTypeMap.get(debitType));
                policy.debitBalanceTypes().add(item);
            }
        }
        policy.node().set(pmcDataModel.getOrgNode());
        Persistence.service().persist(policy);
    }

    PADPolicy getPolicy() {
        return policy;
    }

}
