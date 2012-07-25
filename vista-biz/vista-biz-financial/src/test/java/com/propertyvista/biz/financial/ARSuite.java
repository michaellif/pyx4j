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

import com.propertyvista.biz.financial.ar.ARCreditDebitLinkManagerTest;
import com.propertyvista.biz.financial.ar.ARInvoiceDebitComparatorTest;
import com.propertyvista.biz.financial.ar.ARNotCoveredDebitInvoiceLineItemListTest;
import com.propertyvista.biz.financial.ar.ARSunnyDayScenarioTest;
import com.propertyvista.biz.financial.ar.ARArrearsSnapshotTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ ARCreditDebitLinkManagerTest.class, ARInvoiceDebitComparatorTest.class, ARNotCoveredDebitInvoiceLineItemListTest.class,
        ARArrearsSnapshotTest.class, ARSunnyDayScenarioTest.class, })
public class ARSuite {

}
