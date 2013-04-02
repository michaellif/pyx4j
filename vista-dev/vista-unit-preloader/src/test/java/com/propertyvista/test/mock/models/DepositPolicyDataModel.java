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

import org.apache.commons.lang.NotImplementedException;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.offering.FeatureItemType;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.domain.policy.policies.DepositPolicy;
import com.propertyvista.domain.policy.policies.domain.DepositPolicyItem;
import com.propertyvista.domain.policy.policies.domain.DepositPolicyItem.ValueType;
import com.propertyvista.domain.tenant.lease.Deposit.DepositType;
import com.propertyvista.test.mock.MockDataModel;

public class DepositPolicyDataModel extends MockDataModel<DepositPolicy> {

    public DepositPolicyDataModel() {
    }

    @Override
    protected void generate() {
        DepositPolicy policy = EntityFactory.create(DepositPolicy.class);

        if (getConfig().depositsEnabled) {

            for (FeatureItemType type : getDataModel(FeatureItemTypeDataModel.class).getAllItems()) {
                DepositPolicyItem item = null;
                String product = null;

                FeatureItemType featureItemType = type;
                product = featureItemType.featureType().getStringView();

                switch (featureItemType.featureType().getValue()) {
                case parking:
                    item = EntityFactory.create(DepositPolicyItem.class);
                    item.depositType().setValue(DepositType.SecurityDeposit);
                    item.valueType().setValue(ValueType.Percentage);
                    item.value().setValue(new BigDecimal("1.0"));
                    break;
                case locker:
                    item = EntityFactory.create(DepositPolicyItem.class);
                    item.depositType().setValue(DepositType.SecurityDeposit);
                    item.valueType().setValue(ValueType.Percentage);
                    item.value().setValue(new BigDecimal("1.0"));
                    break;
                case pet:
                    item = EntityFactory.create(DepositPolicyItem.class);
                    item.depositType().setValue(DepositType.SecurityDeposit);
                    item.valueType().setValue(ValueType.Monetary);
                    item.value().setValue(new BigDecimal("200.00"));
                    break;
                default:
                    break;
                }

                if (item != null) {
                    item.productType().set(type);
                    item.annualInterestRate().setValue(new BigDecimal("0.12"));
                    item.description().setValue(item.depositType().getStringView() + ", " + product);
                    policy.policyItems().add(item);
                }
            }

            for (ServiceItemType type : getDataModel(ServiceItemTypeDataModel.class).getAllItems()) {
                DepositPolicyItem item = null;
                String product = null;
                ServiceItemType serviceItemType = type;
                product = serviceItemType.serviceType().getStringView();

                switch (serviceItemType.serviceType().getValue()) {
                case residentialUnit:
                    item = EntityFactory.create(DepositPolicyItem.class);
                    item.depositType().setValue(DepositType.LastMonthDeposit);
                    item.valueType().setValue(ValueType.Percentage);
                    item.value().setValue(new BigDecimal("1.0"));
                    break;
                default:
                    break;
                }

                if (item != null) {
                    item.productType().set(type);
                    item.annualInterestRate().setValue(new BigDecimal("0.12"));
                    item.description().setValue(item.depositType().getStringView() + ", " + product);
                    policy.policyItems().add(item);
                }
            }

        }

        policy.node().set(getDataModel(BuildingDataModel.class).getCurrentItem());

        Persistence.service().persist(policy);
        addItem(policy);
        super.setCurrentItem(policy);
    }

    @Override
    public void setCurrentItem(DepositPolicy item) {
        throw new NotImplementedException();
    }
}
