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

import org.apache.wicket.PageParameters;
import org.apache.wicket.Response;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebComponent;

import com.propertyvista.pmsite.server.panels.NavigationItem;

public class StaticPage extends BasePage {

    public StaticPage(PageParameters parameters) {
        super(parameters);
        final String pageId = parameters.getString(NavigationItem.NAVIG_PARAMETER_NAME);

        add(new WebComponent("content") {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
                Response response = getRequestCycle().getResponse();
                response.write("<ul>");
                for (int i = 0; i < 5; i++)
                    response.write("<li>" + pageId + "</li>");
                response.write("</ul>");
            }
        });

    }

}
