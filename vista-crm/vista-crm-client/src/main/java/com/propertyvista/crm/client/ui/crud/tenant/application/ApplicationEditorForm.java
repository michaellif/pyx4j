/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.tenant.application;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.IsWidget;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.crm.client.themes.CrmTheme;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.dto.OnlineApplicationDTO;

public class ApplicationEditorForm extends CrmEntityForm<OnlineApplicationDTO> {

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(CrmTheme.defaultTabHeight, Unit.EM);

    public ApplicationEditorForm() {
        this(false);
    }

    public ApplicationEditorForm(boolean viewMode) {
        super(OnlineApplicationDTO.class, viewMode);
    }

    @Override
    public IsWidget createContent() {

        tabPanel.setSize("100%", "100%");
        return tabPanel;
    }

    @Override
    public void setActiveTab(int index) {
        if (index < tabPanel.getWidgetCount()) {
            tabPanel.selectTab(index);
        }
    }

    @Override
    public int getActiveTab() {
        return tabPanel.getSelectedIndex();
    }
}