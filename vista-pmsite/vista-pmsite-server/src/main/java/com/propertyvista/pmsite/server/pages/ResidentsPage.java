/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 22, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.pmsite.server.pages;

import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.IHeaderResponse;

import templates.TemplateResources;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.model.StylesheetTemplateModel;
import com.propertyvista.pmsite.server.model.WicketUtils.VolatileTemplateResourceReference;
import com.propertyvista.pmsite.server.panels.GwtInclude;

@AuthorizeInstantiation(Roles.USER)
public class ResidentsPage extends BasePage {

    private static final long serialVersionUID = 1L;

    private static final I18n i18n = I18n.get(ResidentsPage.class);

    public ResidentsPage() {
        super();

        add(new GwtInclude("gwtInclude"));
    }

    @Override
    public String getLocalizedPageTitle() {
        return i18n.tr("Residents");
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        int styleId = ((PMSiteWebRequest) getRequest()).getContentManager().getStyleId();
        String fileCSS = "resident" + styleId + ".css";
        VolatileTemplateResourceReference refCSS = new VolatileTemplateResourceReference(TemplateResources.class, fileCSS, "text/css",
                new StylesheetTemplateModel(getRequest()));
        response.renderCSSReference(refCSS);
        super.renderHead(response);

    }
}
