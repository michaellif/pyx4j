/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 15, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.eft.caledoncards.reports;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.operations.domain.eft.cards.to.DailyReportRecord;
import com.propertyvista.operations.domain.eft.cards.to.DailyReportRecord.DailyReportCardType;
import com.propertyvista.operations.domain.eft.cards.to.DailyReportRecord.DailyReportRecordType;

public class DailyReportParserTest {

    @Test
    public void testReadingExample() throws Exception {
        String name = "20140815_003626_PROPERTYVISTA.CSV";
        List<DailyReportRecord> records = new DailyReportParser().parsReport(IOUtils.getResource(name, this.getClass()).openStream(), name);
        DailyReportRecord r;
        {
            r = records.get(1);
            Assert.assertEquals("PASDFG59", r.terminalID().getValue());
            Assert.assertEquals(DailyReportRecordType.PRCO, r.transactionType().getValue());
            Assert.assertEquals(DailyReportCardType.VISA, r.cardType().getValue());
            Assert.assertEquals(new BigDecimal("964.07"), r.amount().getValue());
            Assert.assertEquals("R64227", r.referenceNumber().getValue());
            Assert.assertEquals(true, r.approved().getValue());
            Assert.assertEquals(false, r.voided().getValue());
        }

        {
            r = records.get(26);
            Assert.assertEquals("PASDFG95", r.terminalID().getValue());
            Assert.assertEquals(DailyReportRecordType.SETT, r.transactionType().getValue());
            Assert.assertEquals(new BigDecimal("800.72"), r.amount().getValue());
        }
    }

    @Test
    @Ignore
    public void testReadExisting() throws Exception {
        File dir = new File("D:/var/prod/Daily Reports Files");
        for (File file : dir.listFiles()) {
            new DailyReportParser().parsReport(file);
        }
    }

}
