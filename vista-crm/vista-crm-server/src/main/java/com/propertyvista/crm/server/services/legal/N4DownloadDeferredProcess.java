/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 19, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.legal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.rpc.report.DeferredReportProcessProgressResponse;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;

import com.propertyvista.domain.blob.LegalLetterBlob;
import com.propertyvista.domain.legal.n4.N4LegalLetter;

public class N4DownloadDeferredProcess extends AbstractDeferredProcess {

    private static final Logger log = LoggerFactory.getLogger(N4DownloadDeferredProcess.class);

    private static final long serialVersionUID = 1L;

    private final AtomicInteger progress;

    private final Vector<N4LegalLetter> accepted;

    private volatile Throwable error;

    private final int progressMax;

    private volatile String fileName;

    public N4DownloadDeferredProcess(Vector<N4LegalLetter> accepted) {
        this.progress = new AtomicInteger();
        this.progress.set(0);
        this.progressMax = accepted.size();
        this.accepted = accepted;
    }

    @Override
    public void execute() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            Document mergedDoc = new Document();
            PdfCopy copy = new PdfCopy(mergedDoc, bos);
            mergedDoc.open();

            PdfReader reader;
            int numOfPages;
            for (N4LegalLetter n4LegalLetterIdStub : accepted) {
                progress.set(progress.get() + 1);
                N4LegalLetter n4LegalLetter = Persistence.service().retrieve(N4LegalLetter.class, n4LegalLetterIdStub.getPrimaryKey());
                LegalLetterBlob blob = Persistence.service().retrieve(LegalLetterBlob.class, n4LegalLetter.file().blobKey().getValue());

                ByteArrayInputStream blobStream = new ByteArrayInputStream(blob.data().getValue());

                reader = new PdfReader(blobStream);
                numOfPages = reader.getNumberOfPages();

                for (int page = 1; page <= numOfPages; ++page) {
                    copy.addPage(copy.getImportedPage(reader, page));
                }

                copy.freeReader(reader);
                reader.close();
            }
            mergedDoc.close();

            Downloadable generatedN4bundle = new Downloadable(bos.toByteArray(), "application/pdf");
            generatedN4bundle.save(fileName = "" + System.currentTimeMillis() + "-n4bundle.pdf");

        } catch (Throwable caught) {
            log.error("got error while merging N4's", caught);
            error = caught;
        } finally {
            IOUtils.closeQuietly(bos);
            completed = true;
        }
    }

    @Override
    public DeferredProcessProgressResponse status() {
        DeferredReportProcessProgressResponse status = new DeferredReportProcessProgressResponse();
        if (canceled) {
            status.setCanceled();
        } else if (completed) {
            if (error != null) {
                status.setError();
                status.setErrorStatusMessage(error.getMessage());
            } else {
                status.setDownloadLink(System.currentTimeMillis() + "/" + fileName);
            }
            status.setCompleted();

        } else {

            status.setProgress(progress.get());
            status.setProgressMaximum(progressMax);
        }
        return status;
    }

}
