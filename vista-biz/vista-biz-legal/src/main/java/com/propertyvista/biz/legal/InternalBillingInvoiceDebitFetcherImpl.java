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
 * Created on 2013-10-30
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.legal;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.InvoiceDebit;

public class InternalBillingInvoiceDebitFetcherImpl implements InvoiceDebitFetcher {

    @Override
    public List<InvoiceDebit> getInvoiceDebits(Collection<ARCode> acceptableArCodes, BillingAccount billingAccount, LogicalDate dueDate) {
        // TODO assert billingAccount is for billing

        List<InvoiceDebit> outstandingDebits = ServerSideFactory.create(ARFacade.class).getNotCoveredDebitInvoiceLineItems(billingAccount);

        Set<BillingCycle> outsandingBillingCycles = new HashSet<BillingCycle>();
        List<InvoiceDebit> debitsOfOutstandingBillingCycles = new LinkedList<InvoiceDebit>();
        for (InvoiceDebit outstandingDebit : outstandingDebits) {
            outsandingBillingCycles.add(outstandingDebit.billingCycle());
        }
        for (BillingCycle outstandingBillingCycle : outsandingBillingCycles) {
            EntityQueryCriteria<InvoiceDebit> criteria = EntityQueryCriteria.create(InvoiceDebit.class);
            criteria.eq(criteria.proto().billingCycle(), outstandingBillingCycle);
            criteria.eq(criteria.proto().billingAccount(), billingAccount);
            debitsOfOutstandingBillingCycles.addAll(Persistence.service().query(criteria));
        }

        List<InvoiceDebit> filteredDebits = N4GenerationUtils.filterDebits(debitsOfOutstandingBillingCycles, acceptableArCodes,
                SystemDateManager.getLogicalDate());
        return filteredDebits;

    }

}
