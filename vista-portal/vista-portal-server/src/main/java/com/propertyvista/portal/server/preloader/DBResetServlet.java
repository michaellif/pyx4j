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
package com.propertyvista.portal.server.preloader;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.portal.server.VistaServerSideConfiguration;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.rdb.EntityPersistenceServiceRDB;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.server.ServerEntityFactory;
import com.pyx4j.entity.server.impl.EntityClassFinder;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.gwt.server.IOUtils;

@SuppressWarnings("serial")
public class DBResetServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(DBResetServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        long start = System.currentTimeMillis();
        VistaServerSideConfiguration conf = (VistaServerSideConfiguration) ServerSideConfiguration.instance();
        response.setHeader("Content-Disposition", "attachment; filename=\"db-reset.log\"");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setContentType("text/plain");
        OutputStream output = response.getOutputStream();
        try {
            EntityPersistenceServiceRDB srv = (EntityPersistenceServiceRDB) PersistenceServicesFactory.getPersistenceService();
            List<String> allClasses = EntityClassFinder.findEntityClasses();
            for (String className : allClasses) {
                Class<? extends IEntity> entityClass = ServerEntityFactory.entityClass(className);
                EntityMeta meta = EntityFactory.getEntityMeta(entityClass);
                if (meta.isTransient()) {
                    continue;
                }
                if (srv.isTableExists(meta.getEntityClass())) {
                    log.warn("drop table {}", meta.getEntityClass());
                    output.write(("drop table " + meta.getEntityClass() + "\n").getBytes());
                    srv.dropTable(meta.getEntityClass());
                }
            }

            output.write(conf.getDataPreloaders().preloadAll().getBytes());
            output.write(("\nTotal time: " + TimeUtils.secSince(start)).getBytes());
        } finally {
            IOUtils.closeQuietly(output);
        }
    }
}
