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
package com.propertyvista.crm.client.ui.editors.forms;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;

import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsSplitFlowPanel;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.components.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmHeaderDecorator;
import com.propertyvista.dto.ParkingDTO;

public class ParkingEditorForm extends CrmEntityForm<ParkingDTO> {

    public ParkingEditorForm() {
        super(ParkingDTO.class, new CrmEditorsComponentFactory());
    }

    public ParkingEditorForm(IEditableComponentFactory factory) {
        super(ParkingDTO.class, factory);
    }

    @Override
    public IsWidget createContent() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();
        VistaDecoratorsSplitFlowPanel split = new VistaDecoratorsSplitFlowPanel();

        main.add(split);
        split.getLeftPanel().add(inject(proto().name()), 15);
        split.getLeftPanel().add(inject(proto().description()), 15);
        split.getLeftPanel().add(inject(proto().type()), 10);
        split.getLeftPanel().add(inject(proto().levels()), 7);
        split.getLeftPanel().add(inject(proto().totalSpaces()), 7);
        split.getRightPanel().add(inject(proto().disabledSpaces()), 7);
        split.getRightPanel().add(inject(proto().regularSpaces()), 7);
        split.getRightPanel().add(inject(proto().doubleSpaces()), 7);
        split.getRightPanel().add(inject(proto().narrowSpaces()), 7);

        main.add(new CrmHeaderDecorator(i18n.tr("Financials")));

        main.add(split = new VistaDecoratorsSplitFlowPanel());
        split.getLeftPanel().add(inject(proto().disableRent()), 7);
        split.getLeftPanel().add(inject(proto().regularRent()), 7);
        split.getLeftPanel().add(inject(proto().doubleRent()), 7);
        split.getRightPanel().add(inject(proto().narrowRent()), 7);
        split.getRightPanel().add(inject(proto().deposit()), 7);

        main.setWidth("100%");
        return main;
    }
}