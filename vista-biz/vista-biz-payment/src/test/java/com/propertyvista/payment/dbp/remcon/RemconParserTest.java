/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-05-20
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.payment.dbp.remcon;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.gwt.server.IOUtils;

public class RemconParserTest {

    private static final Logger log = LoggerFactory.getLogger(RemconParserTest.class);

    RemconFile loadResource(String name) {
        IOUtils.resourceFileName(name, RemconParserTest.class);
        RemconParser parser = new RemconParser();
        parser.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(IOUtils.resourceFileName(name, RemconParserTest.class)));
        RemconFile remcon = parser.getRemconFile();
        log.debug("RemconFile {} {}", name, remcon);
        return remcon;
    }

    @Test
    public void testEmptyFile() {
        RemconFile remcon = loadResource("remcon_empty.txt");
        Assert.assertEquals(2, remcon.records.size());
    }

    @Test
    public void testSingleTransaction() {
        RemconFile remcon = loadResource("remcon_single_tran_20013-04-09.txt");
        Assert.assertEquals(6, remcon.records.size());
        List<RemconRecordDetailRecord> records = remcon.getDetailRecords();
        Assert.assertEquals(1, records.size());

        log.debug("RemconRecordDetailRecord {}", RemconFile.toString(records.get(0)));
    }

    @Test
    public void testBMOTestFile1() {
        RemconFile remcon = loadResource("remcon_test_file.txt");
        Assert.assertEquals(15, remcon.records.size());
        List<RemconRecordDetailRecord> records = remcon.getDetailRecords();
        Assert.assertEquals(6, records.size());

        log.debug("RemconRecordDetailRecord {}", RemconFile.toString(records.get(0)));
    }

    @Test
    public void testBMOTestFile2() {
        RemconFile remcon = loadResource("remcon_test_file_multiple_accounts.txt");
        Assert.assertEquals(72, remcon.records.size());
        List<RemconRecordDetailRecord> records = remcon.getDetailRecords();
        Assert.assertEquals(44, records.size());

        log.debug("RemconRecordDetailRecord {}", RemconFile.toString(records.get(0)));
    }
}
