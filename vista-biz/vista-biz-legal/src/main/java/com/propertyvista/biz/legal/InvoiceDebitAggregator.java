/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
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
 * Created on 2013-09-30
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.legal;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.legal.N4RentOwingForPeriod;

public class InvoiceDebitAggregator {

    public Map<BillingCycle, List<InvoiceDebit>> aggregate(List<InvoiceDebit> invoiceDebits) {
        Map<BillingCycle, List<InvoiceDebit>> billingCycleDebits = new HashMap<BillingCycle, List<InvoiceDebit>>();

        for (InvoiceDebit debit : invoiceDebits) {
            if (!billingCycleDebits.containsKey(debit.billingCycle())) {
                billingCycleDebits.put(debit.billingCycle(), new LinkedList<InvoiceDebit>());
            }
            billingCycleDebits.get(debit.billingCycle()).add(debit);
        }

        return billingCycleDebits;

    }

    public List<N4RentOwingForPeriod> debitsForPeriod(Map<BillingCycle, List<InvoiceDebit>> agregatedDebits) {
        List<N4RentOwingForPeriod> debitsForPeriod = new LinkedList<N4RentOwingForPeriod>();

        for (Map.Entry<BillingCycle, List<InvoiceDebit>> billingCycleDebits : agregatedDebits.entrySet()) {
            N4RentOwingForPeriod rentForPeriod = EntityFactory.create(N4RentOwingForPeriod.class);
            rentForPeriod.from().setValue(billingCycleDebits.getKey().billingCycleStartDate().getValue());
            rentForPeriod.to().setValue(billingCycleDebits.getKey().billingCycleEndDate().getValue());
            rentForPeriod.rentCharged().setValue(BigDecimal.ZERO);
            rentForPeriod.rentPaid().setValue(BigDecimal.ZERO);
            rentForPeriod.rentOwing().setValue(BigDecimal.ZERO);

            for (InvoiceDebit debit : billingCycleDebits.getValue()) {
                rentForPeriod.rentCharged().setValue(rentForPeriod.rentCharged().getValue().add(debit.amount().getValue()));
                rentForPeriod.rentOwing().setValue(rentForPeriod.rentOwing().getValue().add(debit.outstandingDebit().getValue()));
            }
            rentForPeriod.rentPaid().setValue(rentForPeriod.rentCharged().getValue().subtract(rentForPeriod.rentOwing().getValue()));

            debitsForPeriod.add(rentForPeriod);
        }
        Collections.sort(debitsForPeriod, new Comparator<N4RentOwingForPeriod>() {
            @Override
            public int compare(N4RentOwingForPeriod o1, N4RentOwingForPeriod o2) {
                return o1.from().getValue().compareTo(o2.from().getValue());
            }
        });
        return debitsForPeriod;

    }
}
