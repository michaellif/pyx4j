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
package com.propertyvista.paypad.server.ci;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.rdb.EntityPersistenceServiceRDB;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.ServerEntityFactory;
import com.pyx4j.entity.server.impl.EntityClassFinder;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.essentials.server.EssentialsServerSideConfiguration;
import com.pyx4j.gwt.server.IOUtils;

@SuppressWarnings("serial")
public class DBResetServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(DBResetServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {

        if (!ServerSideConfiguration.instance().isDevelopmentBehavior()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        synchronized (DBResetServlet.class) {
            long start = System.currentTimeMillis();
            StringBuilder buf = new StringBuilder();
            try {
                EssentialsServerSideConfiguration conf = (EssentialsServerSideConfiguration) ServerSideConfiguration.instance();
                EntityPersistenceServiceRDB srv = (EntityPersistenceServiceRDB) Persistence.service();
                List<String> allClasses = EntityClassFinder.getEntityClassesNames();
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

                //SchedulerHelper.shutdown();
                //SchedulerHelper.dbReset();
                //SchedulerHelper.init();

                buf.append(conf.getDataPreloaders().preloadAll());
                buf.append("\nTotal time: " + TimeUtils.secSince(start));

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
