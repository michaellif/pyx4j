/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 8, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.pmsite.server.panels;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import com.pyx4j.server.contexts.Context;
import com.pyx4j.server.contexts.Visit;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.pmsite.server.PMSiteContentManager;

public class GwtInclude extends Panel {

    private static final long serialVersionUID = 1L;

    public GwtInclude(String id) {
        super(id);

        Label script = new Label("gwtAuthenticationToken", "function gwtToken() { return \"" + getAuthenticationToken()
                + "\";} function gwtPortalGoogleAPIKey() { return \"" + VistaDeployment.getPortalGoogleAPIKey() + "\";}");
        script.setEscapeModelStrings(false); // do not HTML escape JavaScript code
        add(script);

        WebMarkupContainer relativeGwtInclude = new WebMarkupContainer("gwtResidentsJs");
        relativeGwtInclude.add(new AttributeAppender("src", Model.of(PMSiteContentManager.getPortalContextPath() + "sitegwt/sitegwt.nocache.js")));
        add(relativeGwtInclude);

    }

    private String getAuthenticationToken() {
        Visit visit = Context.getVisit();
        if (visit != null) {
            return visit.getSessionToken();
        } else {
            return "";
        }
    }

}
