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
package com.propertyvista.crm.client.ui.crud.building.parking;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.crm.client.themes.CrmTheme;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.dto.ParkingDTO;

public class ParkingForm extends CrmEntityForm<ParkingDTO> {

    private static final I18n i18n = I18n.get(ParkingForm.class);

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(CrmTheme.defaultTabHeight, Unit.EM);

    public ParkingForm() {
        this(false);
    }

    public ParkingForm(boolean viewMode) {
        super(ParkingDTO.class, viewMode);
    }

    @Override
    public IsWidget createContent() {

//        tabPanel.add(isEditable() ? new HTML() : new ScrollPanel(((ParkingViewerView) getParentView()).getDashboardView().asWidget(), i18n.tr("Dashboard"));
        tabPanel.add(createDetailsTab(), i18n.tr("Details"));
        tabPanel.add(isEditable() ? new HTML() : new ScrollPanel(((ParkingViewerView) getParentView()).getSpotView().asWidget()), i18n.tr("Spots"));
        tabPanel.setLastTabDisabled(isEditable());
        tabPanel.add(new ScrollPanel(new Label("Notes and attachments goes here... ")), i18n.tr("Notes & Attachments"));
        tabPanel.setLastTabDisabled(isEditable());

        tabPanel.setSize("100%", "100%");
        return tabPanel;
    }

    @Override
    public void setActiveTab(int index) {
        tabPanel.selectTab(index);
    }

    @Override
    public int getActiveTab() {
        return tabPanel.getSelectedIndex();
    }

    private Widget createDetailsTab() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().description()), 25).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().type()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().levels()), 3).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().totalSpaces()), 3).build());

        row = -1;
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().regularSpaces()), 3).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().disabledSpaces()), 3).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().wideSpaces()), 3).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().narrowSpaces()), 3).build());

        main.getColumnFormatter().setWidth(0, "55%");
        main.getColumnFormatter().setWidth(1, "45%");

        return new ScrollPanel(main);
    }
}