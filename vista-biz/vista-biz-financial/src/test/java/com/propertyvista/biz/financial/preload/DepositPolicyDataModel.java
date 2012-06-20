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
package com.propertyvista.biz.financial.preload;

import java.math.BigDecimal;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.offering.FeatureItemType;
import com.propertyvista.domain.financial.offering.ProductItemType;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.domain.policy.policies.DepositPolicy;
import com.propertyvista.domain.policy.policies.domain.DepositPolicyItem;
import com.propertyvista.domain.tenant.lease.Deposit.DepositType;
import com.propertyvista.domain.tenant.lease.Deposit.ValueType;

public class DepositPolicyDataModel {

    private final ProductItemTypesDataModel productItemTypesDataModel;

    private final BuildingDataModel buildingDataModel;

    private DepositPolicy policy;

    public DepositPolicyDataModel(PreloadConfig config, ProductItemTypesDataModel productItemTypesDataModel, BuildingDataModel buildingDataModel) {
        this.productItemTypesDataModel = productItemTypesDataModel;
        this.buildingDataModel = buildingDataModel;
    }

    public void generate() {
        policy = EntityFactory.create(DepositPolicy.class);

        for (ProductItemType type : productItemTypesDataModel.getProductItemTypes()) {
            DepositPolicyItem item = null;
            if (type instanceof ServiceItemType) {
                ServiceItemType serviceItemType = (ServiceItemType) type;
                switch (serviceItemType.serviceType().getValue()) {
                case residentialUnit:
                    item = EntityFactory.create(DepositPolicyItem.class);
                    item.depositType().setValue(DepositType.LastMonthDeposit);
                    item.valueType().setValue(ValueType.percentage);
                    item.value().setValue(new BigDecimal("1.0"));
                    item.description().setValue("Lease");
                    break;
                default:
                    break;
                }
            } else if (type instanceof FeatureItemType) {
                FeatureItemType featureItemType = (FeatureItemType) type;

                switch (featureItemType.featureType().getValue()) {
                case parking:
                    item = EntityFactory.create(DepositPolicyItem.class);
                    item.depositType().setValue(DepositType.SecurityDeposit);
                    item.valueType().setValue(ValueType.percentage);
                    item.value().setValue(new BigDecimal("1.0"));
                    item.description().setValue("Parking");
                    break;
                case locker:
                    item = EntityFactory.create(DepositPolicyItem.class);
                    item.depositType().setValue(DepositType.SecurityDeposit);
                    item.valueType().setValue(ValueType.percentage);
                    item.value().setValue(new BigDecimal("1.0"));
                    item.description().setValue("Locker");
                    break;
                case pet:
                    item = EntityFactory.create(DepositPolicyItem.class);
                    item.depositType().setValue(DepositType.SecurityDeposit);
                    item.valueType().setValue(ValueType.amount);
                    item.value().setValue(new BigDecimal("200.00"));
                    item.description().setValue("Pet");
                    break;
                }
            }

            if (item != null) {
                item.productType().set(type);
                item.annualInterestRate().setValue(new BigDecimal("0.12"));
                policy.policyItems().add(item);
            }
        }

        policy.node().set(buildingDataModel.getBuilding());

        Persistence.service().persist(policy);
    }

    DepositPolicy getPolicy() {
        return policy;
    }
}
