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
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.common.selectors;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import com.propertyvista.crm.rpc.dto.selections.PortfolioForSelectionDTO;

public class PortfolioForSelectionCell extends AbstractCell<PortfolioForSelectionDTO> {

    @Override
    public void render(Context context, PortfolioForSelectionDTO value, SafeHtmlBuilder sb) {
        if (value == null) {
            return;
        }
        sb.appendHtmlConstant("<div style=\"padding:3px\">");
        sb.appendEscaped(value.name().getValue());
        sb.appendHtmlConstant("</div>");
    }

}
