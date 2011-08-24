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

import com.pyx4j.commons.Consts;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.rpc.report.DownloadFormat;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.essentials.server.download.MimeMap;
import com.pyx4j.essentials.server.report.XMLStringWriter;
import com.pyx4j.essentials.server.xml.XMLEntityWriter;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.domain.VistaBehavior;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.interfaces.importer.BuildingRetriever;
import com.propertyvista.interfaces.importer.model.ImportIO;
import com.propertyvista.interfaces.importer.xml.ImportXMLEntityName;

@SuppressWarnings("serial")
public class ExportDownloadServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(Downloadable.class);

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        SecurityController.assertBehavior(VistaBehavior.ADMIN);

        log.debug("download export");

        ImportIO importIO = EntityFactory.create(ImportIO.class);
        EntityQueryCriteria<Building> buildingCriteria = EntityQueryCriteria.create(Building.class);
        List<Building> buildings = PersistenceServicesFactory.getPersistenceService().query(buildingCriteria);
        for (Building building : buildings) {
            importIO.buildings().add(new BuildingRetriever().getModel(building));
        }
        XMLStringWriter xml = new XMLStringWriter(Charset.forName("UTF-8"));
        XMLEntityWriter xmlWriter = new XMLEntityWriter(xml, new ImportXMLEntityName());
        xmlWriter.setEmitId(false);
        xmlWriter.write(importIO);
        byte[] data = xml.getBytes();

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
