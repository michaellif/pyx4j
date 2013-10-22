/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-18
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.customer;

import java.util.List;

import org.apache.commons.io.FilenameUtils;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.gwt.shared.DownloadFormat;

import com.propertyvista.interfaces.importer.model.PadFileModel;
import com.propertyvista.interfaces.importer.pad.TenantPadCreateReport;
import com.propertyvista.interfaces.importer.pad.TenantPadParser;
import com.propertyvista.interfaces.importer.pad.TenantPadProcessor;
import com.propertyvista.server.common.upload.AbstractUploadWithDownloadableResponceDeferredProcess;

public class TenantPadFileUploadDeferredProcess extends AbstractUploadWithDownloadableResponceDeferredProcess<IEntity> {

    private static final long serialVersionUID = 1L;

    public TenantPadFileUploadDeferredProcess(IEntity data) {
        super(data);
    }

    @Override
    public void execute() {
        List<PadFileModel> model = new TenantPadParser().parsePads(getBinaryData(),
                DownloadFormat.valueByExtension(FilenameUtils.getExtension(getResponse().fileName().getValue())));
        getResponse().message().setValue(new TenantPadProcessor().process(model));

        TenantPadCreateReport report = new TenantPadCreateReport();
        report.createReport(model);

        String fileName = FilenameUtils.getBaseName(getResponse().fileName().getValue()) + "_processingResults.xlsx";
        report.createDownloadable(fileName);

        getResponse().success().setValue(Boolean.TRUE);
        getResponse().resultUrl().setValue(fileName);

        status().setCompleted();
    }
}
