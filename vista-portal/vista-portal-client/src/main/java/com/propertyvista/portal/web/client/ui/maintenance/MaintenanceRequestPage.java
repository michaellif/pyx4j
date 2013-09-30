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
package com.propertyvista.portal.web.client.ui.maintenance;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.portal.rpc.portal.web.dto.maintenance.MaintenanceRequestDTO;
import com.propertyvista.portal.web.client.themes.EntityViewTheme;
import com.propertyvista.portal.web.client.ui.CPortalEntityForm;
import com.propertyvista.portal.web.client.ui.profile.ProfilePageView.ProfilePagePresenter;

public class MaintenanceRequestPage extends CPortalEntityForm<MaintenanceRequestDTO> {

    private static final I18n i18n = I18n.get(MaintenanceRequestPage.class);

    private ProfilePagePresenter presenter;

    public MaintenanceRequestPage(MaintenanceRequestPageViewImpl view) {
        super(MaintenanceRequestDTO.class, view, "Maintenance Request", ThemeColor.contrast2);
        asWidget().setStyleName(EntityViewTheme.StyleName.EntityView.name());
    }

    public void setPresenter(ProfilePagePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel mainPanel = new BasicFlexFormPanel();
        int row = -1;

        mainPanel.setH1(++row, 0, 1, i18n.tr("Basic Information"));

        return mainPanel;
    }

}
