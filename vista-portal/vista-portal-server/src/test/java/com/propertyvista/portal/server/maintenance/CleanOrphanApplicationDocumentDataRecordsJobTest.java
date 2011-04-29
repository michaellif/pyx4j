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
import com.propertyvista.portal.domain.pt.PotentialTenantInfo;
import com.propertyvista.portal.server.preloader.VistaDataPreloaders;
import com.propertyvista.server.domain.ApplicationDocumentData;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.quartz.SchedulerHelper;
import com.pyx4j.unit.server.mock.TestLifecycle;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import junit.framework.Assert;

import org.quartz.DateBuilder;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CleanOrphanApplicationDocumentDataRecordsJobTest extends VistaDBTestCase {
    private final static Logger logger = LoggerFactory.getLogger(CleanOrphanApplicationDocumentDataRecordsJobTest.class);

    private final static boolean RUN_TESTS = true;

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
        if (!RUN_TESTS)
            return;

        int totalCountExpected = PersistenceServicesFactory.getPersistenceService().count(EntityQueryCriteria.create(ApplicationDocumentData.class));

        //first run on on fully linked and recently created records - no records deletions expected
        instance.execute(context);
        int totalCountAfter = PersistenceServicesFactory.getPersistenceService().count(EntityQueryCriteria.create(ApplicationDocumentData.class));
        Assert.assertEquals("It was expected no changes since all records are linked and recent", totalCountExpected, totalCountAfter);

        //create new application data record
        List<ApplicationDocumentData> allDocData = PersistenceServicesFactory.getPersistenceService().query(
                EntityQueryCriteria.create(ApplicationDocumentData.class));
        ApplicationDocumentData data1 = allDocData.get(0);
        ApplicationDocumentData applicationDocumentData = createDataRecord(data1.tenant());
        totalCountExpected++;
        totalCountAfter = PersistenceServicesFactory.getPersistenceService().count(EntityQueryCriteria.create(ApplicationDocumentData.class));
        Assert.assertEquals("It was expected record count increased since we added one more data record", totalCountExpected, totalCountAfter);

        instance.execute(context);

        ApplicationDocumentData data = PersistenceServicesFactory.getPersistenceService().retrieve(ApplicationDocumentData.class,
                applicationDocumentData.id().getValue());
        Assert.assertNotNull("Cannot find data record", data);

        //update record creation date to back in time to make it appear as old
        Calendar c = new GregorianCalendar();
        c.add(Calendar.HOUR, -25);
        applicationDocumentData.created().setValue(c.getTime());
        PersistenceServicesFactory.getPersistenceService().persist(applicationDocumentData);

        instance.execute(context);
        data = PersistenceServicesFactory.getPersistenceService().retrieve(ApplicationDocumentData.class, applicationDocumentData.id().getValue());
        Assert.assertNull("Record is still in DB - expected to be cleaned up", data);

        /*
         * //unlink one record
         * //EntityQueryCriteria<ApplicationDocument> criteria =
         * EntityQueryCriteria.create(ApplicationDocument.class);
         * //criteria.add(PropertyCriterion.eq(criteria.proto().dataId(), 1L));
         * //ApplicationDocument doc =
         * PersistenceServicesFactory.getPersistenceService().retrieve(criteria);
         * //logger.info("doc={}", doc);
         * List<ApplicationDocument> allDocs =
         * PersistenceServicesFactory.getPersistenceService
         * ().query(EntityQueryCriteria.create(ApplicationDocument.class));
         * Assert.assertFalse("There is no ApplicationDocument records in DB for testing",
         * allDocs.isEmpty());
         * PersistenceServicesFactory.getPersistenceService().delete(allDocs.get(0));
         * 
         * //then run the job again - expect no changes since all records are recent
         * instance.execute(context);
         * totalCountAfter =
         * PersistenceServicesFactory.getPersistenceService().count(EntityQueryCriteria
         * .create(ApplicationDocumentData.class));
         * Assert.assertEquals("It was expected no changes since all records are recent",
         * totalCountExpected, totalCountAfter);
         * 
         * //update record creation date to back in time to make it appear as old
         * List<ApplicationDocumentData> allDocsData =
         * PersistenceServicesFactory.getPersistenceService
         * ().query(EntityQueryCriteria.create(ApplicationDocumentData.class));
         * logger.info("allDocs.size={}",allDocsData.size());
         * //long appDocDataId=2;
         * for(ApplicationDocumentData d: allDocsData) {
         * //if (d.id().getValue()!=1) appDocDataId=d.id().getValue();
         * Calendar c = new GregorianCalendar();
         * c.add(Calendar.HOUR, -25);
         * d.created().setValue(c.getTime());
         * PersistenceServicesFactory.getPersistenceService().persist(d);
         * }
         * //logger.info("appDocDataId={}", appDocDataId);
         * 
         * //then run the job again - expect one unlinked records to be deleted
         * instance.execute(context);
         * totalCountAfter =
         * PersistenceServicesFactory.getPersistenceService().count(EntityQueryCriteria
         * .create(ApplicationDocumentData.class));
         * totalCountExpected--;
         * Assert.assertEquals("It was expected one unlinked record to be deleted",
         * totalCountExpected, totalCountAfter);
         * 
         * //then run the job again - expect no changes since all remaining records are
         * still linked
         * instance.execute(context);
         * totalCountAfter =
         * PersistenceServicesFactory.getPersistenceService().count(EntityQueryCriteria
         * .create(ApplicationDocumentData.class));
         * Assert.assertEquals(
         * "It was expected no changes since all remaining records are linked",
         * totalCountExpected, totalCountAfter);
         * 
         * //unlink one more record
         * allDocs =
         * PersistenceServicesFactory.getPersistenceService().query(EntityQueryCriteria
         * .create(ApplicationDocument.class));
         * Assert.assertFalse("There is no ApplicationDocument records in DB for testing",
         * allDocs.isEmpty());
         * PersistenceServicesFactory.getPersistenceService().delete(allDocs.get(0));
         * //criteria = EntityQueryCriteria.create(ApplicationDocument.class);
         * //criteria.add(PropertyCriterion.eq(criteria.proto().dataId(), appDocDataId));
         * //doc = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);
         * //logger.info("doc={}", doc);
         * //PersistenceServicesFactory.getPersistenceService().delete(doc);
         * 
         * //then run the job again - expect second unlinked records to be deleted
         * instance.execute(context);
         * totalCountAfter =
         * PersistenceServicesFactory.getPersistenceService().count(EntityQueryCriteria
         * .create(ApplicationDocumentData.class));
         * totalCountExpected--;
         * Assert.assertEquals("It was expected one unlinked record to be deleted",
         * totalCountExpected, totalCountAfter);
         */
    }

    public void testQuartz() throws Exception {
        if (!RUN_TESTS)
            return;

        SchedulerHelper.init();

        Scheduler scheduler = SchedulerHelper.getScheduler();
        boolean isTesting = true;
        SimpleTrigger simpleTrigger;
        if (isTesting) {
            // testing schedule
            simpleTrigger = TriggerBuilder.newTrigger().withIdentity("testing", "Maintenance").startAt(DateBuilder.futureDate(1, IntervalUnit.SECOND))
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule().repeatForever().withIntervalInSeconds(2)).build();
        } else {
            // production schedule
            simpleTrigger = TriggerBuilder.newTrigger().withIdentity("dailyRun", "Maintenance").startAt(DateBuilder.tomorrowAt(4, 0, 0)) //start at 4am
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule().repeatForever().withIntervalInHours(24)).build();
        }
        JobDetail job = JobBuilder.newJob(CleanOrphanApplicationDocumentDataRecordsJob.class)
                .withIdentity("CleanOrphanApplicationDocumentDataRecordsJob", "Maintenance").build();
        scheduler.scheduleJob(job, simpleTrigger);
        //scheduler.start();
    }

    private static ApplicationDocumentData createDataRecord(PotentialTenantInfo tenantInfo) {
        ApplicationDocumentData applicationDocumentData = EntityFactory.create(ApplicationDocumentData.class);
        applicationDocumentData.tenant().set(tenantInfo);
        applicationDocumentData.application().set(tenantInfo.application());
        applicationDocumentData.data().setValue(new byte[0]);
        applicationDocumentData.contentType().setValue("image/jpeg");
        PersistenceServicesFactory.getPersistenceService().persist(applicationDocumentData);
        return applicationDocumentData;
    }
}
