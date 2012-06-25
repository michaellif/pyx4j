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
package com.propertyvista.crm.client.ui.crud.building.lockers;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.IDecorator;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.crm.client.themes.CrmTheme;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.dto.LockerAreaDTO;

public class LockerAreaForm extends CrmEntityForm<LockerAreaDTO> {

    private static final I18n i18n = I18n.get(LockerAreaForm.class);

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(CrmTheme.defaultTabHeight, Unit.EM);

    public LockerAreaForm() {
        this(false);
    }

    public LockerAreaForm(boolean viewMode) {
        super(LockerAreaDTO.class, viewMode);
    }

    @Override
    protected IDecorator createDecorator() {
        return null;
    }

    @Override
    public IsWidget createContent() {

//        tabPanel.add(isEditable() ? new HTML() : ((LockerAreaView) getParentView()).getDashboardView().asWidget(), i18n.tr("Dashboard"));
        tabPanel.add(createDetailsTab(), i18n.tr("Details"));
        tabPanel.add(isEditable() ? new HTML() : new ScrollPanel(((LockerAreaViewerView) getParentView()).getLockerView().asWidget()), i18n.tr("Lockers"));
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
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().isPrivate()), 3).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().levels()), 3).build());

        row = -1;
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().totalLockers()), 3).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().regularLockers()), 3).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().largeLockers()), 3).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().smallLockers()), 3).build());

        main.getColumnFormatter().setWidth(0, "55%");
        main.getColumnFormatter().setWidth(1, "45%");

        return new ScrollPanel(main);
    }
}