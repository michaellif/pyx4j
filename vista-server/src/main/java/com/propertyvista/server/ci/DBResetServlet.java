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
import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.entity.rdb.RDBUtils;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.dataimport.DataPreloaderCollection;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.server.preloader.DataGenerator;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.quartz.SchedulerHelper;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.domain.DemoData.DemoPmc;
import com.propertyvista.misc.VistaDataPreloaderParameter;
import com.propertyvista.misc.VistaDevPreloadConfig;
import com.propertyvista.server.common.security.DevelopmentSecurity;
import com.propertyvista.server.config.VistaServerSideConfiguration;
import com.propertyvista.server.domain.admin.Pmc;

@SuppressWarnings("serial")
public class DBResetServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(DBResetServlet.class);

    @I18n(strategy = I18n.I18nStrategy.IgnoreAll)
    private static enum ResetType {

        @Translate("Drop All and Preload all demo PMC (~60 seconds)")
        all,

        @Translate("Drop All and Preload all demo PMC : Mini version for UI Design (~15 seconds)")
        allMini,

        @Translate("Drop All and Preload all demo PMC : Mockup version  (~5 minutes)")
        allWithMockup,

        @Translate("Drop All Tables")
        clear,

        @Translate("Preload this PMC")
        preloadPmc,

        clearPmc;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        log.debug("DBReset requested");
        synchronized (DBResetServlet.class) {
            log.debug("DBReset started");
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
                    buf.append("Current PMC is '").append(NamespaceManager.getNamespace()).append("'<br/>");
                    buf.append("Usage:<br/><table>");
                    for (ResetType t : EnumSet.allOf(ResetType.class)) {
                        buf.append("<tr><td><a href=\"");
                        buf.append("?type=").append(t.name()).append("\">");
                        buf.append("?type=").append(t.name());
                        buf.append("</a></td><td>").append(t.toString());
                        buf.append("</td></tr>");
                    }
                    buf.append("</table>");
                } else {
                    buf.append("Requested : '" + type.name() + "' " + type.toString());
                    if (EnumSet.of(ResetType.all, ResetType.allMini, ResetType.allWithMockup, ResetType.clear).contains(type)) {
                        RDBUtils.dropAllEntityTables();
                    }
                    CacheService.reset();
                    SchedulerHelper.shutdown();
                    SchedulerHelper.dbReset();
                    SchedulerHelper.init();

                    switch (type) {
                    case all:
                    case allWithMockup:
                    case allMini:
                        for (DemoPmc demoPmc : EnumSet.allOf(DemoPmc.class)) {
                            preloadPmc(buf, demoPmc.name(), type);
                        }
                        break;
                    case preloadPmc:
                        preloadPmc(buf, NamespaceManager.getNamespace(), type);
                        break;
                    case clearPmc: {
                        String thisPmcName = NamespaceManager.getNamespace();
                        buf.append("\n--- PMC  '" + thisPmcName + "' ---\n");
                        RDBUtils.deleteFromAllEntityTables();
                        NamespaceManager.setNamespace(Pmc.adminNamespace);
                        EntityQueryCriteria<Pmc> criteria = EntityQueryCriteria.create(Pmc.class);
                        criteria.add(PropertyCriterion.eq(criteria.proto().dnsName(), thisPmcName));
                        Persistence.service().delete(criteria);
                    }
                        break;
                    }

                    buf.append("\nTotal time: " + TimeUtils.secSince(start));
                    log.info("DB reset {} {}", type, TimeUtils.secSince(start));
                    buf.insert(0, "Processing total time: " + TimeUtils.secSince(start) + "\n");
                }

            } catch (Throwable t) {
                log.error("DB reset error", t);
                buf.append("\nError:");
                buf.append(t.getMessage());
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } finally {
                DataGenerator.cleanup();
                CacheService.reset();
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

    private void preloadPmc(StringBuilder buf, String demoPmcName, ResetType type) {
        long pmcStart = System.currentTimeMillis();
        NamespaceManager.setNamespace(Pmc.adminNamespace);
        EntityQueryCriteria<Pmc> criteria = EntityQueryCriteria.create(Pmc.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().dnsName(), demoPmcName));
        Persistence.service().delete(criteria);

        Pmc pmc = EntityFactory.create(Pmc.class);
        pmc.name().setValue(demoPmcName + " Demo");
        pmc.dnsName().setValue(demoPmcName);

        Persistence.service().persist(pmc);

        NamespaceManager.setNamespace(demoPmcName);
        buf.append("\n--- Preload  " + demoPmcName + " ---");

        if (!EnumSet.of(ResetType.all, ResetType.allMini).contains(type)) {
            RDBUtils.deleteFromAllEntityTables();
        }

        DataPreloaderCollection preloaders = ((VistaServerSideConfiguration) ServerSideConfiguration.instance()).getDataPreloaders();
        switch (type) {
        case allWithMockup:
            VistaDevPreloadConfig cfg = VistaDevPreloadConfig.createDefault();
            cfg.mockupData = true;
            preloaders.setParameterValue(VistaDataPreloaderParameter.devPreloadConfig.name(), cfg);
            break;
        case allMini:
            preloaders.setParameterValue(VistaDataPreloaderParameter.devPreloadConfig.name(), VistaDevPreloadConfig.createUIDesignMini());
            break;
        }

        buf.append(preloaders.preloadAll());
        CacheService.reset();

        log.info("Preloaded PMC '{}' {}", demoPmcName, TimeUtils.secSince(pmcStart));
        buf.append("Preloaded PMC '" + demoPmcName + "' " + TimeUtils.secSince(pmcStart));
    }
}
