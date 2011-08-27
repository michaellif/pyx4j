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
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.rdb.EntityPersistenceServiceRDB;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.server.ServerEntityFactory;
import com.pyx4j.entity.server.impl.EntityClassFinder;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.quartz.SchedulerHelper;
import com.pyx4j.server.contexts.NamespaceManager;

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
            try {
                VistaServerSideConfiguration conf = (VistaServerSideConfiguration) ServerSideConfiguration.instance();
                if (!conf.openDBReset()) {
                    if (!DevelopmentSecurity.isDevelopmentAccessGranted()) {
                        throw new Error("permission denied");
                    }
                }

                EntityPersistenceServiceRDB srv = (EntityPersistenceServiceRDB) PersistenceServicesFactory.getPersistenceService();

                ResetType type = ResetType.all;
                String tp = req.getParameter("type");
                if (CommonsStringUtils.isStringSet(tp)) {
                    type = ResetType.valueOf(tp);
                }

                if (EnumSet.of(ResetType.all, ResetType.clear).contains(type)) {
                    List<String> allClasses = EntityClassFinder.findEntityClasses();
                    for (String className : allClasses) {
                        Class<? extends IEntity> entityClass = ServerEntityFactory.entityClass(className);
                        EntityMeta meta = EntityFactory.getEntityMeta(entityClass);
                        if (meta.isTransient()) {
                            continue;
                        }
                        if (srv.isTableExists(meta.getEntityClass())) {
                            log.warn("drop table {}", meta.getEntityClass());
                            buf.append("drop table " + meta.getEntityClass() + "\n");
                            srv.dropTable(meta.getEntityClass());
                        }
                    }
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
                    NamespaceManager.setNamespace(Pmc.adminNamespace);
                    Pmc pmc = EntityFactory.create(Pmc.class);
                    pmc.name().setValue("Start Demo");
                    pmc.dnsName().setValue(VistaNamespaceResolver.demoSLNamespace);

                    Persistence.service().persist(pmc);

                    NamespaceManager.setNamespace(VistaNamespaceResolver.demoSLNamespace);
                    buf.append("\n---Preload SL---");
                    buf.append(conf.getDataPreloaders().preloadAll());
                    buf.append("\nTotal time: " + TimeUtils.secSince(start));
                }

            } catch (Throwable t) {
                log.error("DB reset error", t);
                buf.append("\nError:");
                buf.append(t.getMessage());
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
            response.setDateHeader("Expires", System.currentTimeMillis());
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-control", "no-cache, no-store, must-revalidate");
            response.setContentType("text/plain");
            OutputStream output = response.getOutputStream();
            try {
                output.write(buf.toString().getBytes());
            } finally {
                IOUtils.closeQuietly(output);
            }
        }
    }
}
