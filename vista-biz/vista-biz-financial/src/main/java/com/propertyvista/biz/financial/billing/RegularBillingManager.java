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

import java.util.Arrays;
import java.util.List;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.SysDateManager;
import com.propertyvista.domain.financial.billing.Bill;

class RegularBillingManager extends AbstractBillingManager {

    private static final I18n i18n = I18n.get(RegularBillingManager.class);

    RegularBillingManager(Bill bill) {
        super(bill, Bill.BillType.Regular);

        if (SysDateManager.getSysDate().compareTo(bill.billingRun().executionTargetDate().getValue()) < 0) {
            throw new BillingException(i18n.tr("Regular billing can't run before target execution date"));
        }

    }

    @Override
    protected List<AbstractBillingProcessor> initProcessors() {
        // @formatter:off
        return Arrays.asList(new AbstractBillingProcessor[] {
                
                new BillingProductChargeProcessor(this),
                new BillingLeaseAdjustmentProcessor(this), 
                new BillingPaymentProcessor(this), 
                /** Should run last **/
                new BillingLatePaymentFeeProcessor(this) 
                
        });
        // @formatter:on
    }
}
