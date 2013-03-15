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
package com.propertyvista.test.preloader;

import java.util.HashMap;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.billing.InvoiceDebit.DebitType;
import com.propertyvista.domain.policy.policies.PADPolicy;
import com.propertyvista.domain.policy.policies.PADPolicy.OwingBalanceType;
import com.propertyvista.domain.policy.policies.PADPolicy.PADChargeType;
import com.propertyvista.domain.policy.policies.PADPolicyItem;

public class PADPolicyDataModel {
    private PADPolicy policy;

    private final PreloadConfig config;

    private final PmcDataModel pmcDataModel;

    public PADPolicyDataModel(PreloadConfig config, PmcDataModel pmcDataModel) {
        this.config = config;
        this.pmcDataModel = pmcDataModel;
    }

    public void generate() {
        policy = EntityFactory.create(PADPolicy.class);
        if (config.padChargeType == null) {
            config.padChargeType = PADChargeType.OwingBalance;
        }
        policy.chargeType().setValue(config.padChargeType);

        if (config.padChargeType == PADChargeType.OwingBalance) {
            policy.chargeType().setValue(PADChargeType.OwingBalance);
            if (config.padBalanceTypeMap == null) {
                config.padBalanceTypeMap = new HashMap<DebitType, OwingBalanceType>();
                config.padBalanceTypeMap.put(DebitType.lease, OwingBalanceType.LastBill);
                config.padBalanceTypeMap.put(DebitType.parking, OwingBalanceType.ToDateTotal);
                config.padBalanceTypeMap.put(DebitType.locker, OwingBalanceType.ToDateTotal);
            }
            for (DebitType debitType : config.padBalanceTypeMap.keySet()) {
                PADPolicyItem item = EntityFactory.create(PADPolicyItem.class);
                item.debitType().setValue(debitType);
                item.owingBalanceType().setValue(config.padBalanceTypeMap.get(debitType));
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
