/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-08
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.reports;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import com.pyx4j.i18n.shared.I18n;

public class NoResultsHtml {

    private static final I18n i18n = I18n.get(NoResultsHtml.class);

    public static SafeHtml get() {
        SafeHtmlBuilder b = new SafeHtmlBuilder();
        b.appendHtmlConstant("<div style=\"text-align: center;\">");
        b.appendEscaped(i18n.tr("No results that match the query."));
        b.appendHtmlConstant("</div>");
        return b.toSafeHtml();

    }
}
