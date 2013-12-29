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

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.Callable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Consts;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.rdb.EntityPersistenceServiceRDB;
import com.pyx4j.entity.rdb.RDBUtils;
import com.pyx4j.entity.rdb.cfg.Configuration.DatabaseType;
import com.pyx4j.entity.rdb.cfg.Configuration.MultitenancyType;
import com.pyx4j.entity.rpc.DataPreloaderInfo;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.dataimport.DataPreloaderCollection;
import com.pyx4j.essentials.rpc.report.ReportRequest;
import com.pyx4j.essentials.server.preloader.DataGenerator;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.quartz.SchedulerHelper;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.server.contexts.DevSession;
import com.pyx4j.server.contexts.Lifecycle;
import com.pyx4j.server.contexts.NamespaceManager;
import com.pyx4j.server.mail.Mail;

import com.propertyvista.biz.system.OperationsTriggerFacade;
import com.propertyvista.biz.system.PmcFacade;
import com.propertyvista.biz.system.VistaSystemFacade;
import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.DemoData.DemoPmc;
import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.misc.VistaDataPreloaderParameter;
import com.propertyvista.misc.VistaDevPreloadConfig;
import com.propertyvista.operations.domain.scheduler.PmcProcessType;
import com.propertyvista.operations.server.preloader.VistaOperationsDataPreloaders;
import com.propertyvista.operations.server.qa.DBIntegrityCheckDeferredProcess;
import com.propertyvista.portal.server.preloader.PmcCreatorDev;
import com.propertyvista.server.common.security.DevelopmentSecurity;
import com.propertyvista.server.config.VistaServerSideConfiguration;
import com.propertyvista.server.jobs.TaskRunner;
import com.propertyvista.shared.config.VistaDemo;

