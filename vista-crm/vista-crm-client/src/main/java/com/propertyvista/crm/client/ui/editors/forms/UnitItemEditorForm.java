/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
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
import com.propertyvista.domain.property.asset.unit.AptUnitItem;

public class UnitItemEditorForm extends CrmEntityForm<AptUnitItem> {

    public UnitItemEditorForm() {
        super(AptUnitItem.class, new CrmEditorsComponentFactory());
    }

    public UnitItemEditorForm(IEditableComponentFactory factory) {
        super(AptUnitItem.class, factory);
    }

    @Override
    public IsWidget createContent() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();
        VistaDecoratorsSplitFlowPanel split = new VistaDecoratorsSplitFlowPanel();

        main.add(split);
        split.getLeftPanel().add(inject(proto().type()), 15);
        split.getLeftPanel().add(inject(proto().description()), 15);
        split.getRightPanel().add(inject(proto().conditionNotes()), 7);

        main.add(new CrmHeaderDecorator(i18n.tr("Details")));

        main.add(split = new VistaDecoratorsSplitFlowPanel());
        split.getLeftPanel().add(inject(proto().wallColour()), 15);
        split.getLeftPanel().add(inject(proto().flooringType()), 15);
        split.getLeftPanel().add(inject(proto().flooringInstallDate()), 8);
        split.getLeftPanel().add(inject(proto().flooringValue()), 7);
        split.getLeftPanel().add(inject(proto().counterTopType()), 15);
        split.getRightPanel().add(inject(proto().counterTopInstallDate()), 8);
        split.getRightPanel().add(inject(proto().counterTopValue()), 7);
        split.getRightPanel().add(inject(proto().cabinetsType()), 15);
        split.getRightPanel().add(inject(proto().cabinetsInstallDate()), 8);
        split.getRightPanel().add(inject(proto().cabinetsValue()), 7);

        main.setWidth("100%");
        return main;
    }
}
