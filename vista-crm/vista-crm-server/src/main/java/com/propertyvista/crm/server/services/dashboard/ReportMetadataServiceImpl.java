/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-28
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.dashboard;

import java.io.ByteArrayOutputStream;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.report.JasperFileFormat;
import com.pyx4j.entity.report.JasperReportProcessor;
import com.pyx4j.entity.rpc.EntityCriteriaByPK;
import com.pyx4j.entity.server.EntityServicesImpl;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion.Restriction;
import com.pyx4j.essentials.rpc.report.DownloadFormat;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.crm.rpc.services.dashboard.ReportMetadataService;
import com.propertyvista.crm.server.report.ReportReport;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.portal.rpc.ptapp.ServletMapping;

public class ReportMetadataServiceImpl extends AbstractMetadataServiceImpl implements ReportMetadataService {

    public ReportMetadataServiceImpl() {
        super();
    }

    @Override
    void addTypeCriteria(EntityQueryCriteria<DashboardMetadata> criteria) {
        criteria.add(new PropertyCriterion(criteria.proto().layoutType(), Restriction.EQUAL, DashboardMetadata.LayoutType.Report));
    }

    @Override
    public void downloadBoard(final AsyncCallback<String> callback, VoidSerializable none, Key entityId) {
        DashboardMetadata result = EntityServicesImpl.secureRetrieve(EntityCriteriaByPK.create(DashboardMetadata.class, entityId));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            JasperReportProcessor.createReport(ReportReport.createModel(result), JasperFileFormat.PDF, bos);
            Downloadable d = new Downloadable(bos.toByteArray(), Downloadable.getContentType(DownloadFormat.PDF));
            String fileName = "Report.pdf";
            d.save(fileName);
            callback.onSuccess(ServletMapping.REPORTS_DOWNLOAD + "/" + System.currentTimeMillis() + "/" + fileName);
        } finally {
            IOUtils.closeQuietly(bos);
        }
    }
}
