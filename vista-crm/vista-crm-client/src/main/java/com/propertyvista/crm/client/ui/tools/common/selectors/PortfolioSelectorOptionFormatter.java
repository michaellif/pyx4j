/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 4, 2014
 * @author arminea
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.common.selectors;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.SimpleMessageFormat;

import com.propertyvista.crm.rpc.dto.selections.PortfolioForSelectionDTO;

public class PortfolioSelectorOptionFormatter implements IFormatter<PortfolioForSelectionDTO, SafeHtml> {

    @Override
    public SafeHtml format(PortfolioForSelectionDTO value) {
        SafeHtmlBuilder builder = new SafeHtmlBuilder();
        builder.appendHtmlConstant(SimpleMessageFormat.format("<div style=\"padding:5px;\"><div>{0}</div></div>", value.name().getValue()));
        return builder.toSafeHtml();
    }
}
