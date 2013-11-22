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
import java.util.ListIterator;
import java.util.Map;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.legal.ltbcommon.RentOwingForPeriod;

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

    public List<RentOwingForPeriod> debitsForPeriod(Map<BillingCycle, List<InvoiceDebit>> agregatedDebits) {
        List<RentOwingForPeriod> debitsForPeriod = new LinkedList<RentOwingForPeriod>();

        for (Map.Entry<BillingCycle, List<InvoiceDebit>> billingCycleDebits : agregatedDebits.entrySet()) {
            RentOwingForPeriod rentOwingForPeriod = EntityFactory.create(RentOwingForPeriod.class);
            rentOwingForPeriod.from().setValue(billingCycleDebits.getKey().billingCycleStartDate().getValue());
            rentOwingForPeriod.to().setValue(billingCycleDebits.getKey().billingCycleEndDate().getValue());

            rentOwingForPeriod.rentCharged().setValue(BigDecimal.ZERO);
            rentOwingForPeriod.rentPaid().setValue(BigDecimal.ZERO);
            rentOwingForPeriod.rentOwing().setValue(BigDecimal.ZERO);

            for (InvoiceDebit debit : billingCycleDebits.getValue()) {
                rentOwingForPeriod.rentCharged().setValue(rentOwingForPeriod.rentCharged().getValue().add(debit.amount().getValue()));
                rentOwingForPeriod.rentCharged().setValue(rentOwingForPeriod.rentCharged().getValue().add(debit.taxTotal().getValue()));
                rentOwingForPeriod.rentOwing().setValue(rentOwingForPeriod.rentOwing().getValue().add(debit.outstandingDebit().getValue()));
            }
            rentOwingForPeriod.rentPaid().setValue(rentOwingForPeriod.rentCharged().getValue().subtract(rentOwingForPeriod.rentOwing().getValue()));

            debitsForPeriod.add(rentOwingForPeriod);
        }
        Collections.sort(debitsForPeriod, new Comparator<RentOwingForPeriod>() {
            @Override
            public int compare(RentOwingForPeriod o1, RentOwingForPeriod o2) {
                return o1.from().getValue().compareTo(o2.from().getValue());
            }
        });
        if (debitsForPeriod.size() > 3) {
            ListIterator<RentOwingForPeriod> li = debitsForPeriod.listIterator();
            RentOwingForPeriod rentOwingForPeriodAccumulator = li.next();

            int aggregatedCount = debitsForPeriod.size() - 3;
            int currentAggregated = 0;

            while (currentAggregated != aggregatedCount) {
                RentOwingForPeriod rentOwingForPeriod = li.next();
                li.remove();
                rentOwingForPeriodAccumulator.rentCharged().setValue(
                        rentOwingForPeriodAccumulator.rentCharged().getValue().add(rentOwingForPeriod.rentCharged().getValue()));
                rentOwingForPeriodAccumulator.rentPaid().setValue(
                        rentOwingForPeriodAccumulator.rentPaid().getValue().add(rentOwingForPeriod.rentPaid().getValue()));
                rentOwingForPeriodAccumulator.rentOwing().setValue(
                        rentOwingForPeriodAccumulator.rentOwing().getValue().add(rentOwingForPeriod.rentOwing().getValue()));
                currentAggregated += 1;

                if (currentAggregated == aggregatedCount) {
                    rentOwingForPeriodAccumulator.to().setValue(rentOwingForPeriod.to().getValue());
                }
            }

        }
        return debitsForPeriod;

    }
}
