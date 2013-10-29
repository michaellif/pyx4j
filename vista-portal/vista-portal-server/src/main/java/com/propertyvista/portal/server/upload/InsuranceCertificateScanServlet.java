/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 27, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.server.upload;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.tenant.insurance.InsuranceCertificateScan;
import com.propertyvista.server.domain.GeneralInsurancePolicyBlob;

public class InsuranceCertificateScanServlet extends HttpServlet {

    private static final long serialVersionUID = 8634064043981878083L;

    private static final Logger log = LoggerFactory.getLogger(PmcDocumentServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            InsuranceCertificateScan docFile = Persistence.secureRetrieve(InsuranceCertificateScan.class, parseKey(request.getPathInfo()));
            if (docFile == null) {
                throw new FileNotFoundException("document file was not found");
            }

            GeneralInsurancePolicyBlob blob = Persistence.service().retrieve(GeneralInsurancePolicyBlob.class, docFile.blobKey().getValue());
            if (blob == null) {
                throw new FileNotFoundException("document blob was not found");
            }

            response.setContentType(blob.contentType().getValue());
            response.getOutputStream().write(blob.content().getValue());

        } catch (ParseException e) {
            response.setStatus(HttpServletResponse.SC_GONE);
        } catch (FileNotFoundException e) {
            log.debug("failed to provide document from ''{}'': {}", request.getPathInfo(), e.getMessage());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private Key parseKey(String filePath) throws ParseException {
        if (filePath == null) {
            throw new ParseException("invalid file id", 0);
        }
        String key = FilenameUtils.getPathNoEndSeparator(filePath);
        if (CommonsStringUtils.isEmpty(key) || "0".equals(key)) {
            throw new ParseException("invalid file id", 0);
        }
        return new Key(key);
    }

}