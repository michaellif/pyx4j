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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.OrCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.biz.financial.MoneyUtils;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.billing.BillingUtils;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.InvoiceDeposit;
import com.propertyvista.domain.financial.offering.ProductItemType;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.policy.policies.DepositPolicy;
import com.propertyvista.domain.policy.policies.domain.DepositPolicyItem;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.Deposit.DepositStatus;
import com.propertyvista.domain.tenant.lease.Deposit.DepositType;
import com.propertyvista.domain.tenant.lease.DepositInterestAdjustment;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;

public class DepositFacadeImpl implements DepositFacade {
    private static final I18n i18n = I18n.get(DepositFacadeImpl.class);

    class DepositPolicyKey {
        private final DepositType depositType;

        private final ProductItemType productType;

        public DepositPolicyKey(DepositType depositType, ProductItemType productType) {
            this.depositType = depositType;
            this.productType = productType;
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof DepositPolicyKey) {
                DepositPolicyKey otherKey = (DepositPolicyKey) other;
                if (depositType != null && productType != null) {
                    return depositType.equals(otherKey.depositType) && productType.equals(otherKey.productType);
                }
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 1;
            if (depositType != null && productType != null) {
                hash = depositType.hashCode() ^ productType.hashCode();
            }
            return hash;
        }
    }

    @Override
    public Deposit createDeposit(DepositType depositType, BillableItem billableItem, PolicyNode node) {
        DepositPolicyItem policyItem = getPolicyItem(depositType, billableItem.item().type(), node);
        return (policyItem == null ? null : makeDeposit(policyItem, billableItem));
    }

    @Override
    public List<Deposit> createRequiredDeposits(BillableItem billableItem, PolicyNode node) {
        // get policy
        DepositPolicy depositPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(node, DepositPolicy.class);
        List<Deposit> deposits = new ArrayList<Deposit>();
        for (DepositPolicyItem policyItem : depositPolicy.policyItems()) {
            if (!policyItem.productType().equals(billableItem.item().type())) {
                continue;
            }
            deposits.add(makeDeposit(policyItem, billableItem));
        }

        return deposits;
    }