@SuppressWarnings("serial")
public class DBResetServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(DBResetServlet.class);

    @I18n(strategy = I18n.I18nStrategy.IgnoreAll)
    private static enum ResetType {

        @Translate("Drop All and Configure Vista Operations")
        prodReset,

        @Translate("Drop All and Preload all demo PMC (~3 min 24 seconds) [No Mockup]")
        all,

        @Translate("Drop All and Preload all demo PMC (+<b>star</b> tenants and buildings) : Mini version for UI Design (~1 min 22 seconds)")
        allMini,

        @Translate("Drop All and Preload One 'vista' PMC : Mini version for UI Design (~30 seconds)")
        vistaMini,

        @Translate("Drop All and Preload One 'vista' PMC : Perfomance tests version (~25 minutes)")
        vistaMax3000,

        @Translate("Drop All and Preload all demo PMC : Mockup version  (~5 minutes)")
        allWithMockup,

        @Translate("For All PMC Generate Mockup on top of existing data")
        allAddMockup,

        @Translate("Drop All Tables")
        clear,

        @Translate("Drop PMC Tables and Preload one PMC")
        resetPmc(true),

        @Translate("Drop <b>Operations</b> and PMC Tables and Preload one PMC")
        resetOperationsAndPmc(true),

        @Translate("Preload this PMC")
        preloadPmc(true),

        @Translate("Preload this PMC : Mockup version  (~5 minutes)")
        preloadPmcWithMockup,

        @Translate("Generate Mockup on top of existing data")
        addPmcMockup,

        @Translate("Generate Mockup on top of existing data - Only MockupTenantPreloader")
        addPmcMockupTest1,

        clearPmc(true),

        dropForeignKeys,

        dbIntegrityCheck,

        @Translate("Reset Data Cache for this PMC")
        resetPmcCache,

        @Translate("Reset Data Cache for All PMC")
        resetAllCache;

        private final boolean pmcParam;

        ResetType() {
            this.pmcParam = false;
        }

        ResetType(boolean pmcParam) {
            this.pmcParam = pmcParam;
        }

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    private class OutputHolder implements Closeable {

        boolean pipeBroken = false;

        OutputStream out;

        OutputHolder(OutputStream out) {
            this.out = out;
        }

        @Override
        public void close() throws IOException {
            if (this.out != null) {
                this.out.close();
                this.out = null;
            }
        }

    }

    private void o(OutputHolder out, String... messages) throws IOException {
        if (out.pipeBroken) {
            return;
        }
        try {
            out.out.write("<pre>".getBytes());
            for (String message : messages) {
                out.out.write(message.getBytes());
            }

            out.out.write("</pre>".getBytes());
            out.out.flush();
        } catch (Throwable e) {
            log.error("db-reset out put error", e);
            log.error("db-reset will continue");
            out.pipeBroken = true;
        }
    }

    private void h(OutputHolder out, String... messages) throws IOException {
        if (out.pipeBroken) {
            return;
        }
        try {
            for (String message : messages) {
                out.out.write(message.getBytes());
            }
            out.out.flush();
        } catch (Throwable e) {
            log.error("db-reset out put error", e);
            out.pipeBroken = true;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        long requestStart = System.currentTimeMillis();
        log.debug("DBReset requested from ip:{}, {}", Context.getRequestRemoteAddr(),
                DevSession.getSession().getAttribute(DevelopmentSecurity.OPENID_USER_EMAIL_ATTRIBUTE));
        response.setDateHeader("Expires", System.currentTimeMillis());
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-control", "no-cache, no-store, must-revalidate");
        response.setContentType("text/html");
        OutputHolder out = new OutputHolder(response.getOutputStream());
        h(out, "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"></head><body>");
        try {
            synchronized (DBResetServlet.class) {
                long start = System.currentTimeMillis();
                if (start - requestStart > 10 * Consts.MIN2MSEC) {
                    log.warn("Outdated DBReset request from ip:{}, {}", Context.getRequestRemoteAddr(),
                            DevSession.getSession().getAttribute(DevelopmentSecurity.OPENID_USER_EMAIL_ATTRIBUTE));
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
                try {
                    AbstractVistaServerSideConfiguration conf = (AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance();
                    if (!conf.openDBReset()) {
                        if (!SecurityController.checkBehavior(VistaBasicBehavior.Operations)) {
                            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                            return;
                        }
                    }
                    log.warn("DBReset started from ip:{}, {}", Context.getRequestRemoteAddr(),
                            DevSession.getSession().getAttribute(DevelopmentSecurity.OPENID_USER_EMAIL_ATTRIBUTE));
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
                            if (t.pmcParam) {
                                h(out, "<tr><td>&nbsp;</td></tr>");

                                h(out, "<tr><td>");
                                h(out, "</td><td>", t.toString());
                                h(out, "</td></tr>");
                                for (DemoPmc demoPmc : conf.dbResetPreloadPmc()) {
                                    h(out, "<tr><td>&nbsp;&nbsp;<a href=\"");
                                    h(out, "?type=", t.name());
                                    h(out, "&pmc=", demoPmc.name());
                                    h(out, "\">");

                                    h(out, "?type=", t.name());
                                    h(out, "&pmc=", demoPmc.name());

                                    h(out, "</a></td><td>", "<b>" + demoPmc.name() + "</b> &nbsp;", t.toString());
                                    h(out, "</td></tr>");
                                }
                                h(out, "<tr><td>&nbsp;");
                                h(out, "</td><td>");
                                h(out, "</td></tr>");
                            } else {
                                h(out, "<tr><td><a href=\"");
                                h(out, "?type=", t.name(), "\">");
                                h(out, "?type=", t.name());
                                h(out, "</a></td><td>", t.toString());
                                h(out, "</td></tr>");
                            }
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
                            // End transaction started by Framework filter
                            Persistence.service().endTransaction();

                            Persistence.service().startBackgroundProcessTransaction();
                            Lifecycle.startElevatedUserContext();
                            Mail.getMailService().setDisabled(true);
                            ServerSideFactory.create(VistaSystemFacade.class).setCommunicationsDisabled(true);
                            try {
                                if (EnumSet.of(ResetType.prodReset, ResetType.all, ResetType.allMini, ResetType.vistaMini, ResetType.vistaMax3000,
                                        ResetType.allWithMockup, ResetType.resetOperationsAndPmc, ResetType.clear).contains(type)) {
                                    Validate.isTrue(!VistaDeployment.isVistaProduction(), "Destruction is disabled");
                                    SchedulerHelper.shutdown();
                                    RDBUtils.resetDatabase();
                                    SchedulerHelper.dbReset();
                                    o(out, "DB Dropped: " + TimeUtils.secSince(start));
                                    Thread.sleep(150);
                                    SchedulerHelper.init();
                                    SchedulerHelper.setActive(true);
                                    log.debug("Initialize Admin");
                                    NamespaceManager.setNamespace(VistaNamespace.operationsNamespace);
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

                                        new VistaOperationsDataPreloaders().preloadAll();
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
                                } else if (type == ResetType.resetPmc) {
                                    String pmc = req.getParameter("pmc");
                                    if (pmc == null) {
                                        pmc = NamespaceManager.getNamespace();
                                    }
                                    resetPmcTables(pmc);
                                }

                                switch (type) {
                                case all:
                                case allWithMockup:
                                case allAddMockup:
                                case allMini:
                                    for (DemoPmc demoPmc : conf.dbResetPreloadPmc()) {
                                        preloadPmc(req, out, prodPmcNameCorrections(demoPmc.name()), type);
                                        h(out, "<script>window.scrollTo(0,document.body.scrollHeight);</script>");
                                    }
                                    break;
                                case vistaMini:
                                    preloadPmc(req, out, prodPmcNameCorrections(DemoPmc.vista.name()), type);
                                    break;
                                case vistaMax3000:
                                    preloadPmc(req, out, prodPmcNameCorrections(DemoPmc.vista.name()), type);
                                    break;
                                case addPmcMockup:
                                case addPmcMockupTest1:
                                case preloadPmcWithMockup:
                                case resetPmc:
                                case resetOperationsAndPmc:
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
                                    NamespaceManager.setNamespace(VistaNamespace.operationsNamespace);
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
                                case dbIntegrityCheck:
                                    ReportRequest reportdbo = new ReportRequest();
                                    EntityQueryCriteria<Pmc> criteria = EntityQueryCriteria.create(Pmc.class);
                                    criteria.add(PropertyCriterion.ne(criteria.proto().status(), PmcStatus.Created));
                                    criteria.asc(criteria.proto().namespace());
                                    reportdbo.setCriteria(criteria);
                                    new DBIntegrityCheckDeferredProcess(reportdbo).execute();
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
                                ServerSideFactory.create(VistaSystemFacade.class).setCommunicationsDisabled(false);
                                Mail.getMailService().setDisabled(false);
                                Lifecycle.endElevatedUserContext();
                                Persistence.service().endTransaction();
                            }
                            h(out, "<p style=\"background-color:33FF33\">DONE</p>");
                            h(out, "<script>window.scrollTo(0,document.body.scrollHeight);</script>");
                        }
                    }
                    h(out, "</body></html>");
                } catch (Throwable t) {
                    log.error("DB reset error", t);
                    o(out, "\nDB reset error:");
                    o(out, t.getMessage());
                    h(out, "<script>window.scrollTo(0,document.body.scrollHeight);</script>");
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                } finally {
                    DataGenerator.cleanup();
                    CacheService.reset();
                    log.warn("DBReset compleated from ip:{}, {}", Context.getRequestRemoteAddr(),
                            DevSession.getSession().getAttribute(DevelopmentSecurity.OPENID_USER_EMAIL_ATTRIBUTE));
                }
            }
        } finally {
            IOUtils.closeQuietly(out);
        }

    }

    private void resetPmcTables(String pmc) {
        final String requestNamespace = NamespaceManager.getNamespace();
        NamespaceManager.setNamespace(VistaNamespace.operationsNamespace);
        try {
            CacheService.resetAll();
            RDBUtils.resetSchema(pmc);
            Persistence.service().commit();
        } finally {
            NamespaceManager.setNamespace(requestNamespace);
        }
    }

    private String prodPmcNameCorrections(String name) {
        if (ApplicationMode.isDevelopment() || VistaDemo.isDemo()) {
            return name;
        } else {
            return name;
        }
    }

    private void preloadPmc(HttpServletRequest req, OutputHolder out, String pmcDnsName, ResetType type) throws IOException {
        long pmcStart = System.currentTimeMillis();
        NamespaceManager.setNamespace(VistaNamespace.operationsNamespace);
        log.debug("Preload PMC '{}'", pmcDnsName);

        EntityQueryCriteria<Pmc> criteria = EntityQueryCriteria.create(Pmc.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().dnsName(), pmcDnsName));
        ServerSideFactory.create(PmcFacade.class).deleteAllPmcData(Persistence.service().retrieve(criteria));

        final Pmc pmc = PmcCreatorDev.createPmc(pmcDnsName, (type == ResetType.allMini));
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

        if (!EnumSet.of(ResetType.all, ResetType.allMini, ResetType.vistaMini, ResetType.vistaMax3000, ResetType.addPmcMockup, ResetType.allAddMockup,
                ResetType.addPmcMockupTest1).contains(type)) {
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
        case vistaMax3000:
            cfg = VistaDevPreloadConfig.createPerfomanceMax(3000);
            break;
        default:
            cfg = VistaDevPreloadConfig.createDefault();
        }
        //TODO fix LeasePreloader
        if (pmcDnsName.equals(DemoPmc.star.name()) && (type != ResetType.allMini)) {
            cfg.numComplexes = 0;
            cfg.numResidentialBuildings = 0;
            cfg.numPotentialTenants2CreditCheck = 0;
            cfg.numPotentialTenants = 0;
            cfg.numTenants = 0;
            cfg.numLeads = 0;
            cfg.numUnAssigendTenants = 0;
        }
        setPreloadConfigParameter(req, cfg);
        preloaders.setParameterValue(VistaDataPreloaderParameter.devPreloadConfig.name(), cfg);
        preloaders.setParameterValue(VistaDataPreloaderParameter.pmcName.name(), pmc.name().getStringView());

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
                default:
                    break;
                }
            }

            o(out, preloaders.exectutePreloadersCreate(dpisRun));
        } else {
            o(out, preloaders.preloadAll());
        }

        if (pmcDnsName.equals(DemoPmc.star.name()) && (type == ResetType.all)) {
            TaskRunner.runInOperationsNamespace(new Callable<Void>() {
                @Override
                public Void call() {
                    ServerSideFactory.create(OperationsTriggerFacade.class).startProcess(PmcProcessType.yardiImportProcess, pmc, null);
                    return null;
                }
            });
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
                    } else if (field.getType().equals(String.class)) {
                        field.set(cfg, value);
                    }
                } catch (Throwable e) {
                    throw new Error(e);
                }
            }
        }
    }
}
