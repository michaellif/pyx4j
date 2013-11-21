/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 18, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui.application;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.tenant.prospect.MasterOnlineApplicationStatus;
import com.propertyvista.portal.shared.themes.EntityViewTheme;
import com.propertyvista.portal.shared.ui.CPortalEntityForm;

public class ApplicationStatusPage extends CPortalEntityForm<MasterOnlineApplicationStatus> {

    private static final I18n i18n = I18n.get(ApplicationStatusPage.class);

    public ApplicationStatusPage(ApplicationStatusPageViewImpl view) {
        super(MasterOnlineApplicationStatus.class, view, "Application Status", ThemeColor.contrast2);
        asWidget().setStyleName(EntityViewTheme.StyleName.EntityView.name());
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel mainPanel = new BasicFlexFormPanel();
        int row = -1;

        HTML label = new HTML(
                "Your application has been reviewed and has successfully been approved! You will be contacted shortly from our office management team to make move-in arrangements");
        label.getElement().getStyle().setProperty("maxWidth", "500px");
        label.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        label.getElement().getStyle().setTextAlign(TextAlign.LEFT);
        mainPanel.setWidget(++row, 0, 1, label);

        return mainPanel;
    }

    @Override
    protected void onValuePropagation(MasterOnlineApplicationStatus value, boolean fireEvent, boolean populate) {
        System.out.println("++++++++++++++++" + value);
    }
}
