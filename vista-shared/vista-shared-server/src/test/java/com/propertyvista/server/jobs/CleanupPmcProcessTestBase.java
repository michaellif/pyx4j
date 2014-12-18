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
 */
package com.propertyvista.server.jobs;

import static com.pyx4j.gwt.server.DateUtils.detectDateformat;

import java.util.Date;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.ConnectionTarget;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;

import com.propertyvista.config.tests.VistaDBTestBase;
import com.propertyvista.domain.blob.IdentificationDocumentBlob;
import com.propertyvista.domain.media.IdentificationDocumentFile;
import com.propertyvista.domain.media.IdentificationDocument;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.CustomerScreening;
import com.propertyvista.server.jobs.CleanupPmcProcess.CleanupPmcProcessConfig;

public class CleanupPmcProcessTestBase extends VistaDBTestBase {

    protected CleanupPmcProcess cleanupProcessInstance;

    protected MockupCleanupPmcProcessConfig mockupConfig;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Persistence.service().startTransaction(TransactionScopeOption.Suppress, ConnectionTarget.BackgroundProcess);
        mockupConfig = new MockupCleanupPmcProcessConfig();
        cleanupProcessInstance = new CleanupPmcProcess(mockupConfig);
    }

    @Override
    protected void tearDown() throws Exception {
        Persistence.service().endTransaction();
        super.tearDown();
    }

    protected static IdentificationDocumentBlob createTestBlob(String date, String id) {
        IdentificationDocumentBlob blob = EntityFactory.create(IdentificationDocumentBlob.class);
        blob.created().setValue(detectDateformat(date));
        blob.data().setValue(id.getBytes());
        Persistence.service().persist(blob);
        return blob;
    }

    protected static IdentificationDocument createApplicationDocument(String desc, Key... blobs) {
        Customer c = EntityFactory.create(Customer.class);
        Persistence.service().persist(c);

        CustomerScreening cs = EntityFactory.create(CustomerScreening.class);
        cs.screene().set(c);
        Persistence.service().persist(cs);

        IdentificationDocument doc = EntityFactory.create(IdentificationDocument.class);
        doc.notes().setValue(desc);

        for (Key blobKey : blobs) {
            IdentificationDocumentFile file = doc.files().$();
            file.file().blobKey().setValue(blobKey);
            doc.files().add(file);
        }
        doc.owner().set(cs.version());

        Persistence.service().persist(doc);
        return doc;
    }

    protected static class MockupCleanupPmcProcessConfig implements CleanupPmcProcessConfig {

        private Date minCleanunpDocsDate = null;

        private Date maxCleanunpDocsDate = null;

        public void setCleanupOrphanApplicationDocumentsDateRangeMin(String date) {
            this.minCleanunpDocsDate = detectDateformat(date);
        }

        @Override
        public Date cleanupOrphanApplicationDocumentsDateRangeMin() {
            return minCleanunpDocsDate;
        }

        public void setCleanupOrphanApplicationDocumentsDateRangeMax(String date) {
            this.maxCleanunpDocsDate = detectDateformat(date);
        }

        @Override
        public Date cleanupOrphanApplicationDocumentsDateRangeMax() {
            return maxCleanunpDocsDate;
        }

    }

}
