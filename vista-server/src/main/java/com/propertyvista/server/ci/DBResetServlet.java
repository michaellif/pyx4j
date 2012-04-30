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
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.entity.rdb.EntityPersistenceServiceRDB;
import com.pyx4j.entity.rdb.RDBUtils;
import com.pyx4j.entity.rdb.cfg.Configuration.MultitenancyType;
import com.pyx4j.entity.rpc.DataPreloaderInfo;
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
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.server.contexts.Lifecycle;
import com.pyx4j.server.contexts.NamespaceManager;
import com.pyx4j.server.mail.Mail;

import com.propertyvista.admin.server.preloader.VistaAminDataPreloaders;
import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.domain.DemoData.DemoPmc;
import com.propertyvista.domain.security.VistaBasicBehavior;
import com.propertyvista.misc.VistaDataPreloaderParameter;
import com.propertyvista.misc.VistaDevPreloadConfig;
import com.propertyvista.server.config.VistaServerSideConfiguration;
import com.propertyvista.server.domain.admin.Pmc;

@SuppressWarnings("serial")
public class DBResetServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(DBResetServlet.class);

    @I18n(strategy = I18n.I18nStrategy.IgnoreAll)
    private static enum ResetType {

        @Translate("Drop All and Configure Vista Admin")
        prodReset,

        @Translate("Drop All and Preload all demo PMC (~60 seconds) [No Mockup]")
        all,

        @Translate("Drop All and Preload all demo PMC : Mini version for UI Design (~15 seconds)")
        allMini,

        @Translate("Drop All and Preload all demo PMC : Mockup version  (~5 minutes)")
        allWithMockup,

        @Translate("For All PMC Generate Mockup on top of existing data")
        allAddMockup,

        @Translate("Drop All Tables")
        clear,

        @Translate("Preload this PMC")
        preloadPmc,

        @Translate("Preload this PMC : Mockup version  (~5 minutes)")
        preloadPmcWithMockup,

        @Translate("Generate Mockup on top of existing data")
        addPmcMockup,

        @Translate("Generate Mockup on top of existing data - Only MockupTenantPreloader")
        addPmcMockupTest1,

        clearPmc,

        dropForeignKeys,

        @Translate("Reset Data Cache for this PMC")
        resetPmcCache,

        @Translate("Reset Data Cache for All PMC")
        resetAllCache;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        log.debug("DBReset requested");
        synchronized (DBResetServlet.class) {
            long start = System.currentTimeMillis();
            StringBuilder buf = new StringBuilder();
            String contentType = "text/plain";
            try {
                AbstractVistaServerSideConfiguration conf = (AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance();
                if (!conf.openDBReset()) {
                    if (!SecurityController.checkBehavior(VistaBasicBehavior.Admin)) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                        return;
                    }
                }
                log.debug("DBReset started");
                ResetType type = null;
                String tp = req.getParameter("type");
                if (CommonsStringUtils.isStringSet(tp)) {
                    try {
                        type = ResetType.valueOf(tp);
                    } catch (IllegalArgumentException e) {
                        buf.append("Invalid requests type=").append(tp).append("\n");
                    }
                }
                final String requestNamespace = NamespaceManager.getNamespace();
                if ((req.getParameter("help") != null) || (type == null)) {
                    contentType = "text/html";
                    buf.append("Current PMC is '").append(requestNamespace).append("'<br/>");
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
                    if (type == ResetType.resetPmcCache) {
                        CacheService.reset();
                        buf.append("\nCacheService.reset Ok");
                    } else if (type == ResetType.resetAllCache) {
                        CacheService.resetAll();
                        buf.append("\nCacheService.resetAll Ok");
                    } else {
                        Persistence.service().startBackgroundProcessTransaction();
                        Lifecycle.startElevatedUserContext();
                        Mail.getMailService().setDisabled(true);
                        try {
                            if (EnumSet.of(ResetType.prodReset, ResetType.all, ResetType.allMini, ResetType.allWithMockup, ResetType.clear).contains(type)) {
                                SchedulerHelper.shutdown();
                                RDBUtils.resetDatabase();
                                // Initialize Admin
                                NamespaceManager.setNamespace(Pmc.adminNamespace);
                                try {
                                    RDBUtils.ensureNamespace();
                                    SchedulerHelper.dbReset();
                                    Thread.sleep(150);
                                    ((EntityPersistenceServiceRDB) Persistence.service()).resetMapping();
                                    SchedulerHelper.init();
                                    if (((EntityPersistenceServiceRDB) Persistence.service()).getMultitenancyType() == MultitenancyType.SeparateSchemas) {
                                        RDBUtils.initNameSpaceSpecificEntityTables();
                                    } else {
                                        RDBUtils.initAllEntityTables();
                                    }
                                    CacheService.resetAll();

                                    new VistaAminDataPreloaders().preloadAll();
                                    Persistence.service().commit();
                                } finally {
                                    NamespaceManager.setNamespace(requestNamespace);
                                }
                            }

                            switch (type) {
                            case all:
                            case allWithMockup:
                            case allAddMockup:
                            case allMini:
                                for (DemoPmc demoPmc : EnumSet.allOf(DemoPmc.class)) {
                                    preloadPmc(req, buf, prodPmcNameCorrections(demoPmc.name()), type);
                                }
                                break;
                            case addPmcMockup:
                            case addPmcMockupTest1:
                            case preloadPmcWithMockup:
                            case preloadPmc: {
                                String pmc = req.getParameter("pmc");
                                if (pmc == null) {
                                    pmc = NamespaceManager.getNamespace();
                                }
                                preloadPmc(req, buf, pmc, type);

                                break;
                            }
                            case clearPmc: {
                                String pmc = req.getParameter("pmc");
                                if (pmc == null) {
                                    pmc = NamespaceManager.getNamespace();
                                }
                                buf.append("\n--- PMC  '" + pmc + "' ---\n");
                                RDBUtils.deleteFromAllEntityTables();
                                NamespaceManager.setNamespace(Pmc.adminNamespace);
                                EntityQueryCriteria<Pmc> criteria = EntityQueryCriteria.create(Pmc.class);
                                criteria.add(PropertyCriterion.eq(criteria.proto().namespace(), pmc));
                                Persistence.service().delete(criteria);
                                Persistence.service().commit();
                            }
                                break;
                            case dropForeignKeys:
                                RDBUtils.dropAllForeignKeys();
                                break;
                            case prodReset:
                                break;
                            default:
                                throw new Error("unimplemented: " + type);
                            }

                            buf.append("\nTotal time: " + TimeUtils.secSince(start));
                            log.info("DB reset {} {}", type, TimeUtils.secSince(start));
                            buf.insert(0, "Processing total time: " + TimeUtils.secSince(start) + "\n");
                        } catch (Throwable t) {
                            log.error("", t);
                            throw new Error(t);
                        } finally {
                            Mail.getMailService().setDisabled(false);
                            Lifecycle.endElevatedUserContext();
                            Persistence.service().endTransaction();
                        }
                    }
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

    private String prodPmcNameCorrections(String name) {
        if (ApplicationMode.isDevelopment()) {
            return name;
        } else {
            return "test-" + name;
        }
    }

    private void preloadPmc(HttpServletRequest req, StringBuilder buf, String pmcName, ResetType type) {
        long pmcStart = System.currentTimeMillis();
        NamespaceManager.setNamespace(Pmc.adminNamespace);
        EntityQueryCriteria<Pmc> criteria = EntityQueryCriteria.create(Pmc.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().dnsName(), pmcName));
        Persistence.service().delete(criteria);

        Pmc pmc = EntityFactory.create(Pmc.class);
        pmc.name().setValue(pmcName + " Demo");
        pmc.enabled().setValue(Boolean.TRUE);
        pmc.dnsName().setValue(pmcName);
        pmc.namespace().setValue(pmcName);

        Persistence.service().persist(pmc);
        Persistence.service().commit();

        NamespaceManager.setNamespace(pmcName);
        buf.append("\n--- Preload  " + pmcName + " ---\n");
        if (((EntityPersistenceServiceRDB) Persistence.service()).getMultitenancyType() == MultitenancyType.SeparateSchemas) {
            RDBUtils.ensureNamespace();
            // TODO Hack for non implemented SeparateSchemas DML 
            ((EntityPersistenceServiceRDB) Persistence.service()).resetMapping();
            RDBUtils.initAllEntityTables();
        }

        if (!EnumSet.of(ResetType.all, ResetType.allMini, ResetType.addPmcMockup, ResetType.allAddMockup, ResetType.addPmcMockupTest1).contains(type)) {
            RDBUtils.deleteFromAllEntityTables();
        }
        CacheService.reset();

        DataPreloaderCollection preloaders = ((VistaServerSideConfiguration) ServerSideConfiguration.instance()).getDataPreloaders();
        VistaDevPreloadConfig cfg;
        switch (type) {
        case preloadPmcWithMockup:
        case addPmcMockup:
        case addPmcMockupTest1:
        case allAddMockup:
        case allWithMockup:
            cfg = VistaDevPreloadConfig.createDefault();
            cfg.mockupData = true;
            break;
        case allMini:
            cfg = VistaDevPreloadConfig.createUIDesignMini();
            break;
        default:
            cfg = VistaDevPreloadConfig.createDefault();
        }
        if (pmcName.equals(DemoPmc.star.name())) {
            cfg.numPotentialTenants = 0;
            cfg.numTenants = 0;
        }
        setPreloadConfigParameter(req, cfg);
        preloaders.setParameterValue(VistaDataPreloaderParameter.devPreloadConfig.name(), cfg);

        if (type.name().toLowerCase().contains("add")) {
            Vector<DataPreloaderInfo> dpis = preloaders.getDataPreloaderInfo();
            Vector<DataPreloaderInfo> dpisRun = new Vector<DataPreloaderInfo>();
            String mockupClassNamefragment = "Mockup";
            for (DataPreloaderInfo info : dpis) {
                info.setParameters((HashMap<String, Serializable>) preloaders.getParametersValues());
                switch (type) {
                case addPmcMockupTest1:
                    mockupClassNamefragment = "MockupTenantPreloader";
                case allAddMockup:
                case addPmcMockup:
                    if (info.getDataPreloaderClassName().contains(mockupClassNamefragment)) {
                        dpisRun.add(info);
                        break;
                    }
                }
            }

            buf.append(preloaders.exectutePreloadersCreate(dpisRun));
        } else {
            buf.append(preloaders.preloadAll());
        }
        CacheService.reset();

        log.info("Preloaded PMC '{}' {}", pmcName, TimeUtils.secSince(pmcStart));
        buf.append("Preloaded PMC '" + pmcName + "' " + TimeUtils.secSince(pmcStart));
    }

    private void setPreloadConfigParameter(HttpServletRequest req, VistaDevPreloadConfig cfg) {
        for (Field field : cfg.getClass().getFields()) {
            String value = req.getParameter(field.getName());
            if (value != null) {
                try {
                    if (field.getType().equals(Integer.class) || field.getType().equals(int.class)) {
                        field.setInt(cfg, Integer.valueOf(value));
                    } else if (field.getType().equals(Long.class) || field.getType().equals(long.class)) {
                        field.setLong(cfg, Long.valueOf(value));
                    }
                } catch (Throwable e) {
                    throw new Error(e);
                }
            }
        }
    }
}
