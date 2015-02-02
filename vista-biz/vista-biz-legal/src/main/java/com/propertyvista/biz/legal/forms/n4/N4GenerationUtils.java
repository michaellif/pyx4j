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
 * Created on 2013-10-25
 * @author ArtyomB
 */
package com.propertyvista.biz.legal.forms.n4;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.legal.n4.N4UnpaidCharge;
import com.propertyvista.domain.tenant.lease.Lease;

public class N4GenerationUtils {

    public static List<N4UnpaidCharge> getUnpaidCharges(Lease lease, Collection<ARCode> acceptableArCodes) {
        LogicalDate today = SystemDateManager.getLogicalDate();

        List<InvoiceDebit> debits = ServerSideFactory.create(ARFacade.class).getNotCoveredDebitInvoiceLineItems(lease.billingAccount());
        List<InvoiceDebit> filteredDebits = filterDebits(debits, acceptableArCodes, today);
        List<N4UnpaidCharge> owings = new ArrayList<>();
        for (InvoiceDebit debit : filteredDebits) {
            N4UnpaidCharge owing = EntityFactory.create(N4UnpaidCharge.class);
            owing.fromDate().setValue(debit.billingCycle().billingCycleStartDate().getValue());
            owing.toDate().setValue(debit.billingCycle().billingCycleEndDate().getValue());
            owing.rentCharged().setValue(owing.rentCharged().getValue(BigDecimal.ZERO).add(debit.amount().getValue()));
            owing.rentCharged().setValue(owing.rentCharged().getValue().add(debit.taxTotal().getValue()));
            owing.rentOwing().setValue(owing.rentOwing().getValue(BigDecimal.ZERO).add(debit.outstandingDebit().getValue()));
            owing.rentPaid().setValue(owing.rentCharged().getValue().subtract(owing.rentOwing().getValue()));
            owing.arCode().set(debit.arCode());
            owings.add(owing);
        }
        return owings;
    }

    private static List<InvoiceDebit> filterDebits(List<InvoiceDebit> debits, Collection<ARCode> acceptableArCodes, LogicalDate asOf) {
        List<InvoiceDebit> filteredDebits = new ArrayList<InvoiceDebit>(debits.size());
        for (InvoiceDebit debit : debits) {
            if (acceptableArCodes.contains(debit.arCode()) && debit.dueDate().getValue().compareTo(asOf) < 0) {
                filteredDebits.add(debit);
            }
        }
        return filteredDebits;
    }
}