    @Override
    public void collectInterest(PolicyNode node) {
        Map<DepositPolicyKey, DepositPolicyItem> policyMatrix = getDepositPolicyMatrix(node);

        EntityQueryCriteria<Deposit> depositCriteria = EntityQueryCriteria.create(Deposit.class);
        depositCriteria.add(PropertyCriterion.eq(depositCriteria.proto().status(), DepositStatus.Billed));
        // TODO - do we want to check for last adjustment date?
        for (Deposit deposit : Persistence.service().query(depositCriteria)) {
            DepositPolicyItem policyItem = policyMatrix.get(new DepositPolicyKey(deposit.type().getValue(), deposit.billableItem().item().type()));
            if (policyItem == null) {
                throw new UserRuntimeException(i18n.tr("Could not find Policy Item for deposit {0}", deposit.getStringView()));
            } else {
                DepositInterestAdjustment adjustment = EntityFactory.create(DepositInterestAdjustment.class);
                adjustment.date().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
                adjustment.interestRate().set(policyItem.annualInterestRate());
                adjustment.amount().setValue(
                        MoneyUtils.round(deposit.currentAmount().getValue().multiply(policyItem.annualInterestRate().getValue().divide(new BigDecimal(12)))));

                deposit.interestAdjustments().add(adjustment);
                deposit.currentAmount().setValue(deposit.currentAmount().getValue().add(adjustment.amount().getValue()));

                Persistence.service().persist(deposit);
            }
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
    public void issueDepositRefunds(PolicyNode node) {
        EntityQueryCriteria<Deposit> depositCriteria = EntityQueryCriteria.create(Deposit.class);
        depositCriteria.add(PropertyCriterion.eq(depositCriteria.proto().status(), DepositStatus.Billed));
        // MoveInDeposit - refund right away (first bill)
        OrCriterion orCrit = new OrCriterion();
        depositCriteria.or(PropertyCriterion.eq(depositCriteria.proto().type(), DepositType.MoveInDeposit), orCrit);

        // LastMonthDeposit - return one month prior the end of lease
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(Persistence.service().getTransactionSystemTime());
        calendar.add(Calendar.MONTH, -1);
        Date dueDate = calendar.getTime(); // roll back one month
        orCrit.left(PropertyCriterion.eq(depositCriteria.proto().type(), DepositType.LastMonthDeposit));
        orCrit.left(PropertyCriterion.le(depositCriteria.proto().billableItem().expirationDate(), dueDate));

        // SecurityDeposit - return after the product expiration - check policy for refund window
        orCrit.right(PropertyCriterion.eq(depositCriteria.proto().type(), DepositType.SecurityDeposit));
        orCrit.right(PropertyCriterion.le(depositCriteria.proto().billableItem().expirationDate(), Persistence.service().getTransactionSystemTime()));

        List<Deposit> depositsDue = Persistence.service().query(depositCriteria);
        if (depositsDue == null || depositsDue.size() == 0) {
            return;
        }

        ARFacade arFacade = ServerSideFactory.create(ARFacade.class);
        Map<DepositPolicyKey, DepositPolicyItem> policyMatrix = getDepositPolicyMatrix(node);
        for (Deposit deposit : depositsDue) {
            switch (deposit.type().getValue()) {
            case MoveInDeposit:
            case LastMonthDeposit:
                // refund now
                arFacade.postDepositRefund(deposit);
                break;
            case SecurityDeposit:
                DepositPolicyItem policyItem = policyMatrix.get(new DepositPolicyKey(deposit.type().getValue(), deposit.billableItem().item().type()));
                if (policyItem == null) {
                    throw new UserRuntimeException(i18n.tr("Could not find Policy Item for deposit {0}", deposit.getStringView()));
                } else {
                    // see if we are past the refund window
                    calendar.setTime(Persistence.service().getTransactionSystemTime());
                    calendar.add(Calendar.DAY_OF_MONTH, -policyItem.securityDepositRefundWindow().getValue());
                    dueDate = calendar.getTime();
                    if (!deposit.billableItem().expirationDate().getValue().after(dueDate)) {
                        arFacade.postDepositRefund(deposit);
                    }
                }
                break;
            default:
                throw new UserRuntimeException(i18n.tr("Unknown Deposit Type {0}", deposit.getStringView()));
            }
        }
    }

    @Override
    public void onValidateBill(Bill bill) {
        // update status of newly created deposits to Billed
        for (InvoiceDeposit invoiceDeposit : BillingUtils.getLineItemsForType(bill, InvoiceDeposit.class)) {
            if (DepositStatus.Created.equals(invoiceDeposit.deposit().status().getValue())) {
                invoiceDeposit.deposit().status().setValue(DepositStatus.Billed);
                Persistence.service().persist(invoiceDeposit.deposit());
            }
        }
    }

    private Map<DepositPolicyKey, DepositPolicyItem> getDepositPolicyMatrix(PolicyNode node) {
        DepositPolicy depositPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(node, DepositPolicy.class);
        Map<DepositPolicyKey, DepositPolicyItem> policyMatrix = new HashMap<DepositPolicyKey, DepositPolicyItem>();
        for (DepositPolicyItem policyItem : depositPolicy.policyItems()) {
            policyMatrix.put(new DepositPolicyKey(policyItem.depositType().getValue(), policyItem.productType()), policyItem);
        }
        return policyMatrix;
    }

    private Deposit makeDeposit(DepositPolicyItem policyItem, BillableItem billableItem) {
        Deposit deposit = EntityFactory.create(Deposit.class);
        deposit.type().set(policyItem.depositType());
        deposit.initialAmount().set(policyItem.value());
        deposit.currentAmount().set(deposit.initialAmount());
        deposit.valueType().set(policyItem.valueType());
        deposit.description().set(policyItem.description());
        deposit.status().setValue(DepositStatus.Created);
        deposit.depositDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
        deposit.billableItem().set(billableItem);
        return deposit;
    }

    private DepositPolicyItem getPolicyItem(DepositType depositType, ProductItemType productType, PolicyNode node) {
        DepositPolicyItem policyItem = null;
        DepositPolicy depositPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(node, DepositPolicy.class);
        for (DepositPolicyItem pi : depositPolicy.policyItems()) {
            if (pi.depositType().getValue().equals(depositType) && pi.productType().equals(productType)) {
                policyItem = pi;
                break;
            }
        }
        return policyItem;
    }
}
