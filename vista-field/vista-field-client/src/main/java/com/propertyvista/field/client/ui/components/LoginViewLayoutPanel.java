/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-30
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.field.client.ui.components;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.propertyvista.field.client.theme.FieldTheme;
import com.propertyvista.field.client.ui.login.LoginViewImpl.LoginHtmlTemplates;

public class LoginViewLayoutPanel extends Composite {

    private final FlowPanel header;

    private final FlowPanel footer;

    private final FlowPanel content;

    public LoginViewLayoutPanel() {
        VerticalPanel viewPanel = new VerticalPanel();
        viewPanel.setStyleName(FieldTheme.StyleName.LoginViewPanel.name());

        header = new FlowPanel();
        header.setStyleName(FieldTheme.StyleName.LoginViewSectionHeader.name());
        viewPanel.add(header);

        content = new FlowPanel();
        content.setStyleName(FieldTheme.StyleName.LoginViewSectionContent.name());
        viewPanel.add(content);

        footer = new FlowPanel();
        footer.setStyleName(FieldTheme.StyleName.LoginViewSectionFooter.name());
        viewPanel.add(footer);

        HTML orLine = makeOrLineDecoration();
        viewPanel.add(orLine);

        initWidget(viewPanel);
    }

    public FlowPanel getHeader() {
        return header;
    }

    public FlowPanel getContent() {
        return content;
    }

    public FlowPanel getFooter() {
        return footer;
    }

    private HTML makeOrLineDecoration() {
        HTML orLine = new HTML(LoginHtmlTemplates.TEMPLATES.orLineSeparator(FieldTheme.StyleName.LoginOrLineSeparator.name()));
        return orLine;
    }

}
