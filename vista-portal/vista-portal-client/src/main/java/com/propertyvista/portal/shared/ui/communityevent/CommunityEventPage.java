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

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.property.asset.CommunityEvent;
import com.propertyvista.portal.shared.themes.CommunityEventTheme;
import com.propertyvista.portal.shared.themes.EntityViewTheme;
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

        CLabel<String> caption = new CLabel<String>();
        caption.asWidget().setStyleName(CommunityEventTheme.StyleName.CommunityEventCaption.name());
        formPanel.append(Location.Dual, proto().caption(), caption);

        CLabel<String> date = new CLabel<String>();
        date.asWidget().setStyleName(CommunityEventTheme.StyleName.CommunityEventDate.name());
        formPanel.append(Location.Dual, proto().date(), date);

        CLabel<String> description = new CLabel<String>();
        description.asWidget().setStyleName(CommunityEventTheme.StyleName.CommunityEventDescription.name());
        formPanel.append(Location.Dual, proto().description(), description);

        return formPanel;
    }

}
