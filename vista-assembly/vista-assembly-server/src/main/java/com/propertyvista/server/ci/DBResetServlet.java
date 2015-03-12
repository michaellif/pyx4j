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
 */
package com.propertyvista.server.ci;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Consts;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.rpc.report.ReportRequest;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.server.contexts.DevSession;
import com.pyx4j.server.contexts.Lifecycle;
import com.pyx4j.server.contexts.NamespaceManager;
import com.pyx4j.server.contexts.ServerContext;

import com.propertyvista.biz.preloader.OutputHolder;
import com.propertyvista.biz.preloader.PmcPreloaderFacade;
//import net.sf.dynamicreports.design.constant.ResetType;
import com.propertyvista.biz.preloader.ResetType;
import com.propertyvista.biz.preloader.pmc.PmcPreloaderManager;
import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.domain.DemoData.DemoPmc;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.domain.security.common.VistaAccessGrantedBehavior;
import com.propertyvista.operations.server.preloader.VistaOperationsDataPreloaders;
import com.propertyvista.operations.server.qa.DBIntegrityCheckDeferredProcess;
import com.propertyvista.server.common.security.DevelopmentSecurity;

@SuppressWarnings("serial")
public class DBResetServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(DBResetServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {

        long requestStart = System.currentTimeMillis();
        log.debug("DBReset requested from ip:{}, {}", ServerContext.getRequestRemoteAddr(),
                DevSession.getSession().getAttribute(DevelopmentSecurity.OPENID_USER_EMAIL_ATTRIBUTE));
        response.setDateHeader("Expires", System.currentTimeMillis());
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-control", "no-cache, no-store, must-revalidate");
        response.setContentType("text/html");
        OutputHolder out = new OutputHolder(response.getOutputStream());
        out.h("<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"></head><body>");
        try {

            long start = System.currentTimeMillis();
            if (start - requestStart > 10 * Consts.MIN2MSEC) {
                log.warn("Outdated DBReset request from ip:{}, {}", ServerContext.getRequestRemoteAddr(),
                        DevSession.getSession().getAttribute(DevelopmentSecurity.OPENID_USER_EMAIL_ATTRIBUTE));
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            try {
                AbstractVistaServerSideConfiguration conf = (AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance();
                if (!conf.openDBReset()) {
                    if (!SecurityController.check(VistaAccessGrantedBehavior.Operations)) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                        return;
                    }
                }
                log.warn("DBReset started from ip:{}, {}", ServerContext.getRequestRemoteAddr(),
                        DevSession.getSession().getAttribute(DevelopmentSecurity.OPENID_USER_EMAIL_ATTRIBUTE));
                ResetType type = null;
                String tp = req.getParameter("type");
                if (CommonsStringUtils.isStringSet(tp)) {
                    try {
                        type = ResetType.valueOf(tp);
                    } catch (IllegalArgumentException e) {
                        out.o("Invalid requests type=", tp, "\n");
                    }
                }
                final String requestNamespace = NamespaceManager.getNamespace();
                if ((req.getParameter("help") != null) || (type == null)) {
                    printHelp(requestNamespace, out, conf.dbResetPreloadPmc());

                } else {
                    out.o("Requested : '" + type.name() + "' " + type.toString());
                    if (type == ResetType.resetPmcCache) {
                        ServerSideFactory.create(PmcPreloaderFacade.class).resetPmcCache(out);
                    } else if (type == ResetType.resetAllCache) {
                        ServerSideFactory.create(PmcPreloaderFacade.class).resetAllCache(out);
                    } else {
                        // End transaction started by Framework filter
                        Persistence.service().endTransaction(); // TODO Add this to functions in facade?

                        Persistence.service().startBackgroundProcessTransaction();
                        Lifecycle.startElevatedUserContext();
                        try {
                            if (EnumSet.of(ResetType.prodReset, ResetType.all, ResetType.allMini, ResetType.vistaMini, ResetType.vista, ResetType.vistaMax3000,
                                    ResetType.allWithMockup, ResetType.resetOperationsAndPmc, ResetType.clear).contains(type)) {

                                ServerSideFactory.create(PmcPreloaderFacade.class).resetAll(out, new VistaOperationsDataPreloaders());

                            } else if (type == ResetType.resetPmc) {
                                String pmc = ensurePmc(req.getParameter("pmc"));
                                ServerSideFactory.create(PmcPreloaderFacade.class).resetPmcTables(pmc);
                            }

                            switch (type) {
                            case all:
                            case allWithMockup:
                            case allAddMockup:
                            case allMini:
                                for (DemoPmc demoPmc : conf.dbResetPreloadPmc()) {
                                    ServerSideFactory.create(PmcPreloaderFacade.class).preloadPmc(prodPmcNameCorrections(demoPmc.name()), type,
                                            req.getParameterMap(), out);
                                    out.h("<script>window.scrollTo(0,document.body.scrollHeight);</script>");
                                }
                                break;
                            case vista:
                                ServerSideFactory.create(PmcPreloaderFacade.class).preloadPmc(prodPmcNameCorrections(DemoPmc.vista.name()), type,
                                        req.getParameterMap(), out);
                                break;
                            case vistaMini:
                                ServerSideFactory.create(PmcPreloaderFacade.class).preloadPmc(prodPmcNameCorrections(DemoPmc.vista.name()), type,
                                        req.getParameterMap(), out);
                                break;
                            case vistaMax3000:
                                ServerSideFactory.create(PmcPreloaderFacade.class).preloadPmc(prodPmcNameCorrections(DemoPmc.vista.name()), type,
                                        req.getParameterMap(), out);
                                break;
                            case addPmcMockup:
                            case addPmcMockupTest1:
                            case preloadPmcWithMockup:
                            case resetPmc:
                            case resetOperationsAndPmc:
                            case preloadPmc: {
                                String pmc = ensurePmc(req.getParameter("pmc"));
                                ServerSideFactory.create(PmcPreloaderFacade.class).preloadPmc(pmc, type, req.getParameterMap(), out);
                                break;
                            }
                            case clearPmc: {
                                String pmc = ensurePmc(req.getParameter("pmc"));
                                out.o("\n--- PMC  '" + pmc + "' ---\n");
                                ServerSideFactory.create(PmcPreloaderFacade.class).clearPmc(pmc);
                            }
                                break;
                            case dropForeignKeys:
                                ServerSideFactory.create(PmcPreloaderFacade.class).dropForeignKeys(out);
                                break;
                            case prodReset:
                                break;
                            case dbIntegrityCheck:
                                // TODO No access to operations-server module for DBIntegrityCheckDeferredProcess
                                // change for:
                                // ServerSideFactory.create(PmcPreloaderFacade.class).dbIntegrityCheck(out);
                                PmcPreloaderManager.instance().stopCommunications();
                                try {
                                    ReportRequest reportdbo = new ReportRequest();
                                    EntityQueryCriteria<Pmc> criteria = EntityQueryCriteria.create(Pmc.class);
                                    criteria.add(PropertyCriterion.ne(criteria.proto().status(), PmcStatus.Created));
                                    criteria.asc(criteria.proto().namespace());
                                    reportdbo.setCriteria(criteria);
                                    new DBIntegrityCheckDeferredProcess(reportdbo, false).execute();
                                    PmcPreloaderManager.recordOperation(ResetType.dbIntegrityCheck, start);
                                } catch (Throwable t) {
                                    log.error("", t);
                                    Persistence.service().rollback();
                                    PmcPreloaderManager.writeToOutput(out, "\nDB reset error:");
                                    PmcPreloaderManager.writeToOutput(out, t.getMessage());
                                } finally {
                                    PmcPreloaderManager.instance().startCommunications();
                                    PmcPreloaderManager.instance().performResetFinallyActions();
                                }
                                break;
                            default:
                                throw new Error("unimplemented: " + type);
                            }

                        } finally {
                            Lifecycle.endElevatedUserContext();
                            Persistence.service().endTransaction();
                        }

                        out.o("\nTotal time: " + TimeUtils.secSince(start));
                        log.info("DB reset {} {}", type, TimeUtils.secSince(start));
                        out.o("Processing total time: " + TimeUtils.secSince(start) + "\n");
                        out.h("<p style=\"background-color:33FF33\">DONE</p>");
                        out.h("<script>window.scrollTo(0,document.body.scrollHeight);</script>");
                    }
                }
                out.h("</body></html>");
            } catch (Throwable t) {
                out.h("<p style=\"visibility: hidden;\">DB reset error</p>");
                out.h("<p style=\"background-color:FF3030\">ERROR</p>");
                out.h("<script>window.scrollTo(0,document.body.scrollHeight);</script>");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } finally {
                log.warn("DBReset completed from ip:{}, {}", ServerContext.getRequestRemoteAddr(),
                        DevSession.getSession().getAttribute(DevelopmentSecurity.OPENID_USER_EMAIL_ATTRIBUTE));
            }
        } finally {
            IOUtils.closeQuietly(out);
        }

    }

    private void printHelp(String requestNamespace, OutputHolder out, Set<DemoPmc> PMCs) {
        try {
            out.o("Current PMC is '", requestNamespace, "'<br/>");
            out.h("Usage:<br/><table>");
            for (ResetType t : EnumSet.allOf(ResetType.class)) {
//            if (t.pmcParam) {
                if (t.getPmcParam()) {
                    out.h("<tr><td>&nbsp;</td></tr>");

                    out.h("<tr><td>");
                    out.h("</td><td>", t.toString());
                    out.h("</td></tr>");
                    for (DemoPmc demoPmc : PMCs) {
                        out.h("<tr><td>&nbsp;&nbsp;<a href=\"");
                        out.h("?type=", t.name());
                        out.h("&pmc=", demoPmc.name());
                        out.h("\">");

                        out.h("?type=", t.name());
                        out.h("&pmc=", demoPmc.name());

                        out.h("</a></td><td>", "<b>" + demoPmc.name() + "</b> &nbsp;", t.toString());
                        out.h("</td></tr>");
                    }
                    out.h("<tr><td>&nbsp;");
                    out.h("</td><td>");
                    out.h("</td></tr>");
                } else {
                    out.h("<tr><td><a href=\"");
                    out.h("?type=", t.name(), "\">");
                    out.h("?type=", t.name());
                    out.h("</a></td><td>", t.toString());
                    out.h("</td></tr>");
                }
            }
            out.h("</table>");
        } catch (Throwable t) {
            log.error("Error printing help: ", t);
            throw new Error(t);
        }
    }

    private String prodPmcNameCorrections(String name) {
        if (ApplicationMode.isDevelopment() || ApplicationMode.isDemo()) {
            return name;
        } else {
            return name;
        }
    }

    private static String ensurePmc(String pmc) {
        if (pmc == null) {
            pmc = NamespaceManager.getNamespace();
        }
        return pmc;
    }
}
