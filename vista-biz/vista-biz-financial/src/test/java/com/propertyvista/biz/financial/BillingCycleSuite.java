/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 21, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.propertyvista.biz.financial.billingcycle.BillingCycleForDefaultStartDayPolicy15Test;
import com.propertyvista.biz.financial.billingcycle.BillingCycleForDefaultStartDayPolicy1Test;
import com.propertyvista.biz.financial.billingcycle.BillingCycleForDefaultStartDayPolicy28Test;
import com.propertyvista.biz.financial.billingcycle.BillingCycleForSameDayAsLeaseStartDayPolicyTest;
import com.propertyvista.biz.financial.billingcycle.BillingTypeWithDefaultStartDayPolicyTest;
import com.propertyvista.biz.financial.billingcycle.BillingTypeWithSameDayAsLeaseStartDayPolicyTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
//@formatter:off
    BillingCycleForDefaultStartDayPolicy15Test.class, 
    BillingCycleForDefaultStartDayPolicy1Test.class, 
    BillingCycleForDefaultStartDayPolicy28Test.class, 
    BillingCycleForSameDayAsLeaseStartDayPolicyTest.class, 
    BillingTypeWithDefaultStartDayPolicyTest.class, 
    BillingTypeWithSameDayAsLeaseStartDayPolicyTest.class, 
    //@formatter:on

})
public class BillingCycleSuite {

}
