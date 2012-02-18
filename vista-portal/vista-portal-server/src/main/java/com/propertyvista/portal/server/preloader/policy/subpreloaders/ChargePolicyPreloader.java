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
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.policy.policies.ChargePolicy;
import com.propertyvista.domain.policy.policies.domain.ChargePolicyItem;
import com.propertyvista.portal.server.preloader.policy.util.AbstractPolicyPreloader;

public class ChargePolicyPreloader extends AbstractPolicyPreloader<ChargePolicy> {

    public ChargePolicyPreloader() {
        super(ChargePolicy.class);
    }

    private final static I18n i18n = I18n.get(ChargePolicyPreloader.class);

    @Override
    protected ChargePolicy createPolicy(StringBuilder log) {
        ChargePolicy policy = EntityFactory.create(ChargePolicy.class);

        ChargePolicyItem item = EntityFactory.create(ChargePolicyItem.class);
        item.productItemType().type().setValue(ProductItemType.Type.service);
        item.productItemType().name().setValue(i18n.tr("Service Item Tyoe 1"));
        item.productItemType().serviceType().setValue(Service.Type.garage);

        EntityQueryCriteria<ChargeCode> criteria = EntityQueryCriteria.create(ChargeCode.class);
        //       Ê Ê Ê Ê Ê Ê//criteria.add(PropertyCriterion.eq(criteria.proto().name(), testId));

        List<ChargeCode> chargeCodes = Persistence.service().query(criteria);
        if (chargeCodes.size() > 0) {
            ChargeCode cc = chargeCodes.get(0);
            item.productItemType().chargeCode().taxes().setValue(cc.taxes().getValue());
            item.productItemType().chargeCode().name().setValue(cc.name().getValue());
            item.productItemType().chargeCode().glCode().glId().setValue(cc.glCode().glId().getValue());
            item.productItemType().chargeCode().glCode().description().setValue(cc.glCode().description().getValue());
            item.productItemType().chargeCode().glCode().glCodeCategory().glCategoryId().setValue(cc.glCode().glCodeCategory().glCategoryId().getValue());

            policy.chargePolicyItems().add(item);

            Persistence.service().persist(policy);
        } else
            throw new Error("unable to persist ChargePolicy");

        return policy;
    }
}
