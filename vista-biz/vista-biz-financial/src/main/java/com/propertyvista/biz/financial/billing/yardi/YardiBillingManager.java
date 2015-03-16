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
 */
package com.propertyvista.biz.financial.billing.yardi;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.financial.billingcycle.BillingCycleFacade;
import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.tenant.lease.Lease;

public class YardiBillingManager {

    private YardiBillingManager() {
    }

    private static class SingletonHolder {
        public static final YardiBillingManager INSTANCE = new YardiBillingManager();
    }

    //TODO shouldn't be public
    public static YardiBillingManager instance() {
        return SingletonHolder.INSTANCE;
    }

    public Bill runBillingPreview(Lease leaseId) {
        Lease lease = ServerSideFactory.create(LeaseFacade.class).load(leaseId, false);
        BillingCycle billingCycle = ServerSideFactory.create(BillingCycleFacade.class).getLeaseFirstBillingCycle(lease);
        return new YardiBillProducer(billingCycle, lease).produceBill();
    }
}
