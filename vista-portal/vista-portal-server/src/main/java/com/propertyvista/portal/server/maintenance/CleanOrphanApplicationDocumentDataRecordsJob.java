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
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
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

import com.propertyvista.portal.domain.pt.ApplicationDocument;
import com.propertyvista.server.domain.ApplicationDocumentData;

import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion.Restriction;

public class CleanOrphanApplicationDocumentDataRecordsJob implements Job {

    private final static Logger logger = LoggerFactory.getLogger(CleanOrphanApplicationDocumentDataRecordsJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.info("CleanOrphanApplicationDocumentDataRecordsJob: STARTED");
        EntityQueryCriteria<ApplicationDocumentData> allDataCriteria = EntityQueryCriteria.create(ApplicationDocumentData.class);
        Calendar minDate = new GregorianCalendar();
        minDate.add(Calendar.DATE, -7);
        allDataCriteria.add(new PropertyCriterion(allDataCriteria.proto().created(), Restriction.GREATER_THAN, minDate.getTime()));
        Calendar maxDate = new GregorianCalendar();
        maxDate.add(Calendar.HOUR, -24);
        allDataCriteria.add(new PropertyCriterion(allDataCriteria.proto().created(), Restriction.LESS_THAN, maxDate.getTime()));
        List<Long> dataKeys = PersistenceServicesFactory.getPersistenceService().queryKeys(allDataCriteria);
        logger.trace("dataKeys={}", dataKeys);
        int deleted = 0;
        for (Long dataKey : dataKeys) {
            EntityQueryCriteria<ApplicationDocument> criteria = EntityQueryCriteria.create(ApplicationDocument.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().dataId(), dataKey));
            ApplicationDocument doc = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);
            if (doc == null) {
                logger.debug("CleanOrphanApplicationDocumentDataRecordsJob: Found orphan ApplicationDocumentData record - deleting. id={}", dataKey);
                PersistenceServicesFactory.getPersistenceService().delete(ApplicationDocumentData.class, dataKey);
                deleted++;
            }
        }
        logger.info("CleanOrphanApplicationDocumentDataRecordsJob: {} ApplicationDocumentData record(s) deleted", deleted);
    }
}
