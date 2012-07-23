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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.biz.financial.MoneyUtils;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.billing.BillingUtils;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.InvoiceDeposit;
import com.propertyvista.domain.financial.offering.ProductItemType;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.policy.policies.DepositPolicy;
import com.propertyvista.domain.policy.policies.domain.DepositPolicyItem;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.Deposit.DepositType;
import com.propertyvista.domain.tenant.lease.DepositInterestAdjustment;
import com.propertyvista.domain.tenant.lease.DepositLifecycle;
import com.propertyvista.domain.tenant.lease.DepositLifecycle.DepositStatus;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;

public class DepositFacadeImpl implements DepositFacade {
    private static final I18n i18n = I18n.get(DepositFacadeImpl.class);

    class DepositPolicyKey {
        private final DepositType depositType;

        private final String productType;

        public DepositPolicyKey(DepositType depositType, ProductItemType productType) {
            this.depositType = depositType;
            this.productType = productType.name().getValue();
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
        DepositPolicyItem policyItem = null;
        DepositPolicy depositPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(node, DepositPolicy.class);
        for (DepositPolicyItem pi : depositPolicy.policyItems()) {
            if (pi.depositType().getValue().equals(depositType) && pi.productType().equals(billableItem.item().type())) {
                policyItem = pi;
                break;
            }
        }
        return (policyItem == null ? null : makeDeposit(policyItem, billableItem));
    }

    @Override
    public List<Deposit> createRequiredDeposits(BillableItem billableItem, PolicyNode node) {
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
    public DepositLifecycle createDepositLifecycle(Deposit deposit, BillingAccount billingAccount) {
        DepositLifecycle depositLifecycle = EntityFactory.create(DepositLifecycle.class);

        depositLifecycle.currentAmount().setValue(deposit.amount().getValue());
        depositLifecycle.depositDate().set(deposit.billableItem().effectiveDate());

        depositLifecycle.billingAccount().set(billingAccount);
        depositLifecycle.status().setValue(DepositStatus.Created);

        deposit.lifecycle().set(depositLifecycle);

        return depositLifecycle;
    }

    @Override
    public Deposit getDeposit(DepositLifecycle depositLifecycle) {
        EntityQueryCriteria<Deposit> criteria = new EntityQueryCriteria<Deposit>(Deposit.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().lifecycle(), depositLifecycle));
        List<Deposit> deposits = Persistence.service().query(criteria);
        if (!deposits.isEmpty()) {
            Collections.sort(deposits, new Comparator<Deposit>() {
                @Override
                public int compare(Deposit o1, Deposit o2) {
                    return (int) (o1.getPrimaryKey().asLong() - o2.getPrimaryKey().asLong());
                }
            });
            return deposits.get(0); // get the very first version of the deposit 
        }
        throw new IllegalArgumentException();
    }

    @Override
    public List<Deposit> getCurrentDeposits(Lease lease) {
        List<Deposit> deposits = new ArrayList<Deposit>();

        List<Lease> leases = null;
        if (lease == null) {
            // find active leases
            EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
            criteria.add(PropertyCriterion.in(criteria.proto().version().status(), Lease.Status.current()));
            leases = Persistence.service().query(criteria);
        } else {
            leases = Arrays.asList(lease);
        }
        List<Deposit> addList;
        for (Lease l : leases) {
            addList = l.version().leaseProducts().serviceItem().deposits();
            if (addList != null) {
                deposits.addAll(addList);
            }
            for (BillableItem feature : l.version().leaseProducts().featureItems()) {
                addList = feature.deposits();
                if (addList != null) {
                    deposits.addAll(addList);
                }
            }
        }
        return deposits;
    }

    @Override
    public void collectInterest(PolicyNode node) {
        Map<DepositPolicyKey, DepositPolicyItem> policyMatrix = getDepositPolicyMatrix(node);

        // TODO - do we want to check for last adjustment date?
        for (Deposit deposit : getCurrentDeposits(null)) {
            if (!DepositStatus.Paid.equals(deposit.lifecycle().status().getValue())) {
                continue;
            }
            Persistence.service().retrieve(deposit.billableItem());
            DepositPolicyItem policyItem = policyMatrix.get(new DepositPolicyKey(deposit.type().getValue(), deposit.billableItem().item().type()));
            if (policyItem == null) {
                throw new UserRuntimeException(i18n.tr("Could not find Policy Item for deposit {0}", deposit.getStringView()));
            } else {
                DepositInterestAdjustment adjustment = EntityFactory.create(DepositInterestAdjustment.class);
                adjustment.date().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
                adjustment.interestRate().set(policyItem.annualInterestRate());
                adjustment.amount().setValue(
                        MoneyUtils.round(deposit.lifecycle().currentAmount().getValue()
                                .multiply(policyItem.annualInterestRate().getValue().divide(new BigDecimal(12)))));

                deposit.lifecycle().interestAdjustments().add(adjustment);
                deposit.lifecycle().currentAmount().setValue(deposit.lifecycle().currentAmount().getValue().add(adjustment.amount().getValue()));

                Persistence.service().persist(deposit.lifecycle());
            }
        }
    }

