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
package com.propertyvista.portal.shared.ui.account;

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.shared.dto.CustomerAccountDTO;
import com.propertyvista.portal.shared.themes.EntityViewTheme;
import com.propertyvista.portal.shared.themes.NavigationAnchorTheme;
import com.propertyvista.portal.shared.ui.CPortalEntityEditor;

public class AccountPage extends CPortalEntityEditor<CustomerAccountDTO> {

    private static final I18n i18n = I18n.get(AccountPage.class);

    public AccountPage(AccountPageViewImpl view) {
        super(CustomerAccountDTO.class, view, "My Account", ThemeColor.contrast2);
        asWidget().setStyleName(EntityViewTheme.StyleName.EntityView.name());
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.h1(i18n.tr("Security"));

        Anchor anchor = new Anchor(i18n.tr("Change my Password"), new Command() {

            @Override
            public void execute() {
                AppSite.getPlaceController().goTo(new PortalSiteMap.PasswordChange());
            }
        });
        anchor.setWidth("200px");
        anchor.getElement().getStyle().setTextAlign(TextAlign.LEFT);
        anchor.setStyleName(NavigationAnchorTheme.StyleName.NavigationAnchor.name());
        formPanel.append(Location.Left, anchor);

        formPanel.br();
        formPanel.br();
        formPanel.h1(i18n.tr("Communication Preferences"));
        formPanel.br();

        formPanel.h3(i18n.tr("Informational"));
        formPanel.append(Location.Left, proto().deliveryPreferences().informationalDelivery()).decorate().customLabel(i18n.tr("Delivery Frequency"));
        formPanel.br();
        formPanel.h3(i18n.tr("Promotional"));
        formPanel.append(Location.Left, proto().deliveryPreferences().promotionalDelivery()).decorate().customLabel(i18n.tr("Delivery Frequency"));

        return formPanel;
    }

}
