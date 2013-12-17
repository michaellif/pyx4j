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

import java.util.Date;
import java.util.List;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.blob.IdentificationDocumentBlob;

public class CleanupPmcProcessTest extends CleanupPmcProcessTestBase {

    public void testCleanupOrphanApplicationDocuments() {

        // SET UP
        // 5 orphan blobs inside the target cleanup date range, 3 outside the target range
        // 2 not orphan inside the cleanup range
        mockupConfig.setCleanupOrphanApplicationDocumentsDateRangeMin("01-May-1999");
        mockupConfig.setCleanupOrphanApplicationDocumentsDateRangeMax("05-May-1999");

        createTestBlob("01-May-1999", "orphan 1");
        createTestBlob("01-May-1999", "orphan 2");
        createTestBlob("02-May-1999", "orphan 3");
        createTestBlob("02-May-1999", "orphan 4");
        createTestBlob("05-May-1999", "orphan 5");

        createTestBlob("28-Feb-1999", "orphan out 1");
        createTestBlob("06-May-1999", "orphan out 2");
        createTestBlob("10-May-1999", "orphan out 3");

        createApplicationDocument("in cleanup date doc",//@formatter:off
                createTestBlob("01-May-1999", "child 1").getPrimaryKey(),
                createTestBlob("03-May-1999", "child 2").getPrimaryKey()
        );//@formatter:on

        // RUN CLEANUP
        PmcProcessContext context = new PmcProcessContext(new Date());
        cleanupProcessInstance.executePmcJob(context);

        // TEST
        {
            EntityQueryCriteria<IdentificationDocumentBlob> criteria = EntityQueryCriteria.create(IdentificationDocumentBlob.class);
            criteria.add(PropertyCriterion.ge(criteria.proto().created(), mockupConfig.cleanupOrphanApplicationDocumentsDateRangeMin()));
            criteria.add(PropertyCriterion.le(criteria.proto().created(), mockupConfig.cleanupOrphanApplicationDocumentsDateRangeMax()));

            List<IdentificationDocumentBlob> blobs = Persistence.service().query(criteria);

            // assert no orphan blobs in the cleanup range
            for (IdentificationDocumentBlob blob : blobs) {
                assertFalse("unexpected orphan document blob in the target cleanup range " + blob.toString(),
                        new String(blob.data().getValue()).startsWith("orphan"));
            }

            // assert all the required not orphan blobs are still in the cleanup range
            boolean child1Found = false;
            boolean child2Found = false;
            for (IdentificationDocumentBlob blob : blobs) {
                if (new String(blob.data().getValue()).equals("child 1")) {
                    child1Found = true;
                }
                if (new String(blob.data().getValue()).equals("child 2")) {
                    child2Found = true;
                }
            }
            assertTrue("child orphans were not found", child1Found & child2Found);
        }

        {
            EntityQueryCriteria<IdentificationDocumentBlob> criteria = EntityQueryCriteria.create(IdentificationDocumentBlob.class);
            criteria.or() //@formatter:off
                .left(PropertyCriterion.lt(criteria.proto().created(), mockupConfig.cleanupOrphanApplicationDocumentsDateRangeMin()))
                .right(PropertyCriterion.gt(criteria.proto().created(), mockupConfig.cleanupOrphanApplicationDocumentsDateRangeMax()));
            //@formatter:on

            List<IdentificationDocumentBlob> blobs = Persistence.service().query(criteria);

            // assert orphans that should have been deleted are still alive
            assertEquals("number of expected orphan documents does not match", 3, blobs.size());
        }

    }

}
