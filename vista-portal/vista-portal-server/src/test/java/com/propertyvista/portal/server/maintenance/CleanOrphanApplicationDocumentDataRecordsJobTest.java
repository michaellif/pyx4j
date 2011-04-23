/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-04-22
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.maintenance;

import com.propertyvista.config.tests.VistaDBTestCase;
import com.propertyvista.portal.domain.DemoData;
import com.propertyvista.portal.domain.pt.ApplicationDocument;
import com.propertyvista.portal.server.preloader.VistaDataPreloaders;
import com.propertyvista.server.domain.ApplicationDocumentData;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.unit.server.mock.TestLifecycle;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import junit.framework.Assert;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CleanOrphanApplicationDocumentDataRecordsJobTest extends VistaDBTestCase {
    private final static Logger logger = LoggerFactory.getLogger(CleanOrphanApplicationDocumentDataRecordsJobTest.class);
    
    private static final JobExecutionContext context = null;
    private CleanOrphanApplicationDocumentDataRecordsJob instance;
    
    public CleanOrphanApplicationDocumentDataRecordsJobTest() {
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        DemoData.MAX_CUSTOMERS = 5;
        new VistaDataPreloaders().preloadAll(false);
        instance = new CleanOrphanApplicationDocumentDataRecordsJob();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        TestLifecycle.tearDown();
    }

    /**
     * Test of execute method, of class CleanOrphanApplicationDocumentDataRecordsJob.
     */
    public void testExecute() throws Exception {
        int totalCountBefore = PersistenceServicesFactory.getPersistenceService().count(EntityQueryCriteria.create(ApplicationDocumentData.class));

        //first run on on fully linked and recently created records - no records deletions expected
        instance.execute(context);
        int totalCountAfter = PersistenceServicesFactory.getPersistenceService().count(EntityQueryCriteria.create(ApplicationDocumentData.class));
        Assert.assertEquals("It was expected no changes since all records are linked and recent", totalCountBefore, totalCountAfter);

        //unlink one record
        //EntityQueryCriteria<ApplicationDocument> criteria = EntityQueryCriteria.create(ApplicationDocument.class);
        //criteria.add(PropertyCriterion.eq(criteria.proto().dataId(), 1L));
        //ApplicationDocument doc = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);
        //logger.info("doc={}", doc);
        List<ApplicationDocument> allDocs = PersistenceServicesFactory.getPersistenceService().query(EntityQueryCriteria.create(ApplicationDocument.class));
        Assert.assertFalse("There is no ApplicationDocument records in DB for testing", allDocs.isEmpty());
        PersistenceServicesFactory.getPersistenceService().delete(allDocs.get(0));
        
        //then run the job again - expect no changes since all records are recent
        instance.execute(context);
        totalCountAfter = PersistenceServicesFactory.getPersistenceService().count(EntityQueryCriteria.create(ApplicationDocumentData.class));
        Assert.assertEquals("It was expected no changes since all records are recent", totalCountBefore, totalCountAfter);
        
        //update record creation date to back in time to make it appear as old
        List<ApplicationDocumentData> allDocsData = PersistenceServicesFactory.getPersistenceService().query(EntityQueryCriteria.create(ApplicationDocumentData.class));
        logger.info("allDocs.size={}",allDocsData.size());
        //long appDocDataId=2;
        for(ApplicationDocumentData d: allDocsData) {
            //if (d.id().getValue()!=1) appDocDataId=d.id().getValue();
            Calendar c = new GregorianCalendar();
            c.add(Calendar.HOUR, -25);
            d.created().setValue(c.getTime());
            PersistenceServicesFactory.getPersistenceService().persist(d);
        }
        //logger.info("appDocDataId={}", appDocDataId);
        
        //then run the job again - expect one unlinked records to be deleted
        instance.execute(context);
        totalCountAfter = PersistenceServicesFactory.getPersistenceService().count(EntityQueryCriteria.create(ApplicationDocumentData.class));
        totalCountBefore--;
        Assert.assertEquals("It was expected one unlinked record to be deleted", totalCountBefore, totalCountAfter);
        
        //then run the job again - expect no changes since all remaining records are still linked
        instance.execute(context);
        totalCountAfter = PersistenceServicesFactory.getPersistenceService().count(EntityQueryCriteria.create(ApplicationDocumentData.class));
        Assert.assertEquals("It was expected no changes since all remaining records are linked", totalCountBefore, totalCountAfter);
        
        //unlink one more record
        allDocs = PersistenceServicesFactory.getPersistenceService().query(EntityQueryCriteria.create(ApplicationDocument.class));
        Assert.assertFalse("There is no ApplicationDocument records in DB for testing", allDocs.isEmpty());
        PersistenceServicesFactory.getPersistenceService().delete(allDocs.get(0));
        //criteria = EntityQueryCriteria.create(ApplicationDocument.class);
        //criteria.add(PropertyCriterion.eq(criteria.proto().dataId(), appDocDataId));
        //doc = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);
        //logger.info("doc={}", doc);
        //PersistenceServicesFactory.getPersistenceService().delete(doc);
        
        //then run the job again - expect second unlinked records to be deleted
        instance.execute(context);
        totalCountAfter = PersistenceServicesFactory.getPersistenceService().count(EntityQueryCriteria.create(ApplicationDocumentData.class));
        totalCountBefore--;
        Assert.assertEquals("It was expected one unlinked record to be deleted", totalCountBefore, totalCountAfter);
        
    }
}
