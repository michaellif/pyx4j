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

import org.apache.wicket.markup.html.IHeaderResponse;

import templates.TemplateResources;

import com.propertyvista.pmsite.server.PMSiteContentManager;
import com.propertyvista.pmsite.server.model.StylesheetTemplateModel;
import com.propertyvista.pmsite.server.model.WicketUtils.VolatileTemplateResourceReference;
import com.propertyvista.pmsite.server.panels.AdvancedSearchCriteriaPanel;

public class FindAptPage extends BasePage {

    private static final long serialVersionUID = 1L;

    public FindAptPage() {
        super();
        setVersioned(false);

        add(new AdvancedSearchCriteriaPanel());

    }

    @Override
    public void renderHead(IHeaderResponse response) {
        int styleId = PMSiteContentManager.getSiteStyle();
        String fileCSS = "findapt" + styleId + ".css";
        VolatileTemplateResourceReference refCSS = new VolatileTemplateResourceReference(TemplateResources.class, fileCSS, "text/css",
                new StylesheetTemplateModel(String.valueOf(styleId)));
        response.renderCSSReference(refCSS);
        super.renderHead(response);

    }
}
