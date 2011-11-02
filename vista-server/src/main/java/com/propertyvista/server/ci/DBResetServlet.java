/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 6, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.ci;

import java.io.IOException;
import java.io.OutputStream;
import java.util.EnumSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.rdb.RDBUtils;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.server.preloader.DataGenerator;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.quartz.SchedulerHelper;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.domain.DemoData.DemoPmc;
import com.propertyvista.server.common.security.DevelopmentSecurity;
import com.propertyvista.server.config.VistaNamespaceResolver;
import com.propertyvista.server.config.VistaServerSideConfiguration;
import com.propertyvista.server.domain.admin.Pmc;

@SuppressWarnings("serial")
public class DBResetServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(DBResetServlet.class);

    private static enum ResetType {

        all,

        clear,

        // Use http://localhost:8888/vista/o/db-reset?type=preload
        preload,

        pmc,
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        synchronized (DBResetServlet.class) {
            long start = System.currentTimeMillis();
            StringBuilder buf = new StringBuilder();
            String contentType = "text/plain";
            try {
                VistaServerSideConfiguration conf = (VistaServerSideConfiguration) ServerSideConfiguration.instance();
                if (!conf.openDBReset()) {
                    if (!DevelopmentSecurity.isDevelopmentAccessGranted()) {
                        throw new Error("permission denied");
                    }
                }
                ResetType type = null;
                String tp = req.getParameter("type");
                if (CommonsStringUtils.isStringSet(tp)) {
                    try {
                        type = ResetType.valueOf(tp);
                    } catch (IllegalArgumentException e) {
                        buf.append("Invalid requests type=").append(tp).append("\n");
                    }
                }
                if ((req.getParameter("help") != null) || (type == null)) {
                    contentType = "text/html";
                    buf.append("Usage:</br>");
                    for (ResetType t : EnumSet.allOf(ResetType.class)) {
                        buf.append("<a href=\"");
                        buf.append("?type=").append(t.name()).append("\">");
                        buf.append("?type=").append(t.name());
                        buf.append("</a></br>");
                    }
                } else {
                    if (EnumSet.of(ResetType.all, ResetType.clear).contains(type)) {
                        RDBUtils.dropAllEntityTables();
                    }

                    SchedulerHelper.shutdown();
                    SchedulerHelper.dbReset();
                    SchedulerHelper.init();

                    switch (type) {
                    case all:
                    case preload:
                        buf.append(conf.getDataPreloaders().preloadAll());
                        break;
                    case pmc:
                        buf.append(conf.getDataPreloaders().delete());
                        break;
                    }
                    buf.append("\nTotal time: " + TimeUtils.secSince(start));

                    if (EnumSet.of(ResetType.all, ResetType.preload).contains(type)) {
                        String reqNamespace = NamespaceManager.getNamespace();
                        try {
                            NamespaceManager.setNamespace(Pmc.adminNamespace);
                            EntityQueryCriteria<Pmc> criteria = EntityQueryCriteria.create(Pmc.class);
                            criteria.add(PropertyCriterion.eq(criteria.proto().dnsName(), reqNamespace));
                            Pmc pmc = Persistence.service().retrieve(criteria);
                            if (pmc == null) {
                                pmc = EntityFactory.create(Pmc.class);
                                pmc.name().setValue(reqNamespace + " Demo");
                                pmc.dnsName().setValue(reqNamespace);
                                Persistence.service().persist(pmc);
                            }
                        } finally {
                            NamespaceManager.setNamespace(reqNamespace);
                        }
                    }

                    if ((type == ResetType.all) && NamespaceManager.getNamespace().equals(VistaNamespaceResolver.demoNamespace)) {

                        for (DemoPmc demoPmc : EnumSet.of(DemoPmc.star, DemoPmc.redridge, DemoPmc.rockville)) {
                            NamespaceManager.setNamespace(Pmc.adminNamespace);
                            Pmc pmc = EntityFactory.create(Pmc.class);
                            pmc.name().setValue(demoPmc.name() + " Demo");
                            pmc.dnsName().setValue(demoPmc.name());

                            Persistence.service().persist(pmc);

                            NamespaceManager.setNamespace(demoPmc.name());
                            buf.append("\n--- Preload  " + demoPmc.name() + " ---");
                            buf.append(conf.getDataPreloaders().preloadAll());
                            buf.append("\nTotal time: " + TimeUtils.secSince(start));
                        }
                    }
                    log.info("DB reset {} {}", type, TimeUtils.secSince(start));
                }

            } catch (Throwable t) {
                log.error("DB reset error", t);
                buf.append("\nError:");
                buf.append(t.getMessage());
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } finally {
                DataGenerator.cleanup();
            }
            response.setDateHeader("Expires", System.currentTimeMillis());
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-control", "no-cache, no-store, must-revalidate");
            response.setContentType(contentType);
            OutputStream output = response.getOutputStream();
            try {
                output.write(buf.toString().getBytes());
            } finally {
                IOUtils.closeQuietly(output);
            }
        }
    }
}
