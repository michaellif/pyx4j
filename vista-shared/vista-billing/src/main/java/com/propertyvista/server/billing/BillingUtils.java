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
package com.propertyvista.server.billing;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.shared.IList;

import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing._InvoiceLineItem;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Product;
import com.propertyvista.domain.financial.offering.Service;

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

    public static <E extends _InvoiceLineItem> List<E> getLineItemsForType(Bill bill, Class<E> type) {
        return getLineItemsForType(bill.lineItems(), type);
    }

    @SuppressWarnings("unchecked")
    public static <E extends _InvoiceLineItem> List<E> getLineItemsForType(IList<_InvoiceLineItem> lineItems, Class<E> type) {
        List<E> items = new ArrayList<E>();
        for (_InvoiceLineItem lineItem : lineItems) {
            if (type.isAssignableFrom(lineItem.getClass())) {
                items.add((E) lineItem);
            }
        }
        return items;
    }
}
