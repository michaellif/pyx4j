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
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.report.JasperFileFormat;
import com.pyx4j.entity.report.JasperReportModel;
import com.pyx4j.entity.report.JasperReportProcessor;
import com.pyx4j.entity.report.master.MasterReportEntry;
import com.pyx4j.entity.report.master.MasterReportModel;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.essentials.rpc.report.DeferredReportProcessProgressResponse;
import com.pyx4j.essentials.server.deferred.IDeferredProcess;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.DashboardMetadata.LayoutType;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;

public class ReportsDeferredProcess implements IDeferredProcess {

    private static final Logger log = LoggerFactory.getLogger(ReportsDeferredProcess.class);

    private final static I18n i18n = I18n.get(ReportsDeferredProcess.class);

    private static final long serialVersionUID = -9137768646648453119L;

    public static final String MIME_PDF_TYPE = "application/pdf";

    private volatile boolean isDone = false;

    private volatile boolean isFailed = false;

    private volatile int progress = 0;

    private volatile Throwable failureReasonForClient = null;

    private volatile String fileName = null;

    private final EntityQueryCriteria<?> queryCriteria;

    private final JasperFileFormat format;

    private final Vector<Key> selectedBuildigns;

    public ReportsDeferredProcess(EntityQueryCriteria<DashboardMetadata> queryCriteria, Vector<Key> selectedBuildings, JasperFileFormat format) {
        this.queryCriteria = queryCriteria;
        this.format = format;
        if (selectedBuildings == null) {
            this.selectedBuildigns = new Vector<Key>();
        } else {
            this.selectedBuildigns = selectedBuildings;
        }
    }

    @Override
    public void execute() {
        // TODO somehow make report generation process "abortable" and monitorable (progress) 
        ByteArrayOutputStream bos = null;
        try {
            DashboardMetadata dashboard = (DashboardMetadata) Persistence.service().retrieve(queryCriteria);
            if (dashboard != null) {

                HashMap<String, Object> masterReportParameters = new HashMap<String, Object>();
                masterReportParameters.put(MasterReportModel.REPORT_TITLE, dashboard.name().getValue());

                MasterReportModel masterReportModel = null;
                if (dashboard.layoutType().getValue() == LayoutType.Report) {
                    masterReportModel = new MasterReportModel(prepareSubreportsForReportLayout(dashboard.gadgets(), selectedBuildigns), masterReportParameters);
                } else {
                    masterReportModel = new MasterReportModel(prepareSubreportsForDashboardLayout(dashboard.gadgets(), selectedBuildigns),
                            masterReportParameters);
                }

                bos = new ByteArrayOutputStream();
                JasperReportProcessor.createReport(masterReportModel, JasperFileFormat.PDF, bos);
                bos.flush();

                Downloadable report = new Downloadable(bos.toByteArray(), asMimeType(format));
                report.save(fileName = metadata2fileName(dashboard, format));
            } else {
                throw new Error("Report Metadata was not found");
            }

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

    public static JasperReportModel createReportModel(GadgetMetadata gadgetMetadata, Vector<Key> selectedBuildings) throws Exception {
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

    public static List<MasterReportEntry> prepareSubreportsForDashboardLayout(IList<GadgetMetadata> gadgets, Vector<Key> selectedBuildigns) throws Exception {
        List<MasterReportEntry> subreports = new LinkedList<MasterReportEntry>();
        for (GadgetMetadata gadgetMetadata : gadgets) {
            subreports.add(new MasterReportEntry(createReportModel(gadgetMetadata, selectedBuildigns)));
        }
        return subreports;
    }

    public static List<MasterReportEntry> prepareSubreportsForReportLayout(List<GadgetMetadata> gadgetMetadatas, Vector<Key> selectedBuildigns)
            throws Exception {
        List<MasterReportEntry> subreports = new LinkedList<MasterReportEntry>();

        GadgetMetadata leftGadgetMetadata = null;
        for (GadgetMetadata gadgetMetadata : gadgetMetadatas) {
            switch (gadgetMetadata.docking().column().getValue()) {
            case -1:
                if (leftGadgetMetadata != null) {
                    subreports.add(new MasterReportEntry(createReportModel(leftGadgetMetadata, selectedBuildigns), null));
                }
                subreports.add(new MasterReportEntry(createReportModel(gadgetMetadata, selectedBuildigns)));
                leftGadgetMetadata = null;
                break;
            case 0:
                if (leftGadgetMetadata != null) {
                    subreports.add(new MasterReportEntry(createReportModel(gadgetMetadata, selectedBuildigns)));
                }
                leftGadgetMetadata = gadgetMetadata;
                break;
            case 1:
                subreports.add(new MasterReportEntry(createReportModel(leftGadgetMetadata, selectedBuildigns), createReportModel(gadgetMetadata,
                        selectedBuildigns)));
                leftGadgetMetadata = null;
                break;
            default:
                throw new Exception(SimpleMessageFormat.format("invalid docking column value {0} for gadget {1} (id() =  {2})",

                gadgetMetadata.docking().column().getValue(),

                gadgetMetadata.getEntityMeta().getCaption(),

                gadgetMetadata.id().getValue()));
            }
        }
        if (leftGadgetMetadata != null) {
            subreports.add(new MasterReportEntry(createReportModel(leftGadgetMetadata, selectedBuildigns), null));
        }

        return subreports;
    }

    private static String metadata2fileName(DashboardMetadata metadata, JasperFileFormat format) {
        return metadata.name().getValue().replaceAll(" ", "-") + "." + format.toString().toLowerCase();
    }

    private static String asMimeType(JasperFileFormat format) throws Exception {
        switch (format) {
        case PDF:
            return MIME_PDF_TYPE;
        default:
            throw new Exception("unsupported jasper file format (currently can hanle only " + JasperFileFormat.PDF + ")");
        }
    }

}
