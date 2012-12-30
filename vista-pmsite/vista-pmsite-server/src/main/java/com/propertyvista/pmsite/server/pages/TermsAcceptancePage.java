/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 26, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.pmsite.server.pages;

import java.util.List;
import java.util.concurrent.Callable;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import templates.TemplateResources;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.admin.domain.legal.LegalDocument;
import com.propertyvista.admin.domain.legal.VistaTerms;
import com.propertyvista.biz.tenant.CustomerFacade;
import com.propertyvista.pmsite.server.PMSiteApplication;
import com.propertyvista.pmsite.server.PMSiteSession;
import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.model.WicketUtils.VolatileTemplateResourceReference;
import com.propertyvista.portal.server.portal.services.PortalAuthenticationServiceImpl;
import com.propertyvista.server.jobs.TaskRunner;
import com.propertyvista.shared.i18n.CompiledLocale;

@AuthorizeInstantiation(PMSiteSession.VistaTermsAcceptanceRequiredRole)
public class TermsAcceptancePage extends BasePage {
    private static final long serialVersionUID = 1L;

    private static final I18n i18n = I18n.get(TermsAcceptancePage.class);

    private Key vistaTermsKey;

    public TermsAcceptancePage() {
        this(null);
    }

    public TermsAcceptancePage(PageParameters pp) {
        super(pp);

        // get legal document
        final CompiledLocale locale = ((PMSiteWebRequest) getRequest()).getSiteLocale().lang().getValue();
        String content = TaskRunner.runInAdminNamespace(new Callable<String>() {
            @Override
            public String call() {
                String result = null;
                String result_en = null;
                EntityQueryCriteria<VistaTerms> criteria = EntityQueryCriteria.create(VistaTerms.class);
                criteria.eq(criteria.proto().target(), VistaTerms.Target.Tenant);
                List<VistaTerms> list = Persistence.service().query(criteria);
                VistaTerms terms = list.get(0);
                setTermsKey(terms.version().getPrimaryKey());
                for (LegalDocument doc : terms.version().document()) {
                    CompiledLocale cl = doc.locale().getValue();
                    if (cl.equals(locale)) {
                        result = doc.content().getValue();
                        break;
                    } else if (cl.getLanguage().endsWith("en")) {
                        result_en = doc.content().getValue();
                    }
                }
                return (result != null ? result : result_en);
            }
        });
        if (content == null) {
            throw new RuntimeException("Vista Terms not found");
        }

        add(new Label("legalTitle", i18n.tr("Vista Terms and Conditions")));
        add(new Label("legalContent", content).setEscapeModelStrings(false));

        add(new StatelessForm<Boolean>("acceptForm") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onInitialize() {
                super.onInitialize();

                add(new Button("accept", Model.of(i18n.tr("I Accept"))) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onSubmit() {
                        Key userKey = Context.getVisit().getUserVisit().getPrincipalPrimaryKey();
                        ServerSideFactory.create(CustomerFacade.class).onVistaTermsAccepted(userKey, getTermsKey(), true);
                        new PortalAuthenticationServiceImpl().reAuthenticate(null);
                    }
                });

                add(new Button("decline", Model.of(i18n.tr("I Decline"))) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onSubmit() {
                        setResponsePage(TermsDeclinedPage.class);
                    }
                }.setDefaultFormProcessing(false));
            }

            @Override
            public void onSubmit() {
                // redirect to target
                PMSiteApplication.get().redirectToTarget();
            }
        });
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        String skin = ((PMSiteWebRequest) getRequest()).getContentManager().getSiteSkin();
        String fileCSS = skin + "/" + "terms.css";
        VolatileTemplateResourceReference refCSS = new VolatileTemplateResourceReference(TemplateResources.class, fileCSS, "text/css",
                ((PMSiteWebRequest) getRequest()).getStylesheetTemplateModel());
        response.renderCSSReference(refCSS);
        super.renderHead(response);
    }

    private void setTermsKey(Key termsKey) {
        vistaTermsKey = termsKey;
    }

    private Key getTermsKey() {
        return vistaTermsKey;
    }
}
