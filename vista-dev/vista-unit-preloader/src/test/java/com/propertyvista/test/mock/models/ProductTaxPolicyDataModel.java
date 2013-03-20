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

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.offering.ProductItemType;
import com.propertyvista.domain.policy.policies.ProductTaxPolicy;
import com.propertyvista.domain.policy.policies.domain.ProductTaxPolicyItem;
import com.propertyvista.test.mock.MockDataModel;

public class ProductTaxPolicyDataModel extends MockDataModel {

    private ProductItemTypesDataModel productItemTypesDataModel;

    private TaxesDataModel taxesDataModel;

    private BuildingDataModel buildingDataModel;

    private ProductTaxPolicy policy;

    public ProductTaxPolicyDataModel() {
    }

    @Override
    protected void generate() {
        productItemTypesDataModel = getDataModel(ProductItemTypesDataModel.class);
        taxesDataModel = getDataModel(TaxesDataModel.class);
        buildingDataModel = getDataModel(BuildingDataModel.class);

        policy = EntityFactory.create(ProductTaxPolicy.class);

        for (ProductItemType type : productItemTypesDataModel.getProductItemTypes()) {
            ProductTaxPolicyItem item = EntityFactory.create(ProductTaxPolicyItem.class);
            item.productItemType().set(type);
            item.taxes().add(taxesDataModel.getTaxes().get(0));
            policy.policyItems().add(item);
        }

        policy.node().set(buildingDataModel.getBuilding());

        Persistence.service().persist(policy);
    }

    ProductTaxPolicy getPolicy() {
        return policy;
    }
}
