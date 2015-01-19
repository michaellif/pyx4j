/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-09
 * @author ArtyomB
 */
package com.propertyvista.crm.client.ui.crud.communication.selector;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import com.propertyvista.dto.communication.CommunicationEndpointDTO;

public class CommunicationEndpointForSelectionCell extends AbstractCell<CommunicationEndpointDTO> {

    @Override
    public void render(Context context, CommunicationEndpointDTO value, SafeHtmlBuilder sb) {
        if (value == null) {
            return;
        }
        sb.appendHtmlConstant("<div style=\"padding:3px\">");
        sb.appendEscaped(value.name().getValue());
        sb.appendEscaped(" ");
        sb.appendEscaped(value.type().getValue().toString());
        sb.appendHtmlConstant("</div>");
    }

}
