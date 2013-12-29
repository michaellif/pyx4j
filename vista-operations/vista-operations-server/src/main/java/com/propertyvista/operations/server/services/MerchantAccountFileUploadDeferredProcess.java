/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-26
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.server.services;

import java.util.List;

import org.apache.commons.io.FilenameUtils;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.essentials.server.report.EntityReportFormatter;
import com.pyx4j.essentials.server.report.ReportTableFormatter;
import com.pyx4j.essentials.server.report.ReportTableXLSXFormatter;
import com.pyx4j.gwt.shared.DownloadFormat;

import com.propertyvista.interfaces.importer.model.MerchantAccountFileModel;
import com.propertyvista.interfaces.importer.parser.MerchantAccountParser;
import com.propertyvista.interfaces.importer.processor.MerchantAccountProcessor;
import com.propertyvista.server.common.upload.AbstractUploadWithDownloadableResponceDeferredProcess;

public class MerchantAccountFileUploadDeferredProcess extends AbstractUploadWithDownloadableResponceDeferredProcess<IEntity> {

    private static final long serialVersionUID = 1L;

    public MerchantAccountFileUploadDeferredProcess(IEntity data) {
        super(data);
    }

    @Override
    public void execute() {
        List<MerchantAccountFileModel> model = new MerchantAccountParser().parseFile(getBinaryData(),
                DownloadFormat.valueByExtension(FilenameUtils.getExtension(getResponse().fileName().getValue())));

        getResponse().message().setValue(new MerchantAccountProcessor().persistMerchantAccounts(model));

        String fileName = FilenameUtils.getBaseName(getResponse().fileName().getValue()) + "_processingResults.xlsx";

        ReportTableFormatter formatter = new ReportTableXLSXFormatter(true);
        EntityReportFormatter<MerchantAccountFileModel> entityFormatter = new EntityReportFormatter<MerchantAccountFileModel>(MerchantAccountFileModel.class);
        entityFormatter.createHeader(formatter);
        entityFormatter.reportAll(formatter, model);

        Downloadable d = new Downloadable(formatter.getBinaryData(), formatter.getContentType());
        d.save(fileName);

        getResponse().success().setValue(Boolean.TRUE);
        getResponse().resultUrl().setValue(fileName);

        status().setCompleted();
    }
}
