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

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;

import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsSplitFlowPanel;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.components.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmHeader1Decorator;
import com.propertyvista.dto.LockerAreaDTO;

public class LockerAreaEditorForm extends CrmEntityForm<LockerAreaDTO> {

    public LockerAreaEditorForm() {
        super(LockerAreaDTO.class, new CrmEditorsComponentFactory());
    }

    public LockerAreaEditorForm(IEditableComponentFactory factory) {
        super(LockerAreaDTO.class, factory);
    }

    @Override
    public IsWidget createContent() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();
        VistaDecoratorsSplitFlowPanel split = new VistaDecoratorsSplitFlowPanel();

        main.add(split);
        split.getLeftPanel().add(inject(proto().name()), 15);
        split.getLeftPanel().add(inject(proto().description()), 15);
        split.getLeftPanel().add(inject(proto().isPrivate()), 7);
        split.getLeftPanel().add(inject(proto().lockerSize()), 7);
        split.getLeftPanel().add(inject(proto().levels()), 7);
        split.getRightPanel().add(inject(proto().totalLockers()), 7);
        split.getRightPanel().add(inject(proto().largeLockers()), 7);
        split.getRightPanel().add(inject(proto().regularLockers()), 7);
        split.getRightPanel().add(inject(proto().smallLockers()), 7);

        main.add(new CrmHeader1Decorator(i18n.tr("Financials")));
        main.add(split = new VistaDecoratorsSplitFlowPanel());

        split.getLeftPanel().add(inject(proto().largeRent()), 7);
        split.getLeftPanel().add(inject(proto().regularRent()), 7);
        split.getRightPanel().add(inject(proto().smallRent()), 7);

        main.setWidth("100%");
        return main;
    }
}