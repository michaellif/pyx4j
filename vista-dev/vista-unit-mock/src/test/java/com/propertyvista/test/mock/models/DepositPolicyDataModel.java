/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 20, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.test.mock.models;

import java.math.BigDecimal;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.policy.policies.DepositPolicy;
import com.propertyvista.domain.policy.policies.domain.DepositPolicyItem;
import com.propertyvista.domain.policy.policies.domain.DepositPolicyItem.ValueType;
import com.propertyvista.domain.tenant.lease.Deposit.DepositType;
import com.propertyvista.test.mock.MockDataModel;
import com.propertyvista.test.mock.models.ARCodeDataModel.Code;

public class DepositPolicyDataModel extends MockDataModel<DepositPolicy> {

    public DepositPolicyDataModel() {
    }

    @Override
    protected void generate() {
        DepositPolicy policy = EntityFactory.create(DepositPolicy.class);

        ARCode chargeCode = getDataModel(ARCodeDataModel.class).getARCode(Code.deposit);
        for (ARCode type : getDataModel(ARCodeDataModel.class).getAllItems()) {
            DepositPolicyItem item = null;
            String product = null;
            ARCode serviceItemType = type;
            product = serviceItemType.type().getStringView();

            switch (serviceItemType.type().getValue()) {
            case Residential:
                item = EntityFactory.create(DepositPolicyItem.class);
                item.depositType().setValue(DepositType.LastMonthDeposit);
                item.valueType().setValue(ValueType.Percentage);
                item.value().setValue(new BigDecimal("1.0"));
                break;
            case Parking:
                item = EntityFactory.create(DepositPolicyItem.class);
                item.depositType().setValue(DepositType.SecurityDeposit);
                item.valueType().setValue(ValueType.Percentage);
                item.value().setValue(new BigDecimal("1.0"));
                break;
            case Locker:
                item = EntityFactory.create(DepositPolicyItem.class);
                item.depositType().setValue(DepositType.SecurityDeposit);
                item.valueType().setValue(ValueType.Percentage);
                item.value().setValue(new BigDecimal("1.0"));
                break;
            case Pet:
                item = EntityFactory.create(DepositPolicyItem.class);
                item.depositType().setValue(DepositType.SecurityDeposit);
                item.valueType().setValue(ValueType.Monetary);
                item.value().setValue(new BigDecimal("200.00"));
                break;

            default:
                break;
            }

            if (item != null) {
                item.productCode().set(type);
                item.chargeCode().set(chargeCode);
                item.annualInterestRate().setValue(new BigDecimal("0.12"));
                item.description().setValue(item.depositType().getStringView() + ", " + product);
                policy.policyItems().add(item);
            }
        }

        policy.node().set(getDataModel(PmcDataModel.class).getOrgNode());

        Persistence.service().persist(policy);
        addItem(policy);
    }

}
