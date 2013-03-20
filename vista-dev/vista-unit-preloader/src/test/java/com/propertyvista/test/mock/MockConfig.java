/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 13, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.test.mock;

import java.util.HashMap;
import java.util.Map;

import com.propertyvista.domain.financial.billing.InvoiceDebit.DebitType;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;
import com.propertyvista.domain.policy.policies.PADPolicy.OwingBalanceType;
import com.propertyvista.domain.policy.policies.PADPolicy.PADChargeType;

public class MockConfig {

    public Integer defaultBillingCycleSartDay = 1;

    public LeaseBillingPolicy.BillConfirmationMethod billConfirmationMethod = LeaseBillingPolicy.BillConfirmationMethod.manual;

    public boolean yardiIntegration = false;

    public Map<DebitType, OwingBalanceType> padBalanceTypeMap;

    public PADChargeType padChargeType;

    public boolean useCaledonMerchantAccounts = false;

    public void setPadChargeType(PADChargeType type) {
        padChargeType = type;
    }

    public void setPadBalanceType(DebitType charge, OwingBalanceType type) {
        if (padBalanceTypeMap == null) {
            padBalanceTypeMap = new HashMap<DebitType, OwingBalanceType>();
        }
        padBalanceTypeMap.put(charge, type);
    }
}
