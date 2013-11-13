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
package com.propertyvista.portal.resident.ui.profile;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.dto.ResidentAccountDTO;
import com.propertyvista.portal.shared.themes.EntityViewTheme;
import com.propertyvista.portal.shared.themes.NavigationAnchorTheme;
import com.propertyvista.portal.shared.ui.CPortalEntityForm;

public class AccountPage extends CPortalEntityForm<ResidentAccountDTO> {

    private static final I18n i18n = I18n.get(AccountPage.class);

    public AccountPage(AccountPageViewImpl view) {
        super(ResidentAccountDTO.class, view, "My Account", ThemeColor.contrast2);
        asWidget().setStyleName(EntityViewTheme.StyleName.EntityView.name());
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel mainPanel = new BasicFlexFormPanel();
        int row = -1;

        mainPanel.setH1(++row, 0, 1, i18n.tr("Security"));

        Anchor anchor = new Anchor("Change my Password", new Command() {

            @Override
            public void execute() {
                AppSite.getPlaceController().goTo(new PortalSiteMap.PasswordChange());
            }
        });
        anchor.setWidth("200px");
        anchor.getElement().getStyle().setTextAlign(TextAlign.LEFT);
        anchor.setStyleName(NavigationAnchorTheme.StyleName.NavigationAnchor.name());
        mainPanel.setWidget(++row, 0, anchor);

        mainPanel.setH1(++row, 0, 1, i18n.tr("Mail Preferences"));

        HTML label = new HTML("Coming soon.");
        label.setWidth("200px");
        label.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        label.getElement().getStyle().setTextAlign(TextAlign.LEFT);
        mainPanel.setWidget(++row, 0, 1, label);

        mainPanel.setH1(++row, 0, 1, i18n.tr("Notification Preferences"));

        label = new HTML("Coming soon.");
        label.setWidth("200px");
        label.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        label.getElement().getStyle().setTextAlign(TextAlign.LEFT);
        mainPanel.setWidget(++row, 0, 1, label);

        return mainPanel;
    }

}
