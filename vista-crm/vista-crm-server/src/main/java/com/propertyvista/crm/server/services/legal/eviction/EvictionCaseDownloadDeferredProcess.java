/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 2, 2015
 * @author stanp
 */
package com.propertyvista.crm.server.services.legal.eviction;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.rpc.report.DeferredReportProcessProgressResponse;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;

import com.propertyvista.domain.blob.EvictionDocumentBlob;
import com.propertyvista.domain.eviction.EvictionCase;
import com.propertyvista.domain.eviction.EvictionCaseStatus;
import com.propertyvista.domain.eviction.EvictionDocument;
import com.propertyvista.domain.eviction.EvictionStatusRecord;

public class EvictionCaseDownloadDeferredProcess extends AbstractDeferredProcess {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(EvictionCaseDownloadDeferredProcess.class);

    private final AtomicInteger progress;

    private final int progressMax;

    private final EvictionCase evictionCase;

    private volatile Throwable error;

    private volatile String fileName;

    public EvictionCaseDownloadDeferredProcess(EvictionCase evictionCase) {
        this.evictionCase = evictionCase;
        this.progress = new AtomicInteger(0);
        int max = 0;
        Persistence.ensureRetrieve(evictionCase.history(), AttachLevel.IdOnly);
        for (EvictionCaseStatus status : evictionCase.history()) {
            Persistence.ensureRetrieve(status.statusRecords(), AttachLevel.IdOnly);
            for (EvictionStatusRecord record : status.statusRecords()) {
                Persistence.ensureRetrieve(record.attachments(), AttachLevel.Attached);
                max += record.attachments().size();
            }
        }
        this.progressMax = max;
    }

    @Override
    public void execute() {
        try {
            Persistence.ensureRetrieve(evictionCase.lease().unit().building(), AttachLevel.Attached);
            String zipName = "EvictionCase-" + evictionCase.lease().unit().building().propertyCode().getValue() + "-"
                    + evictionCase.lease().unit().getStringView() + "-" + System.currentTimeMillis() + ".zip";
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ZipOutputStream zip = new ZipOutputStream(out);
            for (EvictionCaseStatus status : evictionCase.history()) {
                for (EvictionStatusRecord record : status.statusRecords()) {
                    for (EvictionDocument form : record.attachments()) {
                        EvictionDocumentBlob blob = Persistence.service().retrieve(EvictionDocumentBlob.class, form.file().blobKey().getValue());
                        zip.putNextEntry(new ZipEntry(form.file().fileName().getValue()));
                        zip.write(blob.data().getValue());
                        progress.incrementAndGet();
                    }
                }
            }
            zip.close();
            Downloadable generatedN4bundle = new Downloadable(out.toByteArray(), "application/zip");
            generatedN4bundle.save(zipName);
            fileName = URLEncoder.encode(zipName, "UTF-8");
        } catch (Throwable caught) {
            log.error("Unable to archive", caught);
            error = caught;
        } finally {
            completed = true;
        }
    }

    @Override
    protected DeferredProcessProgressResponse createProgressResponse() {
        return new DeferredReportProcessProgressResponse();
    }

    @Override
    protected DeferredProcessProgressResponse updateProgressResponse(DeferredProcessProgressResponse r) {
        DeferredReportProcessProgressResponse rRep = (DeferredReportProcessProgressResponse) super.updateProgressResponse(r);
        if (!rRep.isCompleted() && !rRep.isCanceled()) {
            rRep.setProgress(progress.get());
            rRep.setProgressMaximum(progressMax);
        } else if (error != null) {
            rRep.setMessage(error.getMessage());
        }
        if (fileName != null) {
            rRep.setDownloadLink(System.currentTimeMillis() + "/" + fileName);
        }
        return rRep;
    }
}
