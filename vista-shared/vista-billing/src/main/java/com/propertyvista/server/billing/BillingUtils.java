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

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.financial.billing.BillCharge;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Product;
import com.propertyvista.domain.financial.offering.Service;

public class BillingUtils {

    public static boolean isService(Product product) {
        return product.cast().isAssignableFrom(Service.class);
    }

    public static boolean isFeature(Product product) {
        return product.cast().isAssignableFrom(Feature.class);
    }

    public static boolean isRecurringFeature(Product product) {
        return isFeature(product) && ((Feature) product.cast()).isRecurring().getValue();
    }

    public static BillCharge getServiceCharge(IList<BillCharge> charges) {
        if (false) {
            EntityQueryCriteria<BillCharge> criteria = EntityQueryCriteria.create(BillCharge.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().billableItem().item().product(), Service.class));
            BillCharge billCharge = Persistence.service().retrieve(criteria);
        }

        for (BillCharge charge : charges) {
            if (charge.billableItem().item().product().isValueDetached()) {
                Persistence.service().retrieve(charge.billableItem().item().product());
            }
            if (isService(charge.billableItem().item().product())) {
                return charge;
            }
        }
        return null;
    }

    public static List<BillCharge> getFeatureCharges(IList<BillCharge> charges) {
        List<BillCharge> featureCharges = new ArrayList<BillCharge>();
        for (BillCharge charge : charges) {
            if (charge.billableItem().item().product().isValueDetached()) {
                Persistence.service().retrieve(charge.billableItem().item().product());
            }
            if (isFeature(charge.billableItem().item().product())) {
                featureCharges.add(charge);
            }
        }
        return featureCharges;
    }
}
