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

import org.apache.wicket.model.Model;

import com.propertyvista.pmsite.server.panels.AptDetailsPanel;

public class AptDetailsPage extends BasePage {

    private static final long serialVersionUID = 1L;

    public AptDetailsPage() {
        super();
        Long propId = null;
        try {
            propId = getRequest().getRequestParameters().getParameterValue("propId").toLong();
        } catch (java.lang.NumberFormatException ignore) {
            // do nothing
        }
        if (propId == null) {
            setResponsePage(AptListPage.class);
            return;
        }

        add(new AptDetailsPanel("aptDetailsPanel", new Model<Long>(propId)));
    }

}
