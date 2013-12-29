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

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.policy.policies.ProductTaxPolicy;
import com.propertyvista.domain.policy.policies.domain.ProductTaxPolicyItem;
import com.propertyvista.test.mock.MockDataModel;

public class ProductTaxPolicyDataModel extends MockDataModel<ProductTaxPolicy> {

    public ProductTaxPolicyDataModel() {
    }

    @Override
    protected void generate() {
        ProductTaxPolicy policy = EntityFactory.create(ProductTaxPolicy.class);

        for (ARCode code : getDataModel(ARCodeDataModel.class).getAllItems()) {
            ProductTaxPolicyItem item = EntityFactory.create(ProductTaxPolicyItem.class);
            item.productCode().set(code);
            item.taxes().add(getDataModel(TaxesDataModel.class).getAllItems().get(0));
            policy.policyItems().add(item);
        }

        policy.node().set(getDataModel(PmcDataModel.class).getOrgNode());

        Persistence.service().persist(policy);
        addItem(policy);
    }

}
