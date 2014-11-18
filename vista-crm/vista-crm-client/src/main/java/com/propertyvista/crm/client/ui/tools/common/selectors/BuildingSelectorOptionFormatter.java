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

import com.propertyvista.crm.rpc.dto.selections.BuildingForSelectionDTO;

public class BuildingSelectorOptionFormatter implements IFormatter<BuildingForSelectionDTO, SafeHtml> {

    @Override
    public SafeHtml format(BuildingForSelectionDTO value) {
        SafeHtmlBuilder builder = new SafeHtmlBuilder();
        builder.appendHtmlConstant(SimpleMessageFormat.format(
                "<div style=\"padding:2px;\"><div><span style=\"font-weight:bold;\">{0} - {1}</span></div><div>{2}</div></div>", value.propertyCode()
                        .getValue(), value.name().getValue(), value.address().getValue()));
        return builder.toSafeHtml();
    }
}
