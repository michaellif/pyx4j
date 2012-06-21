/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 13, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.biz.financial.deposit;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.biz.financial.SysDateManager;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.financial.offering.ProductItemType;
import com.propertyvista.domain.policy.policies.DepositPolicy;
import com.propertyvista.domain.policy.policies.domain.DepositPolicyItem;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.Deposit.DepositStatus;
import com.propertyvista.domain.tenant.lease.Deposit.DepositType;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;

public class DepositFacadeImpl implements DepositFacade {
    private static final I18n i18n = I18n.get(DepositFacadeImpl.class);

    @Override
    public Deposit createDeposit(DepositType depositType, ProductItemType productType, Lease lease) {
        DepositPolicyItem policyItem = getPolicyItem(depositType, productType, lease);
        return (policyItem == null ? null : makeDeposit(policyItem));
    }

    @Override
    public List<Deposit> createRequiredDeposits(ProductItemType productType, Lease lease) {
        // get policy
        DepositPolicy depositPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(lease.unit().building(), DepositPolicy.class);
        List<Deposit> deposits = new ArrayList<Deposit>();
        for (DepositPolicyItem policyItem : depositPolicy.policyItems()) {
            if (!policyItem.productType().equals(productType)) {
                continue;
            }
            deposits.add(makeDeposit(policyItem));
        }

        return deposits;
    }

    @Override
    public void collectInterest(Deposit deposit, Lease lease) {
        DepositPolicyItem policyItem = getPolicyItem(deposit.type().getValue(), deposit.billableItem().item().type(), lease);
        if (policyItem == null) {
            throw new UserRuntimeException(i18n.tr("Could not find Policy Item for deposit {0}", deposit.getStringView()));
        }
    }

    @Override
    public boolean coverAccountExpense(Deposit deposit, LeaseAdjustment expense) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean coverProductExpense(Deposit deposit, BillableItem expense) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onTargetProductChange(Deposit deposit) {
        // TODO Auto-generated method stub
    }

    @Override
    public List<Deposit> getReturnList() {
        EntityQueryCriteria<Deposit> depositCriteria = EntityQueryCriteria.create(Deposit.class);
        depositCriteria.add(PropertyCriterion.eq(depositCriteria.proto().status(), DepositStatus.Billed));
        depositCriteria.add(PropertyCriterion.le(depositCriteria.proto().billableItem().expirationDate(), SysDateManager.getSysDate()));
        return Persistence.service().query(depositCriteria);
    }

    private Deposit makeDeposit(DepositPolicyItem policyItem) {
        Deposit deposit = EntityFactory.create(Deposit.class);
        deposit.type().set(policyItem.depositType());
        deposit.initialAmount().set(policyItem.value());
        deposit.valueType().set(policyItem.valueType());
        deposit.description().set(policyItem.description());
        deposit.status().setValue(DepositStatus.Created);
        deposit.depositDate().setValue(new LogicalDate(SysDateManager.getSysDate()));
        return deposit;
    }

    private DepositPolicyItem getPolicyItem(DepositType depositType, ProductItemType productType, Lease lease) {
        DepositPolicyItem policyItem = null;
        DepositPolicy depositPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(lease.unit().building(), DepositPolicy.class);
        for (DepositPolicyItem pi : depositPolicy.policyItems()) {
            if (pi.depositType().getValue().equals(depositType) && pi.productType().equals(productType)) {
                policyItem = pi;
                break;
            }
        }
        return policyItem;
    }
}
