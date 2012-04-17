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
package com.propertyvista.server.financial.preload;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.offering.ProductItemType;
import com.propertyvista.domain.policy.policies.ProductTaxPolicy;
import com.propertyvista.domain.policy.policies.domain.ProductTaxPolicyItem;

public class ProductTaxPolicyDataModel {

    private final ProductItemTypesDataModel productItemTypesDataModel;

    private final TaxesDataModel taxesDataModel;

    private final BuildingDataModel buildingDataModel;

    private ProductTaxPolicy policy;

    public ProductTaxPolicyDataModel(ProductItemTypesDataModel productItemTypesDataModel, TaxesDataModel taxesDataModel, BuildingDataModel buildingDataModel) {
        this.productItemTypesDataModel = productItemTypesDataModel;
        this.taxesDataModel = taxesDataModel;
        this.buildingDataModel = buildingDataModel;
        generate(true);
    }

    public void generate(boolean persist) {
        policy = EntityFactory.create(ProductTaxPolicy.class);

        for (ProductItemType type : productItemTypesDataModel.getProductItemTypes()) {
            ProductTaxPolicyItem item = EntityFactory.create(ProductTaxPolicyItem.class);
            item.productItemType().set(type);
            item.taxes().add(taxesDataModel.getTaxes().get(0));
            policy.policyItems().add(item);
        }

        policy.node().set(buildingDataModel.getBuilding());

        if (persist) {
            Persistence.service().persist(policy);
        }
    }

    ProductTaxPolicy getPolicy() {
        return policy;
    }
}