    @Override
    public boolean coverAccountExpense(DepositLifecycle deposit, LeaseAdjustment expense) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean coverProductExpense(DepositLifecycle deposit, BillableItem expense) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onTargetProductChange(DepositLifecycle deposit) {
        // TODO Auto-generated method stub
    }

    @Override
    public void issueDepositRefunds(PolicyNode node) {
        Map<DepositPolicyKey, DepositPolicyItem> policyMatrix = getDepositPolicyMatrix(node);

        ARFacade arFacade = ServerSideFactory.create(ARFacade.class);

        Date now = Persistence.service().getTransactionSystemTime();

        for (Deposit deposit : getCurrentDeposits(null)) {
            if (!DepositStatus.Paid.equals(deposit.lifecycle().status().getValue())) {
                continue;
            }

            switch (deposit.type().getValue()) {
            case MoveInDeposit:
                // MoveInDeposit - refund on first bill
                arFacade.postDepositRefund(deposit);
                break;
            case LastMonthDeposit:
                // LastMonthDeposit - refund must appear on the last month bill so normally deposits will be issued by
                // the BillingDepositProcessor. However, if for some reason that did not happen we will pick up those
                // deposits here to ensure they get into the final bill.
                Persistence.service().retrieve(deposit.billableItem());
                LogicalDate expirationDate = deposit.billableItem().expirationDate().getValue();
                if (expirationDate != null && expirationDate.before(now)) {
                    arFacade.postDepositRefund(deposit);
                }
                break;
            case SecurityDeposit:
                // SecurityDeposit - return after product expiration - check policy for refund window
                Persistence.service().retrieve(deposit.billableItem());
                expirationDate = deposit.billableItem().expirationDate().getValue();
                if (expirationDate == null) {
                    break;
                }
                DepositPolicyItem policyItem = policyMatrix.get(new DepositPolicyKey(deposit.type().getValue(), deposit.billableItem().item().type()));
                if (policyItem == null) {
                    throw new UserRuntimeException(i18n.tr("Could not find Policy Item for deposit {0}", deposit.getStringView()));
                } else {
                    Calendar calendar = new GregorianCalendar();
                    // see if we are past the refund window
                    calendar.setTime(Persistence.service().getTransactionSystemTime());
                    if (!policyItem.securityDepositRefundWindow().isNull()) {
                        calendar.add(Calendar.DAY_OF_MONTH, -policyItem.securityDepositRefundWindow().getValue());
                    }
                    Date dueDate = calendar.getTime();
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
    public void onConfirmedBill(Bill bill) {
        // update status of newly created deposits to Billed
        for (InvoiceDeposit invoiceDeposit : BillingUtils.getLineItemsForType(bill, InvoiceDeposit.class)) {
            if (!invoiceDeposit.deposit().isProcessed().isBooleanTrue()) {
                invoiceDeposit.deposit().isProcessed().setValue(true);
                invoiceDeposit.deposit().lifecycle().status().setValue(DepositStatus.Processed);
                // TODO - remove next line when Paid status update implemented....
                invoiceDeposit.deposit().lifecycle().status().setValue(DepositStatus.Paid);
                Persistence.service().merge(invoiceDeposit.deposit().lifecycle());
                Persistence.service().merge(invoiceDeposit.deposit());
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
        switch (policyItem.valueType().getValue()) {
        case Monetary:
            deposit.amount().setValue(policyItem.value().getValue());
            break;
        case Percentage:
            deposit.amount().setValue(MoneyUtils.round(policyItem.value().getValue().multiply(billableItem.agreedPrice().getValue())));
            break;
        default:
            throw new Error("Unsupported ValueType");
        }
        deposit.isProcessed().setValue(false);
        deposit.description().set(policyItem.description());
        deposit.billableItem().set(billableItem);

        return deposit;
    }
}
