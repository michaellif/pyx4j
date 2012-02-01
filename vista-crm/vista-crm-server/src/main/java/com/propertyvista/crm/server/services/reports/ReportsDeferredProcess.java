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
import java.util.ArrayList;
import java.util.List;


import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;
import com.pyx4j.entity.report.JasperFileFormat;
import com.pyx4j.entity.report.JasperReportModel;
import com.pyx4j.entity.report.JasperReportProcessor;
import com.pyx4j.entity.report.master.MasterReportEntry;
import com.pyx4j.entity.report.master.MasterReportModel;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.essentials.rpc.report.DeferredReportProcessProgressResponse;
import com.pyx4j.essentials.server.deferred.IDeferredProcess;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.gwt.server.IOUtils;


public class ReportsDeferredProcess implements IDeferredProcess {
	
	private static final long serialVersionUID = -9137768646648453119L;

	public static final String MIME_PDF_TYPE = "application/pdf";
	
	private volatile boolean isDone = false;
	
	private volatile boolean isFailed = false;
	
	private volatile Throwable failureReason = null;
	
	private volatile String fileName = null;
	
	private EntityQueryCriteria<?> queryCriteria;
	
	private JasperFileFormat format;
	
	public ReportsDeferredProcess(EntityQueryCriteria<DashboardMetadata> queryCriteria, JasperFileFormat format) {
		this.queryCriteria = queryCriteria;
		this.format = format;
	}

	@Override
	public void execute() {
		ByteArrayOutputStream bos = null;
		try {
			DashboardMetadata dashboard = (DashboardMetadata) Persistence.service().retrieve(queryCriteria);
			if (dashboard != null) {
				
				MasterReportModel masterReportModel = new MasterReportModel(prepareSubreports(dashboard.gadgets()));
				
				bos = new ByteArrayOutputStream();
				JasperReportProcessor.createReport(masterReportModel, JasperFileFormat.PDF, bos);
				bos.flush();
	
//				byte[] reportAsBytes = "no free lunch".getBytes();	
				
				Downloadable report = new Downloadable(bos.toByteArray(), asMimeType(format));
				report.save(fileName = metadata2fileName(dashboard, format));
			} else {
				throw new Error("Report Metadata was not found");
			}
			
		} catch (Throwable error) {
			isFailed = true;
			failureReason = error;
		} finally {
			IOUtils.closeQuietly(bos);
			isDone = true;
		}
				
    }

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
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
				response.setErrorStatusMessage(failureReason.getMessage());
			}
		} else {
			response.setProgress(0);
		}
		return response;
	}

	public static String metadata2fileName(DashboardMetadata metadata, JasperFileFormat format) {
		return metadata.name().getValue().replaceAll(" ", "-") + "." + format.toString().toLowerCase();
	}
		
	public static JasperReportModel createReportModel(GadgetMetadata gadgetMetadata) {
		if (gadgetMetadata == null) {
			return null;
		} else {
			
			// TODO convert gadgetMetadata to report model
			
			throw new Error("converting gadgetMetadata to reports hasn't been implemented yet");
		}
	}
	
	public static List<MasterReportEntry> prepareSubreports(List<GadgetMetadata> gadgetMetadatas) {
		List<MasterReportEntry> subreports = new ArrayList<MasterReportEntry>();
		
		GadgetMetadata leftGadgetMetadata = null;			
		for (GadgetMetadata gadgetMetadata : gadgetMetadatas) {
			switch(gadgetMetadata.docking().column().getValue()) {
			case -1:
				if (leftGadgetMetadata != null) {
					subreports.add(new MasterReportEntry(createReportModel(leftGadgetMetadata), null));
				}
				subreports.add(new MasterReportEntry(createReportModel(gadgetMetadata)));
				leftGadgetMetadata = null;
				break;
			case 0:
				leftGadgetMetadata = gadgetMetadata;
				break;
			case 1:
				subreports.add(new MasterReportEntry(createReportModel(leftGadgetMetadata), createReportModel(gadgetMetadata)));
				leftGadgetMetadata = null;
				break;
			}
		}
		if (leftGadgetMetadata != null) {
			subreports.add(new MasterReportEntry(createReportModel(leftGadgetMetadata), null));
		}						
		return subreports;
	}
	
	
	// TODO finish jasper format to mime type conversion
	private String asMimeType(JasperFileFormat format) {
		switch (format) {
		case PDF:
			return MIME_PDF_TYPE;
		default:
			return "text/xml";			
		}
	}

}
