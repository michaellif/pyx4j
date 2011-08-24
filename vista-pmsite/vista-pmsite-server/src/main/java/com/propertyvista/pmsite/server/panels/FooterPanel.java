/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 23, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.pmsite.server.panels;

import org.apache.wicket.Response;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.panel.Panel;

public class FooterPanel extends Panel {

    private static final long serialVersionUID = 1L;

    public FooterPanel() {
        super("footer");

        add(new WebComponent("footer_locations") {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
                Response response = getRequestCycle().getResponse();
                response.write("<div style='height: 30px; position: relative; width: 960px;'>");
                response.write("footer_locations");
                response.write("</div>");
            }
        });

        add(new WebComponent("footer_links") {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
                Response response = getRequestCycle().getResponse();
                response.write("<div style='height: 30px; position: relative; width: 960px;'>");
                response.write("footer_links");
                response.write("</div>");
            }
        });

        add(new WebComponent("footer_legal") {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
                Response response = getRequestCycle().getResponse();
                response.write("<div style='height: 30px; position: relative; width: 960px;'>");
                response.write("footer_legal");
                response.write("</div>");
            }
        });

    }

}