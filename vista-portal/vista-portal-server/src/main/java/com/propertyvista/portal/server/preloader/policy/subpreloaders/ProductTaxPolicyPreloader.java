/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 11, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader.policy.subpreloaders;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.domain.policy.policies.ProductTaxPolicy;
import com.propertyvista.portal.server.preloader.policy.util.AbstractPolicyPreloader;

public class ProductTaxPolicyPreloader extends AbstractPolicyPreloader<ProductTaxPolicy> {

    public ProductTaxPolicyPreloader() {
        super(ProductTaxPolicy.class);
    }

    @Override
    protected ProductTaxPolicy createPolicy(StringBuilder log) {
        ProductTaxPolicy policy = EntityFactory.create(ProductTaxPolicy.class);
        log.append(policy.getStringView());
        return policy;
    }

}
