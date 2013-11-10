/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2012
 * @author Artyom
 * @version $Id$
 */
package com.propertyvista.crm.server.services.reports;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.report.JasperFileFormat;
import com.pyx4j.entity.report.JasperReportModel;
import com.pyx4j.entity.report.JasperReportProcessor;
import com.pyx4j.entity.report.master.MasterReportEntry;
import com.pyx4j.entity.report.master.MasterReportModel;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.rpc.report.DeferredReportProcessProgressResponse;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.gwt.server.deferred.IDeferredProcess;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.DashboardMetadata.DashboardType;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;
import com.propertyvista.domain.property.asset.building.Building;

public class ReportsDeferredProcess implements IDeferredProcess {

    public static final String MIME_PDF_TYPE = "application/pdf";

    private static final long serialVersionUID = -9137768646648453119L;

    private static final Logger log = LoggerFactory.getLogger(ReportsDeferredProcess.class);

    private static final I18n i18n = I18n.get(ReportsDeferredProcess.class);

    private volatile boolean isDone = false;

    private volatile boolean isFailed = false;

    private volatile int progress = 0;

    private volatile Throwable failureReasonForClient = null;

    private volatile String fileName = null;

    private final EntityQueryCriteria<?> queryCriteria;

    private final Vector<Building> selectedBuildigns;

    private final JasperFileFormat format;

    public ReportsDeferredProcess(EntityQueryCriteria<DashboardMetadata> queryCriteria, Vector<Building> selectedBuildings, JasperFileFormat format) {
        this.queryCriteria = queryCriteria;
        this.format = format;
        if (selectedBuildings == null) {
            this.selectedBuildigns = new Vector<Building>();
        } else {
            this.selectedBuildigns = selectedBuildings;
        }
    }

    @Override
    public void execute() {
        // TODO somehow make report generation process "abortable" and monitorable (progress) 
        ByteArrayOutputStream bos = null;
        try {
            Persistence.service().startBackgroundProcessTransaction();

            DashboardMetadata dashboard = (DashboardMetadata) Persistence.service().retrieve(queryCriteria);
            if (dashboard != null) {

                MasterReportModel masterReportModel = null;
                HashMap<String, Object> masterReportParameters = new HashMap<String, Object>();

                if (false) {
                    masterReportParameters.put(MasterReportModel.REPORT_TITLE, dashboard.name().getValue());
                    masterReportModel = new MasterReportModel(prepareSubreportsForReportLayout(dashboard.gadgetMetadataList(), selectedBuildigns),
                            masterReportParameters);
                } else {
                    String title;
                    if (dashboard.type().getValue() == DashboardType.building) {
                        if (selectedBuildigns.isEmpty()) {
                            title = i18n.tr("{0} for all buildings", dashboard.name().getValue());
                        } else {
                            title = i18n.tr("{0} for {1}", dashboard.name().getValue(), buildingsLabel());
                        }
                    } else {
                        title = dashboard.name().getValue();
                    }
                    masterReportParameters.put(MasterReportModel.REPORT_TITLE, title);
                    masterReportModel = new MasterReportModel(prepareSubreportsForDashboardLayout(dashboard.gadgetMetadataList(), selectedBuildigns),
                            masterReportParameters);
                }

                bos = new ByteArrayOutputStream();
                JasperReportProcessor.createReport(masterReportModel, JasperFileFormat.PDF, bos);
                bos.flush();

                Downloadable report = new Downloadable(bos.toByteArray(), asMimeType(format));
                report.save(fileName = asFileName(dashboard, format));
            } else {
                throw new Error("Report Metadata was not found");
            }

            Persistence.service().endTransaction();
        } catch (Throwable error) {
            failureReasonForClient = error;
            isFailed = true;
            log.error("Report generation failed", failureReasonForClient);
        } finally {
            IOUtils.closeQuietly(bos);
            isDone = true;
        }

    }

    @Override
    public void cancel() {
        // TODO abort report generation
    }

    @Override
    public DeferredProcessProgressResponse status() {
        DeferredReportProcessProgressResponse response = new DeferredReportProcessProgressResponse();

        if (isDone) {
            if (!isFailed) {
                response.setCompleted();
                response.setDownloadLink(System.currentTimeMillis() + "/" + fileName);
            } else {
                response.setError();
                response.setErrorStatusMessage(failureReasonForClient.getMessage());
            }
        } else {
            response.setMessage(i18n.tr("Creating Report..."));
            response.setProgress(progress);
        }
        return response;
    }

