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

import java.math.BigDecimal;
import java.util.List;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductItemType;
import com.propertyvista.domain.financial.offering.ProductItemType.Type;
import com.propertyvista.domain.policy.policies.DepositPolicy;
import com.propertyvista.domain.policy.policies.domain.DepositPolicyItem;
import com.propertyvista.domain.policy.policies.domain.DepositPolicyItem.ApplyToRepayAt;
import com.propertyvista.domain.policy.policies.domain.DepositPolicyItem.RepaymentMode;
import com.propertyvista.domain.policy.policies.domain.DepositPolicyItem.ValueType;
import com.propertyvista.portal.server.preloader.policy.util.AbstractPolicyPreloader;

public class DepositPolicyPreloader extends AbstractPolicyPreloader<DepositPolicy> {

    private final static I18n i18n = I18n.get(DepositPolicyPreloader.class);

    public DepositPolicyPreloader() {
        super(DepositPolicy.class);
    }

    @Override
    protected DepositPolicy createPolicy(StringBuilder log) {
        DepositPolicy policy = EntityFactory.create(DepositPolicy.class);
        policy.prorate().setValue(true);

        DepositPolicyItem item = EntityFactory.create(DepositPolicyItem.class);
        item.description().setValue(i18n.tr("Security Depisit"));
        item.applyToRepayAt().setValue(ApplyToRepayAt.leaseEnd);
        item.depositValue().setValue(new BigDecimal(500.00));
        item.repaymentMode().setValue(RepaymentMode.value);
        item.valueType().setValue(ValueType.absoluteAmount);

        policy.policyItems().add(item);

        item = EntityFactory.create(DepositPolicyItem.class);

        EntityQueryCriteria<ProductItemType> pitc = EntityQueryCriteria.create(ProductItemType.class);
        List<ProductItemType> list = Persistence.service().query(pitc);

        for (ProductItemType pit : list) {
            if (pit.type().getValue() == Type.feature && pit.featureType().getValue() == Feature.Type.parking) { // do not process all items...
                item = EntityFactory.create(DepositPolicyItem.class);

                item.description().setValue(i18n.tr("First Month Parking"));
                item.applyToRepayAt().setValue(ApplyToRepayAt.firstMonth);
                item.depositValue().setValue(new BigDecimal(100.00));
                item.repaymentMode().setValue(RepaymentMode.serviceFeature);
                item.valueType().setValue(ValueType.percentage);
                item.appliedTo().set(pit);

                policy.policyItems().add(item);

                break;
            }
        }

        log.append(policy.getStringView());

        return policy;
    }
}
