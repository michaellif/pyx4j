/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-06
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.common.selectors;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.client.SafeHtmlTemplates.Template;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import com.propertyvista.crm.rpc.dto.selections.BuildingForSelectionDTO;

class BuildingForSelectionCell extends AbstractCell<BuildingForSelectionDTO> {

    interface Template extends SafeHtmlTemplates {

        @Template("<div style=\"padding:5px;\"><div><span style=\"font-weight:bold;\">{0} - {1}</span></div><div>{2}</div></div>")
        SafeHtml buildingTemplate(String propertyCode, String name, String address);

    }

    private static Template template;

    public BuildingForSelectionCell() {
        if (template == null) {
            template = GWT.create(Template.class);
        }
    }

    @Override
    public void render(Context context, BuildingForSelectionDTO value, SafeHtmlBuilder sb) {
        if (value != null) {
            sb.append(template.buildingTemplate(value.propertyCode().getValue(), value.name().getValue(), value.address().getValue()));
        }
    }

}
