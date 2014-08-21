/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 13, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.ui;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.i18n.shared.I18n;

public class WelcomeViewImpl extends VerticalPanel implements WelcomeView {

    private static final I18n i18n = I18n.get(WelcomeViewImpl.class);

    public WelcomeViewImpl() {
        setSize("100%", "100%");
        setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

        FlexTable content = new FlexTable();

        HTML msg = new HTML(i18n.tr("Welcome to CRM"));
        Style msgStyle = msg.getElement().getStyle();
        msgStyle.setProperty("fontSize", "20px");
        msgStyle.setProperty("fontWeight", "bold");
        msgStyle.setProperty("color", "green");
        msgStyle.setProperty("padding", "20px 30px");
        msgStyle.setProperty("border", "1px solid #ddd");

        content.setWidget(0, 0, msg);
        add(content);
    }
}
