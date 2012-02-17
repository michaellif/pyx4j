/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 22, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.pmsite.server.pages;

import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import templates.TemplateResources;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.model.WicketUtils.VolatileTemplateResourceReference;
import com.propertyvista.pmsite.server.panels.SignInPanel;

public final class SignInPage extends BasePage {
    private static final long serialVersionUID = 1L;

    private static final I18n i18n = I18n.get(SignInPage.class);

    public static final String SigninOnResetParam = "reset";

    public SignInPage() {
        this(null);
    }

    public SignInPage(final PageParameters parameters) {
        super(parameters);
        add(new SignInPanel("signInPanel"));

        if (!parameters.get(SigninOnResetParam).isNull()) {
            info(i18n.tr("A link to the password reset page was sent to your email address"));
        }
    }

    @Override
    public String getLocalizedPageTitle() {
        return i18n.tr("Sign In");
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        int styleId = ((PMSiteWebRequest) getRequest()).getContentManager().getStyleId();
        String fileCSS = "signin" + styleId + ".css";
        VolatileTemplateResourceReference refCSS = new VolatileTemplateResourceReference(TemplateResources.class, fileCSS, "text/css",
                ((PMSiteWebRequest) getRequest()).getStylesheetTemplateModel());
        response.renderCSSReference(refCSS);
        super.renderHead(response);

    }
}