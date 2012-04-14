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
package com.propertyvista.server.financial.billing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.InvoiceAccountCharge;
import com.propertyvista.domain.financial.billing.InvoiceAccountCredit;
import com.propertyvista.domain.financial.billing.InvoiceDeposit;
import com.propertyvista.domain.financial.billing.InvoiceDepositRefund;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.financial.billing.InvoicePayment;
import com.propertyvista.domain.financial.billing.InvoiceProductCharge;
import com.propertyvista.domain.financial.billing.InvoiceProductCharge.ProductType;
import com.propertyvista.domain.financial.billing.InvoiceWithdrawal;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Product;
import com.propertyvista.domain.financial.offering.Service;
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

    public static <E extends InvoiceLineItem> List<E> getLineItemsForType(Bill bill, Class<E> type) {
        return getLineItemsForType(bill.lineItems(), type);
    }

    @SuppressWarnings("unchecked")
    public static <E extends InvoiceLineItem> List<E> getLineItemsForType(Collection<InvoiceLineItem> lineItems, Class<E> type) {
        List<E> items = new ArrayList<E>();
        for (InvoiceLineItem lineItem : lineItems) {
            if (type.isAssignableFrom(lineItem.getClass())) {
                items.add((E) lineItem);
            }
        }
        return items;
    }

    public static void populateDto(Bill bill, BillDTO dto) {
        for (InvoiceLineItem lineItem : bill.lineItems()) {
            // *** Current Bill list values ***
            // charges
            if (lineItem instanceof InvoiceProductCharge) {
                InvoiceProductCharge charge = (InvoiceProductCharge) lineItem;
                ProductType prodType = charge.productType().getValue();
                if (ProductType.recurringFeature.equals(prodType)) {
                    // Additional recurring charges
                    dto.recurringProductCharges().add(charge);
                } else if (ProductType.oneTimeFeature.equals(prodType)) {
                    // One-time charges
                    dto.onetimeProductCharges().add(charge);
                }
                //} else if (lineItem instanceof InvoiceProductCredit) {
                // Credit(s)
            } else if (lineItem instanceof InvoiceDeposit) {
                // Deposit(s)
                dto.deposits().add((InvoiceDeposit) lineItem);
            }
            // *** Last Bill list values
            else if (lineItem instanceof InvoiceDepositRefund) {
                // Deposit refund(s)
                dto.depositRefunds().add((InvoiceDepositRefund) lineItem);
            } else if (lineItem instanceof InvoiceAccountCharge) {
                // Immediate adjustment charges
                dto.acntAdjustmentCharges().add((InvoiceAccountCharge) lineItem);
            } else if (lineItem instanceof InvoiceAccountCredit) {
                // Immediate adjustment credits
                dto.acntAdjustmentCredits().add((InvoiceAccountCredit) lineItem);
            } else if (lineItem instanceof InvoiceWithdrawal) {
                // Withdrawals(s)
                dto.withdrawals().add((InvoiceWithdrawal) lineItem);
            } else if (lineItem instanceof InvoicePayment) {
                PaymentStatus status = ((InvoicePayment) lineItem).paymentRecord().paymentStatus().getValue();
                if (PaymentStatus.Rejected.equals(status)) {
                    // Rejected payment(s)
                    dto.rejectedPayments().add((InvoicePayment) lineItem);
                } else {
                    // Payment(s)
                    dto.payments().add((InvoicePayment) lineItem);
                }

            }
        }
    }
}
