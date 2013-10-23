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
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;

import com.propertyvista.domain.legal.N4LegalLetter;
import com.propertyvista.server.domain.LegalLetterBlob;

public class N4DownloadDeferredProcess extends AbstractDeferredProcess {

    private static final Logger log = LoggerFactory.getLogger(N4DownloadDeferredProcess.class);

    private static final long serialVersionUID = 1L;

    private final AtomicInteger progress;

    private final Vector<N4LegalLetter> accepted;

    private volatile Throwable error;

    private final int progressMax;

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
            for (N4LegalLetter n4LegalLetter : accepted) {
                progress.set(progress.get() + 1);

                LegalLetterBlob blob = Persistence.service().retrieve(LegalLetterBlob.class, n4LegalLetter.blobKey().getValue());

                ByteArrayInputStream blobStream = new ByteArrayInputStream(blob.content().getValue());

                reader = new PdfReader(blobStream);
                numOfPages = reader.getNumberOfPages();

                for (int page = 1; page <= numOfPages; ++page) {
                    copy.addPage(copy.getImportedPage(reader, page));
                }

                copy.freeReader(reader);
                reader.close();
            }
            mergedDoc.close();

            bos.toByteArray();
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
        DeferredProcessProgressResponse status = super.status();
        status.setProgress(progress.get());
        status.setProgressMaximum(progressMax);
        if (error != null) {
            status.setError();
            status.setErrorStatusMessage(error.getMessage());
        }
        return status;
    }

}
