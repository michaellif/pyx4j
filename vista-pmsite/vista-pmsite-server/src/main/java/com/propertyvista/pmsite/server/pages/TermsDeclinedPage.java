/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 27, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.pmsite.server.pages;

import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import templates.TemplateResources;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.pmsite.server.PMSiteSession;
import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.model.WicketUtils.VolatileTemplateResourceReference;

public class TermsDeclinedPage extends BasePage {
    private static final long serialVersionUID = 1L;

    private static final I18n i18n = I18n.get(TermsDeclinedPage.class);

    public TermsDeclinedPage() {
        this(null);
    }

    public TermsDeclinedPage(PageParameters pp) {
        super(pp);

        if (!PMSiteSession.get().isSignedIn() || !PMSiteSession.get().getRoles().hasRole(PMSiteSession.VistaTermsAcceptanceRequiredRole)) {
            setResponsePage(getApplication().getHomePage());
        }
        getSession().invalidateNow();

        add(new Label("legalTitle", i18n.tr("Vista Terms and Conditions Declined")));
        add(new Label("legalContent", i18n.tr("You must accept the Terms of Use and Privacy Policy to use this application!")));
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

    @Override
    public String getLocalizedPageTitle() {
        return i18n.tr("Legal Terms Declined");
    }
}
