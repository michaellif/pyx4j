/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 20, 2013
 * @author dev_vista
 * @version $Id$
 */
package com.propertyvista.pmsite.server.pages;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.DefaultMarkupResourceStreamProvider;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.util.resource.IResourceStream;

import templates.TemplateResources;

public class MyCommunityPage extends ResidentsPage {
    private static final long serialVersionUID = 1L;

    public MyCommunityPage() {
        super();

        if (getCM().isCustomResidentsContentEnabled()) {
            add(new Label("header", "This is custom Header"));
            add(new Label("footer", "This is custom Footer"));
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        if (getCM().isCustomResidentsContentEnabled()) {
            response.renderCSSReference(new CssResourceReference(TemplateResources.class, "myCommunity/css/style.css"));
        }
        super.renderHead(response);
    }

    @Override
    public IResourceStream getMarkupResourceStream(MarkupContainer container, Class<?> containerClass) {
        if (!getCM().isCustomResidentsContentEnabled() && containerClass == getClass()) {
            containerClass = ResidentsPage.class;
        }
        IResourceStream markup = new DefaultMarkupResourceStreamProvider().getMarkupResourceStream(container, containerClass);
        return markup;
    }
}
