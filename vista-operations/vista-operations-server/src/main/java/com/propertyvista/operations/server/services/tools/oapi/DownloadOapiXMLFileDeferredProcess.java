/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 4, 2014
 * @author ernestog
 * @version $Id$
 */
package com.propertyvista.operations.server.services.tools.oapi;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.rpc.report.DeferredReportProcessProgressResponse;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.essentials.server.download.MimeMap;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;
import com.pyx4j.gwt.shared.DownloadFormat;

import com.propertyvista.oapi.xml.ElementIO;
import com.propertyvista.operations.domain.imports.OapiConversion;
import com.propertyvista.operations.rpc.services.tools.oapi.OapiXMLFileDownloadService;

@SuppressWarnings("serial")
public class DownloadOapiXMLFileDeferredProcess extends AbstractDeferredProcess {
    private volatile int progress;

    private volatile int maximum;

    private String fileName;

    private final EntityQueryCriteria<OapiConversion> criteria;

    private final HashMap<String, Serializable> params;

    public DownloadOapiXMLFileDeferredProcess(EntityQueryCriteria<OapiConversion> criteria, HashMap<String, Serializable> params) {
        this.criteria = criteria;
        this.params = params;
    }

    @Override
    public void execute() {

        final EntityQueryCriteria<OapiConversion> oapiConversionCriteria = EntityQueryCriteria.create(OapiConversion.class);
        oapiConversionCriteria.eq(oapiConversionCriteria.proto().id(), params.get(OapiXMLFileDownloadService.OAPIExportDownloadDTOPKParameter));
        OapiConversion OAPIObj = Persistence.service().retrieve(oapiConversionCriteria);

        // TODO For now set default conversion to Base
        OAPIObj.type().setValue(OapiConversion.Type.Base);

        ConverterToOAPI OAPIConverter = ConverterToOAPIFactory.create(OAPIObj);

        Downloadable d = new Downloadable(getXMLBytes(OAPIConverter.process(OAPIObj)), MimeMap.getContentType(DownloadFormat.XML));
        fileName = "OapiXMLFile.xml";
        d.save(fileName);
        completed = true;
    }

	// TODO For now here. Consider moving to OAPI Interface project
    private byte[] getXMLBytes(ElementIO elementIO) {
        byte[] result = null;
        StringWriter sw = new StringWriter();
        try {
            JAXBContext context = JAXBContext.newInstance(elementIO.getClass());
            Marshaller m = context.createMarshaller();
            //Set pretty-print XML in JAXB
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            // Write to writer
            m.marshal(elementIO, sw);
            result = sw.toString().getBytes();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    @Override
    public DeferredProcessProgressResponse status() {
        if (completed) {
            DeferredReportProcessProgressResponse r = new DeferredReportProcessProgressResponse();
            r.setCompleted();
            r.setDownloadLink(System.currentTimeMillis() + "/" + fileName);
            return r;
        } else {
            DeferredProcessProgressResponse r = super.status();
            r.setProgress(progress);
            r.setProgressMaximum(maximum);
            return r;
        }
    }

}
