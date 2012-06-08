/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Jan 30, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.billing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.InvoiceAccountCharge;
import com.propertyvista.domain.financial.billing.InvoiceAccountCredit;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.billing.InvoiceDeposit;
import com.propertyvista.domain.financial.billing.InvoiceDepositRefund;
import com.propertyvista.domain.financial.billing.InvoiceLatePaymentFee;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.financial.billing.InvoiceNSF;
import com.propertyvista.domain.financial.billing.InvoicePayment;
import com.propertyvista.domain.financial.billing.InvoicePaymentBackOut;
import com.propertyvista.domain.financial.billing.InvoiceProductCharge;
import com.propertyvista.domain.financial.billing.InvoiceProductCharge.ProductType;
import com.propertyvista.domain.financial.billing.InvoiceProductCredit;
import com.propertyvista.domain.financial.billing.InvoiceWithdrawal;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Product;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.dto.BillDTO;

public class BillingUtils {

    public static boolean isService(Product.ProductV product) {
        return product.cast() instanceof Service.ServiceV;
    }

    public static boolean isFeature(Product.ProductV product) {
        return product.cast() instanceof Feature.FeatureV;
    }

    public static boolean isRecurringFeature(Product.ProductV product) {
        return isFeature(product) && ((Feature.FeatureV) product.cast()).recurring().getValue();
    }

    public static boolean isOneTimeFeature(Product.ProductV product) {
        return isFeature(product) && !((Feature.FeatureV) product.cast()).recurring().getValue();
    }

    public static List<InvoiceLineItem> getUnclaimedLineItems(BillingAccount billingAccount) {
        EntityQueryCriteria<InvoiceLineItem> criteria = EntityQueryCriteria.create(InvoiceLineItem.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().claimed(), false));
        criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), billingAccount));
        return Persistence.service().query(criteria);
    }

    public static <E extends InvoiceLineItem> List<E> getLineItemsForType(Bill bill, Class<E> type) {
        return getLineItemsForType(bill.lineItems(), type);
    }

    public static BigDecimal calculateTotal(InvoiceLineItem lineItem) {
        BigDecimal tax = BigDecimal.ZERO;
        if (lineItem.isInstanceOf(InvoiceDebit.class)) {
            tax = lineItem.<InvoiceDebit> cast().taxTotal().getValue();
        }
        return lineItem.amount().getValue().add(tax);
    }

    @SuppressWarnings("unchecked")
    public static <E extends InvoiceLineItem> List<E> getLineItemsForType(Collection<InvoiceLineItem> lineItems, Class<E> type) {
        List<E> items = new ArrayList<E>();
        for (InvoiceLineItem lineItem : lineItems) {
            if (lineItem.isInstanceOf(type)) {
                items.add((E) lineItem.cast());
            }
        }
        return items;
    }

    public static BillDTO createBillDto(Bill bill) {
        Persistence.service().retrieve(bill.lineItems());
        Persistence.service().retrieve(bill.billingAccount());
        BillDTO billDTO = new BillConverter().createDTO(bill);

        enhanceBillDto(bill, billDTO);
        return billDTO;
    }

    public static void enhanceBillDto(Bill bill, BillDTO dto) {
        // set total values
        dto.serviceChargeLineItems().total().set(bill.serviceCharge());
        dto.recurringFeatureChargeLineItems().total().set(bill.recurringFeatureCharges());
        dto.onetimeFeatureChargeLineItems().total().set(bill.oneTimeFeatureCharges());
        dto.depositLineItems().total().set(bill.depositAmount());
        dto.depositRefundLineItems().total().set(bill.depositRefundAmount());
        dto.immediateAccountAdjustmentLineItems().total().set(bill.immediateAccountAdjustments());
        dto.pendingAccountAdjustmentLineItems().total().set(bill.pendingAccountAdjustments());
        dto.nsfChargeLineItems().total().set(bill.nsfCharges());
        dto.pendingAccountAdjustmentLineItems().total().set(bill.pendingAccountAdjustments());
        dto.latePaymentFeeLineItems().total().set(bill.latePaymentFees());
        dto.withdrawalLineItems().total().set(bill.withdrawalAmount());
        dto.rejectedPaymentLineItems().total().set(bill.paymentRejectedAmount());
        dto.paymentLineItems().total().set(bill.paymentReceivedAmount());
        // set detail lists
        for (InvoiceLineItem lineItem : bill.lineItems()) {
            // *** Current Bill list values ***
            if (lineItem instanceof InvoiceProductCharge) {
                InvoiceProductCharge charge = (InvoiceProductCharge) lineItem;
                ProductType prodType = charge.productType().getValue();
                if (ProductType.service.equals(prodType)) {
                    dto.serviceChargeLineItems().lineItems().add(charge);
                } else if (ProductType.recurringFeature.equals(prodType)) {
                    dto.recurringFeatureChargeLineItems().lineItems().add(charge);
                } else if (ProductType.oneTimeFeature.equals(prodType)) {
                    dto.onetimeFeatureChargeLineItems().lineItems().add(charge);
                }
            } else if (lineItem instanceof InvoiceProductCredit) {
                dto.productCreditLineItems().lineItems().add(lineItem);
            } else if (lineItem instanceof InvoiceDeposit) {
                dto.depositLineItems().lineItems().add(lineItem);
            } else if (lineItem instanceof InvoiceDepositRefund) {
                dto.depositRefundLineItems().lineItems().add(lineItem);
            } else if (lineItem instanceof InvoiceAccountCharge) {
                LeaseAdjustment adjusment = ((InvoiceAccountCharge) lineItem).adjustment();
                if (LeaseAdjustment.ExecutionType.immediate == adjusment.executionType().getValue()) {
                    dto.immediateAccountAdjustmentLineItems().lineItems().add(lineItem);
                } else if (LeaseAdjustment.ExecutionType.pending == adjusment.executionType().getValue()) {
                    dto.pendingAccountAdjustmentLineItems().lineItems().add(lineItem);
                }
            } else if (lineItem instanceof InvoiceAccountCredit) {
                LeaseAdjustment adjusment = ((InvoiceAccountCredit) lineItem).adjustment();
                if (LeaseAdjustment.ExecutionType.immediate == adjusment.executionType().getValue()) {
                    dto.immediateAccountAdjustmentLineItems().lineItems().add(lineItem);
                } else if (LeaseAdjustment.ExecutionType.pending == adjusment.executionType().getValue()) {
                    dto.pendingAccountAdjustmentLineItems().lineItems().add(lineItem);
                }
            } else if (lineItem instanceof InvoiceLatePaymentFee) {
                dto.latePaymentFeeLineItems().lineItems().add(lineItem);
            } else if (lineItem instanceof InvoiceNSF) {
                dto.nsfChargeLineItems().lineItems().add(lineItem);
            } else if (lineItem instanceof InvoiceWithdrawal) {
                dto.withdrawalLineItems().lineItems().add(lineItem);
            } else if (lineItem instanceof InvoicePayment) {
                dto.paymentLineItems().lineItems().add(lineItem);
            } else if (lineItem instanceof InvoicePaymentBackOut) {
                dto.rejectedPaymentLineItems().lineItems().add(lineItem);
            }
        }
    }
}
