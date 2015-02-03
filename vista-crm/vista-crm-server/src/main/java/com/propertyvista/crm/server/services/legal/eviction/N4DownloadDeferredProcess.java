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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.rpc.report.DeferredReportProcessProgressResponse;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;

import com.propertyvista.domain.blob.EvictionDocumentBlob;
import com.propertyvista.domain.eviction.EvictionDocument;
import com.propertyvista.domain.eviction.EvictionStatusN4;
import com.propertyvista.domain.legal.n4.N4Batch;

public class N4DownloadDeferredProcess extends AbstractDeferredProcess {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(N4DownloadDeferredProcess.class);

    private final AtomicInteger progress;

    private final int progressMax;

    private final Collection<N4Batch> batches;

    private volatile Throwable error;

    private volatile String fileName;

    public N4DownloadDeferredProcess(Collection<N4Batch> batches) {
        this.batches = batches;
        this.progress = new AtomicInteger(0);
        int max = 0;
        for (N4Batch batch : batches) {
            max += batch.items().size();
        }
        this.progressMax = max;
    }

    @Override
    public void execute() {
        Map<String, ByteArrayOutputStream> formFiles = new HashMap<>();
        ByteArrayOutputStream bos = null;
        try {
            for (N4Batch batch : batches) {
                bos = new ByteArrayOutputStream();
                Document mergedDoc = new Document();
                PdfCopy copy = new PdfCopy(mergedDoc, bos);
                mergedDoc.open();

                PdfReader reader;
                int numOfPages;

                // find forms
                EntityQueryCriteria<EvictionStatusN4> crit = EntityQueryCriteria.create(EvictionStatusN4.class);
                crit.eq(crit.proto().originatingBatch(), batch);
                List<EvictionStatusN4> n4items = Persistence.service().query(crit);

                for (EvictionStatusN4 item : n4items) {
                    Persistence.ensureRetrieve(item.generatedForms(), AttachLevel.Attached);
                    for (EvictionDocument form : item.generatedForms()) {
                        EvictionDocumentBlob blob = Persistence.service().retrieve(EvictionDocumentBlob.class, form.file().blobKey().getValue());
                        ByteArrayInputStream blobStream = new ByteArrayInputStream(blob.data().getValue());

                        reader = new PdfReader(blobStream);
                        numOfPages = reader.getNumberOfPages();
                        for (int page = 1; page <= numOfPages; ++page) {
                            copy.addPage(copy.getImportedPage(reader, page));
                        }
                        copy.freeReader(reader);
                        reader.close();
                    }
                    progress.incrementAndGet();
                }
                mergedDoc.close();

                Persistence.ensureRetrieve(batch.building(), AttachLevel.Attached);
                String fileName = batch.building().propertyCode().getValue() + "-n4forms-" + System.currentTimeMillis() + ".pdf";
                formFiles.put(fileName, bos);
            }
            String fileName = null;
            if (formFiles.size() == 1) {
                // return single pdf
                fileName = formFiles.keySet().iterator().next();
                Downloadable generatedN4bundle = new Downloadable(formFiles.get(fileName).toByteArray(), "application/pdf");
                generatedN4bundle.save(fileName);
            } else if (formFiles.size() > 1) {
                // return zip archive
                fileName = createZipArchive(formFiles);
            }
            this.fileName = URLEncoder.encode(fileName, "UTF-8");
        } catch (Throwable caught) {
            log.error("got error while merging N4's", caught);
            error = caught;
        } finally {
            IOUtils.closeQuietly(bos);
            completed = true;
        }
    }

    private String createZipArchive(Map<String, ByteArrayOutputStream> formFiles) {
        try {
            String zipName = "n4bundle-" + System.currentTimeMillis() + ".zip";
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ZipOutputStream zip = new ZipOutputStream(out);
            for (String pdf : formFiles.keySet()) {
                zip.putNextEntry(new ZipEntry(pdf));
                zip.write(formFiles.get(pdf).toByteArray());
            }
            zip.close();
            Downloadable generatedN4bundle = new Downloadable(out.toByteArray(), "application/zip");
            generatedN4bundle.save(zipName);
            return zipName;
        } catch (Throwable t) {
            log.error("N4 Zip archive generation failed", t);
            return null;
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
