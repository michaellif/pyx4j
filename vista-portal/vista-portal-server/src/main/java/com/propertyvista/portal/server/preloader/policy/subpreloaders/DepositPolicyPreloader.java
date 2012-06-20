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

import com.propertvista.generator.util.RandomUtil;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.OrCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.financial.offering.FeatureItemType;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.domain.policy.policies.DepositPolicy;
import com.propertyvista.domain.policy.policies.domain.DepositPolicyItem;
import com.propertyvista.domain.tenant.lease.Deposit.DepositType;
import com.propertyvista.domain.tenant.lease.Deposit.ValueType;
import com.propertyvista.portal.server.preloader.policy.util.AbstractPolicyPreloader;

public class DepositPolicyPreloader extends AbstractPolicyPreloader<DepositPolicy> {

    private final static I18n i18n = I18n.get(DepositPolicyPreloader.class);

    public DepositPolicyPreloader() {
        super(DepositPolicy.class);
    }

    @Override
    protected DepositPolicy createPolicy(StringBuilder log) {
        DepositPolicy policy = EntityFactory.create(DepositPolicy.class);

        EntityQueryCriteria<ServiceItemType> srvType = EntityQueryCriteria.create(ServiceItemType.class);
        srvType.add(new OrCriterion(PropertyCriterion.eq(srvType.proto().serviceType(), Service.ServiceType.commercialUnit), new OrCriterion(PropertyCriterion
                .eq(srvType.proto().serviceType(), Service.ServiceType.residentialUnit), PropertyCriterion.eq(srvType.proto().serviceType(),
                Service.ServiceType.residentialShortTermUnit))));
        List<ServiceItemType> services = Persistence.service().query(srvType);
        for (ServiceItemType pit : services) {

            DepositPolicyItem item = EntityFactory.create(DepositPolicyItem.class);
            item.depositType().setValue(DepositType.SecurityDeposit);
            item.description().setValue(i18n.tr("Security Deposit"));
            item.value().setValue(new BigDecimal(RandomUtil.randomDouble(500.0)));
            item.valueType().setValue(ValueType.amount);
            item.productType().set(pit);
            item.annualInterestRate().setValue(new BigDecimal(0.01 + RandomUtil.randomDouble(0.03)));

            policy.policyItems().add(item);
        }

        EntityQueryCriteria<FeatureItemType> pitc = EntityQueryCriteria.create(FeatureItemType.class);
        List<FeatureItemType> features = Persistence.service().query(pitc);
        for (FeatureItemType pit : features) {
            if (RandomUtil.randomBoolean()) {
                switch (pit.featureType().getValue()) {
                case parking:
                    DepositPolicyItem item = EntityFactory.create(DepositPolicyItem.class);
                    item.depositType().setValue(DepositType.SecurityDeposit);
                    item.description().setValue(i18n.tr("First Month Parking"));
                    item.value().setValue(new BigDecimal(RandomUtil.randomDouble(1.0)));
                    item.valueType().setValue(ValueType.percentage);
                    item.productType().set(pit);
                    item.annualInterestRate().setValue(new BigDecimal(0.01 + RandomUtil.randomDouble(0.03)));

                    policy.policyItems().add(item);
                    break;
                case locker:
                    item = EntityFactory.create(DepositPolicyItem.class);
                    item.depositType().setValue(DepositType.SecurityDeposit);
                    item.description().setValue(i18n.tr("Last Month Locker"));
                    item.value().setValue(new BigDecimal(RandomUtil.randomDouble(1.0)));
                    item.valueType().setValue(ValueType.percentage);
                    item.productType().set(pit);
                    item.annualInterestRate().setValue(new BigDecimal(0.01 + RandomUtil.randomDouble(0.03)));

                    policy.policyItems().add(item);
                    break;
                }
            }
        }

        log.append(policy.getStringView());

        return policy;
    }
}
