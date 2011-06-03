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
import com.propertyvista.crm.client.ui.components.SubtypeInjectors;
import com.propertyvista.crm.client.ui.decorations.CrmHeader1Decorator;
import com.propertyvista.dto.RoofDTO;

public class RoofEditorForm extends CrmEntityForm<RoofDTO> {

    public RoofEditorForm() {
        super(RoofDTO.class, new CrmEditorsComponentFactory());
    }

    public RoofEditorForm(IEditableComponentFactory factory) {
        super(RoofDTO.class, factory);
    }

    @Override
    public IsWidget createContent() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();
        VistaDecoratorsSplitFlowPanel split = new VistaDecoratorsSplitFlowPanel();

        main.add(split);
        split.getLeftPanel().add(inject(proto().type()), 15);
        split.getRightPanel().add(inject(proto().year()), 8.2);

        main.add(new CrmHeader1Decorator(i18n.tr(proto().warranty().getMeta().getCaption())));
        main.add(split = new VistaDecoratorsSplitFlowPanel());
        SubtypeInjectors.injectWarranty(main, split, proto().warranty(), this);

        main.add(new CrmHeader1Decorator(i18n.tr(proto().maintenance().getMeta().getCaption())));
        main.add(split = new VistaDecoratorsSplitFlowPanel());
        SubtypeInjectors.injectMaintenance(main, split, proto().maintenance(), this);

        split.getLeftPanel().add(inject(proto().notes()), 25);

        main.setWidth("100%");
        return main;
    }
}