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

import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.propertyvista.pmsite.server.PMSiteContentManager;
import com.propertyvista.pmsite.server.panels.AptDetailsPanel;
import com.propertyvista.portal.domain.dto.PropertyDTO;

public class AptDetailsPage extends BasePage {

    private static final long serialVersionUID = 1L;

    public AptDetailsPage() {
        super();
        PageParameters params = getPageParameters();
        Integer propId = null;
        if (params == null || (propId = params.get("propId").toInt()) == null) {
            setResponsePage(AptListPage.class);
            return;
        }

        add(new AptDetailsPanel("aptDetailsPanel", new CompoundPropertyModel<PropertyDTO>(PMSiteContentManager.getPropertyDetails(propId))));
    }

}
