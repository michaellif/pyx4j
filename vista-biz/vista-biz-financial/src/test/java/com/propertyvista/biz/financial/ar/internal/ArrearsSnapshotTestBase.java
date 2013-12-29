/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 15, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.financial.ar.internal;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.gwt.server.DateUtils;

import com.propertyvista.biz.financial.LeaseFinancialTestBase;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.AgingBuckets;
import com.propertyvista.domain.financial.billing.ArrearsSnapshot;
import com.propertyvista.domain.financial.billing.LeaseArrearsSnapshot;
import com.propertyvista.domain.tenant.lease.Lease;

public abstract class ArrearsSnapshotTestBase extends LeaseFinancialTestBase {

    private ArrearsSnapshot<?> actualArrearsSnapshot;

    private Map<String, AgingBuckets<?>> prevExpectedAgingBuckets;

    private LogicalDate prevFromDate;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        cleanUp();
        preloadData();
    }

    private void cleanUp() {
        Persistence.service().delete(EntityQueryCriteria.create(LeaseArrearsSnapshot.class));
        Persistence.service().commit(); // added to fix table lock
    }

    public void runAndConfirmBilling() {
        runBilling(true);
    }

    protected void updateArrearsHistory() {
        ServerSideFactory.create(ARFacade.class).updateArrearsHistory(billingAccount());
        Persistence.service().commit();
    }

    protected void assertArrearsSnapshotStart(String asOf) {
        actualArrearsSnapshot = ServerSideFactory.create(ARFacade.class).getArrearsSnapshot(billingAccount(), asDate(asOf));
        prevFromDate = actualArrearsSnapshot.fromDate().getValue();
        prevExpectedAgingBuckets = new HashMap<String, AgingBuckets<?>>();
    }

    protected void assertArrearsSnapshotIsSameAsBefore(String from, String to) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(asDate(from));

        LogicalDate end = new LogicalDate(asDate(from));
        LogicalDate asOf = null;
        do {
            asOf = new LogicalDate(cal.getTime());
            assertPrevArrearsSnapshot(asOf);
            cal.add(Calendar.DATE, 1);
        } while (asOf.before(end));

    }

    protected void assertPrevArrearsSnapshot(String asOf) {
        assertPrevArrearsSnapshot(asDate(asOf));
    }

    protected void assertPrevArrearsSnapshot(LogicalDate asOf) {
        actualArrearsSnapshot = ServerSideFactory.create(ARFacade.class).getArrearsSnapshot(billingAccount(), asOf);
        assertEquals("got unexpected snapshot from other date", prevFromDate, actualArrearsSnapshot.fromDate().getValue());

        for (AgingBuckets<?> expected : prevExpectedAgingBuckets.values()) {
            if (expected.arCode().getValue() != null) {
                assertArrearsCategory(expected.arCode().getValue(), expected.bucketCurrent().getValue().toString(), expected.bucket30().getValue().toString(),
                        expected.bucket60().getValue().toString(), expected.bucket90().getValue().toString(), expected.bucketOver90().getValue().toString());
            }
        }

        AgingBuckets<?> expectedTotal = prevExpectedAgingBuckets.get("TOTAL");
        assertArrearsTotal(expectedTotal.bucketCurrent().getValue().toString(), expectedTotal.bucket30().getValue().toString(), expectedTotal.bucket60()
                .getValue().toString(), expectedTotal.bucket90().getValue().toString(), expectedTotal.bucketOver90().getValue().toString());
    }

    protected void assertArrearsCategory(ARCode.Type debitType, String expectedCurrent, String expected30, String expected60, String expected90,
            String expectedOver90) {

        for (AgingBuckets<?> actualBuckets : actualArrearsSnapshot.agingBuckets()) {
            if (actualBuckets.arCode().getValue() == debitType) {
                assertAgingBuckets(expectedAgingBuckets(debitType, expectedCurrent, expected30, expected60, expected90, expectedOver90), actualBuckets);
                return;
            }
        }
        assertTrue("arrears snapshot does not contain aging bucket of type " + debitType, false);
    }

    protected void assertArrearsTotal(String expectedCurrent, String expected30, String expected60, String expected90, String expectedOver90) {
        assertArrearsCategory(null, expectedCurrent, expected30, expected60, expected90, expectedOver90);
    }

    private AgingBuckets expectedAgingBuckets(ARCode.Type debitType, String expectedCurrent, String expected30, String expected60, String expected90,
            String expectedOver90) {
        AgingBuckets expected = EntityFactory.create(AgingBuckets.class);
        expected.arCode().setValue(debitType);
        expected.bucketCurrent().setValue(new BigDecimal(expectedCurrent));
        expected.bucket30().setValue(new BigDecimal(expected30));
        expected.bucket60().setValue(new BigDecimal(expected60));
        expected.bucket90().setValue(new BigDecimal(expected90));
        expected.bucketOver90().setValue(new BigDecimal(expectedOver90));

        AgingBuckets cachedExpected = prevExpectedAgingBuckets.get(debitType);
        if (cachedExpected == null) {
            String bucketsKey = debitType == null ? "TOTAL" : debitType.name();
            prevExpectedAgingBuckets.put(bucketsKey, expected);
            cachedExpected = expected;
        } else {
            if (!EntityGraph.fullyEqualValues(cachedExpected, expected)) {
                throw new IllegalStateException(
                        "cached and acutual buckets don't match. did you try to assert bucket's for the same debit type more than once?");
            }
        }
        return cachedExpected;

    }

    private void assertAgingBuckets(AgingBuckets expected, AgingBuckets actual) {
        assertEquals("bucket current", expected.bucketCurrent().getValue(), actual.bucketCurrent().getValue());
        assertEquals("bucket 30", expected.bucket30().getValue(), actual.bucket30().getValue());
        assertEquals("bucket 60", expected.bucket60().getValue(), actual.bucket60().getValue());
        assertEquals("bucket 90", expected.bucket90().getValue(), actual.bucket90().getValue());
        assertEquals("bucket over 90", expected.bucketOver90().getValue(), actual.bucketOver90().getValue());
    }

    private LogicalDate asDate(String date) {
        return new LogicalDate(DateUtils.detectDateformat(date));
    }

    private BillingAccount billingAccount() {
        return Persistence.service().<Lease> retrieve(Lease.class, getLease().getPrimaryKey()).billingAccount();
    }

}
