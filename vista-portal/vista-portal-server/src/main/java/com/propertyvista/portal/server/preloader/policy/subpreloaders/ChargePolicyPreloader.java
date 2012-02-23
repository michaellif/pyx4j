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

import java.util.List;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.financial.offering.ChargeCode;
import com.propertyvista.domain.financial.offering.ProductItemType;
import com.propertyvista.domain.policy.policies.ProductTaxPolicy;
import com.propertyvista.domain.policy.policies.domain.ProductTaxPolicyItem;
import com.propertyvista.portal.server.preloader.policy.util.AbstractPolicyPreloader;

public class ChargePolicyPreloader extends AbstractPolicyPreloader<ProductTaxPolicy> {

    public ChargePolicyPreloader() {
        super(ProductTaxPolicy.class);
    }

    private final static I18n i18n = I18n.get(ChargePolicyPreloader.class);

    @Override
    protected ProductTaxPolicy createPolicy(StringBuilder log) {
        ProductTaxPolicy policy = EntityFactory.create(ProductTaxPolicy.class);

        ProductTaxPolicyItem item = EntityFactory.create(ProductTaxPolicyItem.class);

        EntityQueryCriteria<ProductItemType> pitc = EntityQueryCriteria.create(ProductItemType.class);
        List<ProductItemType> pitlist = Persistence.service().query(pitc);

        if (pitlist.size() > 0) {
            item.productItemType().set(pitlist.get(0));

            EntityQueryCriteria<ChargeCode> criteria = EntityQueryCriteria.create(ChargeCode.class);

            List<ChargeCode> chargeCodes = Persistence.service().query(criteria);
            if (chargeCodes.size() > 0) {
                ChargeCode cc = chargeCodes.get(0);

                //TODO set taxes
                //  item.productItemType().chargeCode().set(cc);
                item.chargeCode().set(cc);

                policy.policyItems().add(item);

                Persistence.service().persist(policy);
            } else
                throw new Error("unable to persist ChargePolicy: no charge codes");
        } else
            throw new Error("unable to persist ChargePolicy: no product item types");

        return policy;
    }
}
