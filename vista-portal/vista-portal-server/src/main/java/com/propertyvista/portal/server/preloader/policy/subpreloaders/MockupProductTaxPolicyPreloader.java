/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader.policy.subpreloaders;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.tax.Tax;
import com.propertyvista.domain.policy.policies.ProductTaxPolicy;
import com.propertyvista.domain.policy.policies.domain.ProductTaxPolicyItem;
import com.propertyvista.generator.util.RandomUtil;
import com.propertyvista.portal.server.preloader.policy.util.AbstractPolicyPreloader;

public class MockupProductTaxPolicyPreloader extends AbstractPolicyPreloader<ProductTaxPolicy> {

    private final static I18n i18n = I18n.get(MockupProductTaxPolicyPreloader.class);

    public MockupProductTaxPolicyPreloader() {
        super(ProductTaxPolicy.class);
    }

    @Override
    protected ProductTaxPolicy createPolicy(StringBuilder log) {
        ProductTaxPolicy policy = EntityFactory.create(ProductTaxPolicy.class);

        EntityQueryCriteria<ARCode> pitc = EntityQueryCriteria.create(ARCode.class);
        for (ARCode pit : Persistence.service().query(pitc)) {
            if (RandomUtil.randomBoolean()) { // do not process all items...
                ProductTaxPolicyItem item = EntityFactory.create(ProductTaxPolicyItem.class);
                item.productCode().set(pit);

                EntityQueryCriteria<Tax> criteria = EntityQueryCriteria.create(Tax.class);
                item.taxes().add(RandomUtil.random(Persistence.service().query(criteria)));
                policy.policyItems().add(item);
            }
        }
        log.append(policy.getStringView());

        return policy;
    }
}
