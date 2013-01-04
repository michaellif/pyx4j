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

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.entity.rdb.EntityPersistenceServiceRDB;
import com.pyx4j.entity.rdb.RDBUtils;
import com.pyx4j.entity.rdb.cfg.Configuration.DatabaseType;
import com.pyx4j.entity.rdb.cfg.Configuration.MultitenancyType;
import com.pyx4j.entity.rpc.DataPreloaderInfo;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.dataimport.DataPreloaderCollection;
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

import com.propertyvista.admin.server.preloader.VistaAdminDataPreloaders;
import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.DemoData.DemoPmc;
import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.misc.VistaDataPreloaderParameter;
import com.propertyvista.misc.VistaDevPreloadConfig;
import com.propertyvista.portal.server.preloader.PmcCreatorDev;
import com.propertyvista.server.config.VistaServerSideConfiguration;
import com.propertyvista.shared.config.VistaDemo;

@SuppressWarnings("serial")
public class DBResetServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(DBResetServlet.class);

    @I18n(strategy = I18n.I18nStrategy.IgnoreAll)
    private static enum ResetType {

        @Translate("Drop All and Configure Vista Admin")
        prodReset,

        @Translate("Drop All and Preload all demo PMC (~3 min 30 seconds) [No Mockup]")
        all,

        @Translate("Drop All and Preload all demo PMC : Mini version for UI Design (~1 min 30 seconds)")
        allMini,

        @Translate("Drop All and Preload One 'vista' PMC : Mini version for UI Design (~30 seconds)")
        vistaMini,

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

    private void o(OutputStream out, String... messages) throws IOException {
        out.write("<pre>".getBytes());
        for (String message : messages) {
            out.write(message.getBytes());
        }

        out.write("</pre>".getBytes());
        out.flush();
    }

    private void h(OutputStream out, String... messages) throws IOException {
        for (String message : messages) {
            out.write(message.getBytes());
        }
        out.flush();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        log.debug("DBReset requested");
        response.setDateHeader("Expires", System.currentTimeMillis());
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-control", "no-cache, no-store, must-revalidate");
        response.setContentType("text/html");
        OutputStream out = response.getOutputStream();
        h(out, "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"></head><body>");
        try {
            synchronized (DBResetServlet.class) {
                long start = System.currentTimeMillis();
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
                            o(out, "Invalid requests type=", tp, "\n");
                        }
                    }
                    final String requestNamespace = NamespaceManager.getNamespace();
                    if ((req.getParameter("help") != null) || (type == null)) {
                        o(out, "Current PMC is '", requestNamespace, "'<br/>");
                        h(out, "Usage:<br/><table>");
                        for (ResetType t : EnumSet.allOf(ResetType.class)) {
                            h(out, "<tr><td><a href=\"");
                            h(out, "?type=", t.name(), "\">");
                            h(out, "?type=", t.name());
                            h(out, "</a></td><td>", t.toString());
                            h(out, "</td></tr>");
                        }
                        h(out, "</table>");
                    } else {
                        o(out, "Requested : '" + type.name() + "' " + type.toString());
                        if (type == ResetType.resetPmcCache) {
                            CacheService.reset();
                            o(out, "\nCacheService.reset Ok");
                        } else if (type == ResetType.resetAllCache) {
                            CacheService.resetAll();
                            o(out, "\nCacheService.resetAll Ok");
                        } else {
                            Persistence.service().startBackgroundProcessTransaction();
                            Lifecycle.startElevatedUserContext();
                            Mail.getMailService().setDisabled(true);
                            ServerSideFactory.create(CommunicationFacade.class).setDisabled(true);
                            try {
                                if (EnumSet.of(ResetType.prodReset, ResetType.all, ResetType.allMini, ResetType.vistaMini, ResetType.allWithMockup,
                                        ResetType.clear).contains(type)) {
                                    Validate.isTrue(!VistaDeployment.isVistaProduction(), "Destruction is disabled");
                                    SchedulerHelper.shutdown();
                                    RDBUtils.resetDatabase();
                                    SchedulerHelper.dbReset();
                                    o(out, "DB Dropped: " + TimeUtils.secSince(start));
                                    Thread.sleep(150);
                                    SchedulerHelper.init();
                                    log.debug("Initialize Admin");
                                    NamespaceManager.setNamespace(VistaNamespace.adminNamespace);
                                    try {
                                        RDBUtils.ensureNamespace();
                                        long astart = System.currentTimeMillis();
                                        if (((EntityPersistenceServiceRDB) Persistence.service()).getMultitenancyType() == MultitenancyType.SeparateSchemas) {
                                            RDBUtils.initNameSpaceSpecificEntityTables();
                                            o(out, "Admin tables created: " + TimeUtils.secSince(astart));
                                        } else {
                                            RDBUtils.initAllEntityTables();
                                            o(out, "All tables created: " + TimeUtils.secSince(astart));
                                        }
                                        if (((EntityPersistenceServiceRDB) Persistence.service()).getDatabaseType() == DatabaseType.PostgreSQL) {
                                            Persistence.service().commit();
                                        }

                                        CacheService.resetAll();

                                        new VistaAdminDataPreloaders().preloadAll();
                                        Persistence.service().commit();
                                    } finally {
                                        NamespaceManager.setNamespace(requestNamespace);
                                    }

                                    if (((EntityPersistenceServiceRDB) Persistence.service()).getMultitenancyType() == MultitenancyType.SeparateSchemas) {
                                        NamespaceManager.setNamespace(VistaNamespace.expiringNamespace);
                                        try {
                                            RDBUtils.ensureNamespace();
                                            RDBUtils.initNameSpaceSpecificEntityTables();
                                            Persistence.service().commit();
                                        } finally {
                                            NamespaceManager.setNamespace(requestNamespace);
                                        }
                                    }
                                }

                                switch (type) {
                                case all:
                                case allWithMockup:
                                case allAddMockup:
                                case allMini:
                                    for (DemoPmc demoPmc : conf.dbResetPreloadPmc()) {
                                        preloadPmc(req, out, prodPmcNameCorrections(demoPmc.name()), type);
                                    }
                                    break;
                                case vistaMini:
                                    preloadPmc(req, out, prodPmcNameCorrections(DemoPmc.vista.name()), type);
                                    break;
                                case addPmcMockup:
                                case addPmcMockupTest1:
                                case preloadPmcWithMockup:
                                case preloadPmc: {
                                    String pmc = req.getParameter("pmc");
                                    if (pmc == null) {
                                        pmc = NamespaceManager.getNamespace();
                                    }
                                    preloadPmc(req, out, pmc, type);

                                    break;
                                }
                                case clearPmc: {
                                    String pmc = req.getParameter("pmc");
                                    if (pmc == null) {
                                        pmc = NamespaceManager.getNamespace();
                                    }
                                    o(out, "\n--- PMC  '" + pmc + "' ---\n");
                                    RDBUtils.deleteFromAllEntityTables();
                                    NamespaceManager.setNamespace(VistaNamespace.adminNamespace);
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

                                o(out, "\nTotal time: " + TimeUtils.secSince(start));
                                log.info("DB reset {} {}", type, TimeUtils.secSince(start));
                                o(out, "Processing total time: " + TimeUtils.secSince(start) + "\n");
                            } catch (Throwable t) {
                                log.error("", t);
                                Persistence.service().rollback();
                                throw new Error(t);
                            } finally {
                                ServerSideFactory.create(CommunicationFacade.class).setDisabled(false);
                                Mail.getMailService().setDisabled(false);
                                Lifecycle.endElevatedUserContext();
                                Persistence.service().endTransaction();
                            }
                            h(out, "<p style=\"background-color:33FF33\">DONE</p>");
                        }
                    }
                    h(out, "</body></html>");
                } catch (Throwable t) {
                    log.error("DB reset error", t);
                    o(out, "\nDB reset error:");
                    o(out, t.getMessage());
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                } finally {
                    DataGenerator.cleanup();
                    CacheService.reset();
                }
            }
        } finally {
            IOUtils.closeQuietly(out);
        }

    }

    private String prodPmcNameCorrections(String name) {
        if (ApplicationMode.isDevelopment() || VistaDemo.isDemo()) {
            return name;
        } else {
            return "test-" + name;
        }
    }

    private void preloadPmc(HttpServletRequest req, OutputStream out, String pmcDnsName, ResetType type) throws IOException {
        long pmcStart = System.currentTimeMillis();
        NamespaceManager.setNamespace(VistaNamespace.adminNamespace);
        log.debug("Preload PMC '{}'", pmcDnsName);

        EntityQueryCriteria<Pmc> criteria = EntityQueryCriteria.create(Pmc.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().dnsName(), pmcDnsName));
        Persistence.service().delete(criteria);

        Pmc pmc = PmcCreatorDev.createPmc(pmcDnsName);
        Persistence.service().commit();

        VistaDeployment.changePmcContext();

        NamespaceManager.setNamespace(pmc.namespace().getValue());
        o(out, "\n--- Preload  " + pmcDnsName + " ---\n");
        if (((EntityPersistenceServiceRDB) Persistence.service()).getMultitenancyType() == MultitenancyType.SeparateSchemas) {
            RDBUtils.ensureNamespace();
            RDBUtils.initAllEntityTables();
            if (((EntityPersistenceServiceRDB) Persistence.service()).getDatabaseType() == DatabaseType.PostgreSQL) {
                Persistence.service().commit();
            }
            o(out, "PMC Tables created ", TimeUtils.secSince(pmcStart));
        }

        if (!EnumSet.of(ResetType.all, ResetType.allMini, ResetType.vistaMini, ResetType.addPmcMockup, ResetType.allAddMockup, ResetType.addPmcMockupTest1)
                .contains(type)) {
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
            cfg = VistaDevPreloadConfig.createMockup();
            break;
        case vistaMini:
        case allMini:
            cfg = VistaDevPreloadConfig.createUIDesignMini();
            break;
        default:
            cfg = VistaDevPreloadConfig.createDefault();
        }
        if (pmcDnsName.equals(DemoPmc.star.name())) {
            cfg.numPotentialTenants2CreditCheck = 0;
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

            o(out, preloaders.exectutePreloadersCreate(dpisRun));
        } else {
            o(out, preloaders.preloadAll());
        }
        CacheService.reset();

        log.info("Preloaded PMC '{}' {}", pmcDnsName, TimeUtils.secSince(pmcStart));
        o(out, "Preloaded PMC '" + pmcDnsName + "' " + TimeUtils.secSince(pmcStart));
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
