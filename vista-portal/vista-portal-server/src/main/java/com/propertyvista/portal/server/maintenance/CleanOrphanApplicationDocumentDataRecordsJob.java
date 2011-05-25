/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 8, 2011
 * @author sergei
 * @version $Id$
 */
package com.propertyvista.portal.server.maintenance;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion.Restriction;

import com.propertyvista.portal.domain.ptapp.ApplicationDocument;
import com.propertyvista.server.domain.ApplicationDocumentData;

public class CleanOrphanApplicationDocumentDataRecordsJob implements Job {

    private final static Logger log = LoggerFactory.getLogger(CleanOrphanApplicationDocumentDataRecordsJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("CleanOrphanApplicationDocumentDataRecordsJob: STARTED");

        // select list of keys from addlicationDocumentData for records created within last 7 days,
        // but not later then last 24 hours (to avoid purging data that was recently created
        EntityQueryCriteria<ApplicationDocumentData> allDataCriteria = EntityQueryCriteria.create(ApplicationDocumentData.class);
        Calendar minDate = new GregorianCalendar();
        minDate.add(Calendar.DATE, -7);
        log.debug("minDate={}", minDate);
        allDataCriteria.add(new PropertyCriterion(allDataCriteria.proto().created(), Restriction.GREATER_THAN, minDate.getTime()));
        Calendar maxDate = new GregorianCalendar();
        maxDate.add(Calendar.HOUR, -24);
        log.debug("maxDate={}", maxDate);
        allDataCriteria.add(new PropertyCriterion(allDataCriteria.proto().created(), Restriction.LESS_THAN, maxDate.getTime()));

        List<String> dataKeys = PersistenceServicesFactory.getPersistenceService().queryKeys(allDataCriteria);
        log.debug("Number of data records found within the timeframe: {}", dataKeys.size());
        log.trace("dataKeys={}", dataKeys);

        int deleted = 0;
        for (String dataKey : dataKeys) {
            EntityQueryCriteria<ApplicationDocument> criteria = EntityQueryCriteria.create(ApplicationDocument.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().dataId(), dataKey));
            ApplicationDocument doc = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);
            if (doc == null) {
                log.debug("CleanOrphanApplicationDocumentDataRecordsJob: Found orphan ApplicationDocumentData record - deleting. id={}", dataKey);
                PersistenceServicesFactory.getPersistenceService().delete(ApplicationDocumentData.class, dataKey);
                deleted++;
            }
        }
        log.info("CleanOrphanApplicationDocumentDataRecordsJob: FINISHED. {} applicationDocumentData record(s) deleted", deleted);
    }
}
