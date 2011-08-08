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
package com.propertyvista.crm.client.ui.crud.tenant.lead;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;

import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsSplitFlowPanel;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.tenant.lead.Showing;

public class ShowingEditorForm extends CrmEntityForm<Showing> {

    public ShowingEditorForm() {
        super(Showing.class, new CrmEditorsComponentFactory());
    }

    public ShowingEditorForm(IEditableComponentFactory factory) {
        super(Showing.class, factory);
    }

    @Override
    public IsWidget createContent() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());
        VistaDecoratorsSplitFlowPanel split = new VistaDecoratorsSplitFlowPanel(!isEditable());

        main.add(split);
        split.getLeftPanel().add(inject(proto().building()), 20);
        split.getLeftPanel().add(inject(proto().unit()), 20);

        split.getRightPanel().add(inject(proto().status()), 12);
        split.getRightPanel().add(inject(proto().result()), 12);
        split.getRightPanel().add(inject(proto().reason()), 12);

        return new CrmScrollPanel(main);
    }
}
