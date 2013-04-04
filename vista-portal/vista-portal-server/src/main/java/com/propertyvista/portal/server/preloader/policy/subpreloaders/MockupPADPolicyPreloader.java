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

import java.util.List;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.policy.policies.PADPolicy;
import com.propertyvista.domain.policy.policies.PADPolicy.OwingBalanceType;
import com.propertyvista.domain.policy.policies.PADPolicy.PADChargeType;
import com.propertyvista.domain.policy.policies.PADPolicyItem;
import com.propertyvista.portal.server.preloader.policy.util.AbstractPolicyPreloader;

public class MockupPADPolicyPreloader extends AbstractPolicyPreloader<PADPolicy> {

    public MockupPADPolicyPreloader() {
        super(PADPolicy.class);
    }

    @Override
    protected PADPolicy createPolicy(StringBuilder log) {
        PADPolicy policy = EntityFactory.create(PADPolicy.class);
        policy.chargeType().setValue(PADChargeType.OwingBalance);
        for (ARCode code : getARCodes(ARCode.Type.Residential)) {
            PADPolicyItem item = EntityFactory.create(PADPolicyItem.class);
            item.debitType().set(code);
            item.owingBalanceType().setValue(OwingBalanceType.LastBill);
            policy.debitBalanceTypes().add(item);
        }
        for (ARCode code : getARCodes(ARCode.Type.Parking)) {
            PADPolicyItem item = EntityFactory.create(PADPolicyItem.class);
            item.debitType().set(code);
            item.owingBalanceType().setValue(OwingBalanceType.LastBill);
            policy.debitBalanceTypes().add(item);
        }
        for (ARCode code : getARCodes(ARCode.Type.Locker)) {
            PADPolicyItem item = EntityFactory.create(PADPolicyItem.class);
            item.debitType().set(code);
            item.owingBalanceType().setValue(OwingBalanceType.LastBill);
            policy.debitBalanceTypes().add(item);
        }

        log.append(policy.getStringView());
        return policy;
    }

    private List<ARCode> getARCodes(ARCode.Type type) {
        EntityQueryCriteria<ARCode> criteria = EntityQueryCriteria.create(ARCode.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().type(), type));
        return Persistence.service().query(criteria);
    }

}
