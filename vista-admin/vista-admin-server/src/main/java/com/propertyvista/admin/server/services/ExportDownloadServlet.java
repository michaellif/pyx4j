/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 23, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.server.services;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Consts;
import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.rpc.report.DownloadFormat;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.essentials.server.download.MimeMap;
import com.pyx4j.essentials.server.xml.XMLEntityWriter;
import com.pyx4j.essentials.server.xml.XMLStringWriter;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.security.VistaAdminBehavior;
import com.propertyvista.interfaces.importer.BuildingRetriever;
import com.propertyvista.interfaces.importer.converter.MediaConfig;
import com.propertyvista.interfaces.importer.model.ImportIO;
import com.propertyvista.interfaces.importer.xml.ImportXMLEntityNamingConvention;

@SuppressWarnings("serial")
public class ExportDownloadServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(Downloadable.class);

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        SecurityController.assertBehavior(VistaAdminBehavior.SystemAdmin);

        Persistence.service().startBackgroundProcessTransaction();

        long pmcId = Long.valueOf(request.getParameter("pmc"));
        Pmc pmc = Persistence.service().retrieve(Pmc.class, new Key(pmcId));
        NamespaceManager.setNamespace(pmc.namespace().getValue());

        log.debug("download export for {}", pmc);

        MediaConfig mediaConfig = new MediaConfig();
        mediaConfig.baseFolder = "data/export/images/" + NamespaceManager.getNamespace() + "/";

        if ("false".equals(request.getParameter("images"))) {
            mediaConfig.baseFolder = null;
        }

        byte[] data;
        try {
            ImportIO importIO = EntityFactory.create(ImportIO.class);
            EntityQueryCriteria<Building> buildingCriteria = EntityQueryCriteria.create(Building.class);

            String propertyManager = request.getParameter("propertyManager");
            if (CommonsStringUtils.isStringSet(propertyManager)) {
                buildingCriteria.add(PropertyCriterion.eq(buildingCriteria.proto().propertyManager().name(), propertyManager));
            }
            buildingCriteria.asc(buildingCriteria.proto().propertyCode());

            List<Building> buildings = Persistence.service().query(buildingCriteria);
            for (Building building : buildings) {
                try {
                    mediaConfig.baseFolder = "data/export/images/" + NamespaceManager.getNamespace() + "/";
                    importIO.buildings().add(new BuildingRetriever().getModel(building, mediaConfig));
                } catch (Throwable t) {
                    log.error("Error converting building {}", building, t);
                    throw t;
                }
            }
            XMLStringWriter xml = new XMLStringWriter(Charset.forName("UTF-8"));
            XMLEntityWriter xmlWriter = new XMLEntityWriter(xml, new ImportXMLEntityNamingConvention());
            xmlWriter.setEmitId(false);
            xmlWriter.write(importIO);
            data = xml.getBytes();
        } catch (Throwable t) {
            log.error("Error converting data", t);
            if (ServerSideConfiguration.instance().isDevelopmentBehavior()) {
                throw new Error("Internal error", t);
            } else {
                throw new Error("Internal error");
            }
        }

        response.setHeader("Content-Disposition", "attachment; filename=\"export.xml\"");
        long fileExpires = System.currentTimeMillis() + Consts.MIN2MSEC * 2;
        response.setDateHeader("Expires", fileExpires);
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");

        response.setContentType(MimeMap.getContentType(DownloadFormat.XML));
        response.setContentLength(data.length);

        OutputStream output = response.getOutputStream();
        try {
            output.write(data);
        } finally {
            IOUtils.closeQuietly(output);
        }
    }
}