    private String buildingsLabel() {
        Iterator<String> buildingLabelIterator = new Iterator<String>() {
            Iterator<Building> buildingsI = selectedBuildigns.iterator();

            @Override
            public boolean hasNext() {
                return buildingsI.hasNext();
            }

            @Override
            public String next() {
                return Persistence.service().retrieve(Building.class, buildingsI.next().getPrimaryKey(), AttachLevel.Attached, false).propertyCode().getValue();
            }

            @Override
            public void remove() {
                buildingsI.remove();
            }
        };
        return StringUtils.join(buildingLabelIterator, ", ");
    }

    static JasperReportModel createSubReportModel(GadgetMetadata gadgetMetadata, Vector<Building> selectedBuildings) throws Exception {
        final Boolean[] isFinished = new Boolean[] { false };
        final JasperReportModel[] result = new JasperReportModel[] { null };
        final Throwable[] error = new Throwable[] { null };

        if (gadgetMetadata == null) {
            return null;
        } else {
            ReportModelCreatorDispatcher.instance().createReportModel(new AsyncCallback<JasperReportModel>() {
                @Override
                public void onFailure(Throwable caught) {
                    error[0] = caught;
                    synchronized (isFinished) {
                        isFinished[0] = true;
                        isFinished.notify();
                    }
                }

                @Override
                public void onSuccess(JasperReportModel model) {
                    result[0] = model;
                    synchronized (isFinished) {
                        isFinished[0] = true;
                        isFinished.notify();
                    }
                }
            }, gadgetMetadata, selectedBuildings);

            boolean isInterrupted = false;
            synchronized (isFinished) {
                while (!isFinished[0] & !isInterrupted) {
                    try {
                        isFinished.wait();
                    } catch (InterruptedException e) {
                        isInterrupted = true;
                    }
                }
            }
            if (result[0] != null) {
                return result[0];
            } else {
                throw new Exception("the creation of report model has failed", error[0]);
            }
        }
    }

    static List<MasterReportEntry> prepareSubreportsForDashboardLayout(IList<GadgetMetadata> gadgets, Vector<Building> selectedBuildigns) throws Exception {
        List<MasterReportEntry> subreports = new LinkedList<MasterReportEntry>();
        for (GadgetMetadata gadgetMetadata : gadgets) {
            subreports.add(new MasterReportEntry(createSubReportModel(gadgetMetadata, selectedBuildigns)));
        }
        return subreports;
    }

    static List<MasterReportEntry> prepareSubreportsForReportLayout(List<GadgetMetadata> gadgetMetadatas, Vector<Building> selectedBuildigns) throws Exception {
        List<MasterReportEntry> subreports = new LinkedList<MasterReportEntry>();

        GadgetMetadata leftGadgetMetadata = null;
        for (GadgetMetadata gadgetMetadata : gadgetMetadatas) {
            switch (-1) {
            case -1:
                if (leftGadgetMetadata != null) {
                    subreports.add(new MasterReportEntry(createSubReportModel(leftGadgetMetadata, selectedBuildigns), null));
                }
                subreports.add(new MasterReportEntry(createSubReportModel(gadgetMetadata, selectedBuildigns)));
                leftGadgetMetadata = null;
                break;
            case 0:
                if (leftGadgetMetadata != null) {
                    subreports.add(new MasterReportEntry(createSubReportModel(gadgetMetadata, selectedBuildigns)));
                }
                leftGadgetMetadata = gadgetMetadata;
                break;
            case 1:
                subreports.add(new MasterReportEntry(createSubReportModel(leftGadgetMetadata, selectedBuildigns), createSubReportModel(gadgetMetadata,
                        selectedBuildigns)));
                leftGadgetMetadata = null;
                break;
            default:

            }
        }
        if (leftGadgetMetadata != null) {
            subreports.add(new MasterReportEntry(createSubReportModel(leftGadgetMetadata, selectedBuildigns), null));
        }

        return subreports;
    }

    private static String asFileName(DashboardMetadata metadata, JasperFileFormat format) {
        return metadata.name().getValue().replaceAll(" ", "-") + "." + format.toString().toLowerCase();
    }

    private static String asMimeType(JasperFileFormat format) throws Exception {
        switch (format) {
        case PDF:
            return MIME_PDF_TYPE;
        default:
            throw new Exception("unsupported jasper file format (currently this process knows to hanle only " + JasperFileFormat.PDF + ")");
        }
    }

}
