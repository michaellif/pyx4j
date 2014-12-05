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
package com.propertyvista.portal.shared.ui.communityevent;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.domain.property.asset.CommunityEvent;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.shared.themes.EntityViewTheme;
import com.propertyvista.portal.shared.themes.NavigationAnchorTheme;
import com.propertyvista.portal.shared.ui.CPortalEntityForm;

public class CommunityEventPage extends CPortalEntityForm<CommunityEvent> {

    private static final I18n i18n = I18n.get(CommunityEventPage.class);

    public CommunityEventPage(CommunityEventPageViewImpl view) {
        super(CommunityEvent.class, view, "Community Event", ThemeColor.contrast2);
        asWidget().setStyleName(EntityViewTheme.StyleName.EntityView.name());
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.h1(i18n.tr("Security"));

        Anchor anchor = new Anchor("Change my Password");

        formPanel.append(Location.Left, anchor);

        formPanel.h1(i18n.tr("Mail Preferences"));

        HTML label = new HTML("Coming soon.");
        label.setWidth("200px");
        label.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        label.getElement().getStyle().setTextAlign(TextAlign.LEFT);
        formPanel.append(Location.Left, label);

        formPanel.h1(i18n.tr("Notification Preferences"));

        label = new HTML("Coming soon.");
        label.setWidth("200px");
        label.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        label.getElement().getStyle().setTextAlign(TextAlign.LEFT);
        formPanel.append(Location.Left, label);

        return formPanel;
    }

}
