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
import java.util.Map;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.policy.policies.PADDebitPolicyItem;
import com.propertyvista.domain.policy.policies.PADPolicy;
import com.propertyvista.domain.policy.policies.PADPolicy.OwingBalanceType;
import com.propertyvista.domain.policy.policies.PADPolicy.PADChargeType;
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

        Map<ARCode, OwingBalanceType> padBalanceTypeMap = getConfig().padBalanceTypeMap;

        if (padBalanceTypeMap == null) {
            padBalanceTypeMap = new HashMap<ARCode, OwingBalanceType>();
            padBalanceTypeMap.put(getDataModel(ARCodeDataModel.class).getARCode(ARCodeDataModel.Code.rent), OwingBalanceType.LastBill);
            padBalanceTypeMap.put(getDataModel(ARCodeDataModel.class).getARCode(ARCodeDataModel.Code.outdoorParking), OwingBalanceType.ToDateTotal);
            padBalanceTypeMap.put(getDataModel(ARCodeDataModel.class).getARCode(ARCodeDataModel.Code.largeLocker), OwingBalanceType.ToDateTotal);
            padBalanceTypeMap.put(ServerSideFactory.create(ARFacade.class).getReservedARCode(ARCode.Type.AccountCredit), OwingBalanceType.ToDateTotal);

        }
        for (ARCode code : padBalanceTypeMap.keySet()) {
            PADDebitPolicyItem item = EntityFactory.create(PADDebitPolicyItem.class);
            item.arCode().set(code);
            item.owingBalanceType().setValue(padBalanceTypeMap.get(code));
            policy.debitBalanceTypes().add(item);

        }
        policy.node().set(pmcDataModel.getOrgNode());
        Persistence.service().persist(policy);
        addItem(policy);
    }

}
