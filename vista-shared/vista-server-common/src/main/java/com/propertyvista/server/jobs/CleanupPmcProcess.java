/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 17, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.server.jobs;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.media.ApplicationDocumentFile;
import com.propertyvista.server.domain.ApplicationDocumentBlob;

public class CleanupPmcProcess implements PmcProcess {

    private static final Logger log = LoggerFactory.getLogger(CleanupPmcProcess.class);

    private final CleanupPmcProcessConfig config;

    CleanupPmcProcess(CleanupPmcProcessConfig config) {
        this.config = config;
    }

    public CleanupPmcProcess() {
        this(new CleanupPmcProcessConfig() {

            @Override
            public Date cleanupOrphanApplicationDocumentsDateRangeMin() {
                Calendar minDate = new GregorianCalendar();
                minDate.add(Calendar.DATE, -7);
                return minDate.getTime();
            }

            @Override
            public Date cleanupOrphanApplicationDocumentsDateRangeMax() {
                Calendar maxDate = new GregorianCalendar();
                maxDate.add(Calendar.HOUR, -24);
                return maxDate.getTime();
            }
        });
    }

    @Override
    public boolean start(PmcProcessContext context) {
        log.info("Cleanup PMC Job started");
        return true;
    }

    /**
     * Clean up documents (i.e. scanned IDs) that were uploaded but cancelled later.
     */
    @Override
    public void executePmcJob(PmcProcessContext context) {
        log.info("Cleaunup orphan application documents started");

        EntityQueryCriteria<ApplicationDocumentBlob> allDataCriteria = EntityQueryCriteria.create(ApplicationDocumentBlob.class);
        Date minDate = config.cleanupOrphanApplicationDocumentsDateRangeMin();
        log.debug("minDate={}", minDate);
        allDataCriteria.add(PropertyCriterion.ge(allDataCriteria.proto().created(), minDate));

        Date maxDate = config.cleanupOrphanApplicationDocumentsDateRangeMax();
        log.debug("maxDate={}", maxDate);
        allDataCriteria.add(PropertyCriterion.le(allDataCriteria.proto().created(), maxDate));

        List<Key> dataKeys = Persistence.service().queryKeys(allDataCriteria);
        final int maxProgress = dataKeys.size();
        log.debug("Number of data records found within the timeframe: {}", maxProgress);
        log.trace("dataKeys={}", dataKeys);

        for (Key dataKey : dataKeys) {
            EntityQueryCriteria<ApplicationDocumentFile> criteria = EntityQueryCriteria.create(ApplicationDocumentFile.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().blobKey(), dataKey));
            ApplicationDocumentFile doc = Persistence.service().retrieve(criteria);
            if (doc == null) {
                log.debug("CleanOrphanApplicationDocumentDataRecordsJob: Found orphan ApplicationDocumentData record - deleting. id={}", dataKey);
                Persistence.service().delete(ApplicationDocumentBlob.class, dataKey);
                StatisticsUtils.addProcessed(context.getRunStats(), 1);
            }
        }
        Persistence.service().commit();
        log.info("Cleanup orphan application documents complete");
    }

    interface CleanupPmcProcessConfig {

        Date cleanupOrphanApplicationDocumentsDateRangeMin();

        Date cleanupOrphanApplicationDocumentsDateRangeMax();

    }

    @Override
    public void complete(PmcProcessContext context) {
        log.info("Cleanup PMC Job complete");
    }
}
