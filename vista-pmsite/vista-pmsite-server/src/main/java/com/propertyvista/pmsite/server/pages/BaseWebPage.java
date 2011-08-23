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

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.resources.StyleSheetReference;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.resource.TextTemplateResourceReference;

public abstract class BaseWebPage extends WebPage {

    public BaseWebPage() {
        super();

        add(new StyleSheetReference("stylesheet", new TextTemplateResourceReference(BaseWebPage.class, "template1.css", "text/css",
                new LoadableDetachableModel<Map<String, Object>>() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public Map<String, Object> load() {
                        final Map<String, Object> vars = new HashMap<String, Object>();
                        vars.put("color1", "#bababa");
                        return vars;
                    }
                })));

        add(new BookmarkablePageLink("homeLink", HomeWebPage.class));
        add(new BookmarkablePageLink("servicesLink", HomeWebPage.class));
        add(new BookmarkablePageLink("aboutUsLink", HomeWebPage.class));
        add(new BookmarkablePageLink("contactLink", ContactWebPage.class));
    }
}
