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
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.billing.BillingUtils;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.InvoiceDeposit;
import com.propertyvista.domain.financial.offering.ProductDeposit;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.policy.policies.DepositPolicy;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.Deposit.DepositType;
import com.propertyvista.domain.tenant.lease.DepositInterestAdjustment;
import com.propertyvista.domain.tenant.lease.DepositLifecycle;
import com.propertyvista.domain.tenant.lease.DepositLifecycle.DepositStatus;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.domain.util.DomainUtil;

public class DepositFacadeImpl implements DepositFacade {
    private static final I18n i18n = I18n.get(DepositFacadeImpl.class);

    @Override
    public Deposit createDeposit(DepositType depositType, BillableItem billableItem) {
        ProductDeposit productDeposit = getProductDepositByType(depositType, billableItem.item());
        return (productDeposit == null ? null : makeDeposit(productDeposit, billableItem));
    }

    @Override
    public List<Deposit> createRequiredDeposits(BillableItem billableItem) {
        List<Deposit> deposits = new ArrayList<Deposit>();
        for (DepositType type : DepositType.values()) {
            ProductDeposit productDeposit = getProductDepositByType(type, billableItem.item());
            if (productDeposit.enabled().isBooleanTrue()) {
                deposits.add(makeDeposit(productDeposit, billableItem));
            }
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
    public Map<Deposit, ProductTerm> getCurrentDeposits(Lease lease) {
        Map<Deposit, ProductTerm> deposits = new HashMap<Deposit, ProductTerm>();

        List<Lease> leases = null;
        if (lease == null) {
            // find active leases
            EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
            criteria.add(PropertyCriterion.in(criteria.proto().status(), Lease.Status.current()));
            leases = Persistence.service().query(criteria);
        } else {
            leases = Arrays.asList(lease);
        }
        List<Deposit> addList;
        for (Lease l : leases) {
            addList = l.currentTerm().version().leaseProducts().serviceItem().deposits();
            if (addList != null) {
                ProductTerm term = new ProductTerm(l.currentTerm().version().leaseProducts().serviceItem(), l);
                for (Deposit deposit : addList) {
                    deposits.put(deposit, term);
                }
            }
            for (BillableItem feature : l.currentTerm().version().leaseProducts().featureItems()) {
                addList = feature.deposits();
                if (addList != null) {
                    ProductTerm term = new ProductTerm(feature, l);
                    for (Deposit deposit : addList) {
                        deposits.put(deposit, term);
                    }
                }
            }
        }
        return deposits;
    }

    @Override
    public void collectInterest(Lease lease) {
        DepositPolicy depositPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(lease.unit().building(), DepositPolicy.class);

        // TODO - do we want to check for last adjustment date?
        Map<Deposit, ProductTerm> deposits = getCurrentDeposits(lease);
        for (Deposit deposit : deposits.keySet()) {
            if (!DepositStatus.Paid.equals(deposit.lifecycle().status().getValue())) {
                continue;
            }

            if (depositPolicy == null) {
                throw new UserRuntimeException(i18n.tr("Could not find Policy Item for deposit {0}", deposit.getStringView()));
            } else {
                DepositInterestAdjustment adjustment = EntityFactory.create(DepositInterestAdjustment.class);
                adjustment.date().setValue(SystemDateManager.getLogicalDate());
                adjustment.interestRate().set(depositPolicy.annualInterestRate());
                adjustment.amount().setValue(
                        DomainUtil.roundMoney(deposit.lifecycle().currentAmount().getValue()
                                .multiply(depositPolicy.annualInterestRate().getValue().divide(new BigDecimal(12)))));

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
    public void issueDepositRefunds(Lease lease) {
        DepositPolicy depositPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(lease.unit().building(), DepositPolicy.class);

        ARFacade arFacade = ServerSideFactory.create(ARFacade.class);

        Date now = SystemDateManager.getDate();

        Map<Deposit, ProductTerm> deposits = getCurrentDeposits(lease);
        for (Deposit deposit : deposits.keySet()) {
            if (!DepositStatus.Paid.equals(deposit.lifecycle().status().getValue())) {
                continue;
            }

            ProductTerm term = deposits.get(deposit);
            switch (deposit.type().getValue()) {
            case MoveInDeposit:
                // MoveInDeposit - refund on first bill
                arFacade.postDepositRefund(deposit);
                break;
            case LastMonthDeposit:
                // LastMonthDeposit - refund must appear on the last month bill so normally deposits will be issued by
                // the BillingDepositProcessor. However, if for some reason that did not happen we will pick up those
                // deposits here to ensure they get into the final bill.
                if (!lease.leaseTo().getValue().after(now) || (term.to != null && !term.to.after(now))) {
                    arFacade.postDepositRefund(deposit);
                }
                break;
            case SecurityDeposit:
                // SecurityDeposit - return after product expiration - check policy for refund window
                if (term.to == null) {
                    break;
                }
                if (depositPolicy == null) {
                    throw new UserRuntimeException(i18n.tr("Could not find Policy Item for deposit {0}", deposit.getStringView()));
                } else {
                    Calendar calendar = new GregorianCalendar();
                    // see if we are past the refund window
                    calendar.setTime(SystemDateManager.getDate());
                    if (!depositPolicy.securityDepositRefundWindow().isNull()) {
                        calendar.add(Calendar.DAY_OF_MONTH, -depositPolicy.securityDepositRefundWindow().getValue());
                    }
                    Date dueDate = calendar.getTime();
                    if (!term.to.after(dueDate) || !lease.leaseTo().getValue().after(dueDate)) {
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
                Persistence.service().persist(invoiceDeposit.deposit().lifecycle());
                Persistence.service().persist(invoiceDeposit.deposit());
            }
        }
    }

    private Deposit makeDeposit(ProductDeposit productDeposit, BillableItem billableItem) {
        if (!productDeposit.enabled().isBooleanTrue()) {
            return null;
        }

        Deposit deposit = EntityFactory.create(Deposit.class);

        deposit.chargeCode().set(productDeposit.chargeCode());
        deposit.type().set(productDeposit.depositType());
        BigDecimal depositValue = getDepositValue(productDeposit, billableItem.item());
        switch (productDeposit.valueType().getValue()) {
        case Monetary:
            deposit.amount().setValue(depositValue);
            break;
        case Percentage:
            deposit.amount().setValue(DomainUtil.roundMoney(depositValue.multiply(billableItem.agreedPrice().getValue())));
            break;
        default:
            throw new Error("Unsupported ValueType");
        }
        deposit.isProcessed().setValue(false);
        deposit.description().set(productDeposit.description());
        deposit.billableItem().set(billableItem);

        return deposit;
    }

    private ProductDeposit getProductDepositByType(DepositType depositType, ProductItem productItem) {
        ProductDeposit productDeposit = null;
        switch (depositType) {
        case LastMonthDeposit:
            productDeposit = productItem.product().depositLMR();
            break;
        case MoveInDeposit:
            productDeposit = productItem.product().depositMoveIn();
            break;
        case SecurityDeposit:
            productDeposit = productItem.product().depositSecurity();
            break;
        }
        return productDeposit;
    }

    private BigDecimal getDepositValue(ProductDeposit productDeposit, ProductItem productItem) {
        BigDecimal value = null;
        switch (productDeposit.depositType().getValue()) {
        case MoveInDeposit:
            value = productItem.depositMoveIn().getValue();
            break;
        case LastMonthDeposit:
            value = productItem.depositLMR().getValue();
            break;
        case SecurityDeposit:
            value = productItem.depositSecurity().getValue();
            break;
        }
        return value == null ? productDeposit.value().getValue() : value;
    }
}
