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
package com.propertyvista.crm.client.ui.crud.building;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.site.client.ui.crud.IView;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsSplitFlowPanel;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.components.CrmEntityForm;
import com.propertyvista.dto.ParkingDTO;

public class ParkingEditorForm extends CrmEntityForm<ParkingDTO> {

    public ParkingEditorForm(IView<ParkingDTO> parentView) {
        this(new CrmEditorsComponentFactory(), parentView);
    }

    public ParkingEditorForm(IEditableComponentFactory factory, IView<ParkingDTO> parentView) {
        super(ParkingDTO.class, factory);
        setParentView(parentView);
    }

    @Override
    public IsWidget createContent() {

        VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(2.7, Unit.EM);
        tabPanel.addDisable(((ParkingView) getParentView()).getDashboardView().asWidget(), i18n.tr("Dashboard"));
        tabPanel.add(new ScrollPanel(createDetailsTab()), i18n.tr("Details"));
        tabPanel.addDisable(new ScrollPanel(((ParkingView) getParentView()).getSpotView().asWidget()), i18n.tr("Spots"));
        tabPanel.addDisable(new ScrollPanel(new Label("Notes and attachments goes here... ")), i18n.tr("Notes & Attachments"));

        tabPanel.setDisableMode(isEditable());
        tabPanel.setSize("100%", "100%");
        return tabPanel;
    }

    private Widget createDetailsTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();
        VistaDecoratorsSplitFlowPanel split = new VistaDecoratorsSplitFlowPanel();

        split.getLeftPanel().add(inject(proto().name()), 15);
        split.getLeftPanel().add(inject(proto().description()), 15);
        split.getLeftPanel().add(inject(proto().type()), 10);
        split.getLeftPanel().add(inject(proto().levels()), 7);
        split.getLeftPanel().add(inject(proto().totalSpaces()), 7);
        split.getRightPanel().add(inject(proto().regularSpaces()), 7);
        split.getRightPanel().add(inject(proto().disabledSpaces()), 7);
        split.getRightPanel().add(inject(proto().wideSpaces()), 7);
        split.getRightPanel().add(inject(proto().narrowSpaces()), 7);

        main.add(split);
        return main;
    }
}