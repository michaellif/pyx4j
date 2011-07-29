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
package com.propertyvista.crm.client.ui.crud.marketing;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;

import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsSplitFlowPanel;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.financial.Concession;

public class ConcessionEditorForm extends CrmEntityForm<Concession> {

    public ConcessionEditorForm() {
        super(Concession.class, new CrmEditorsComponentFactory());
    }

    public ConcessionEditorForm(IEditableComponentFactory factory) {
        super(Concession.class, factory);
    }

    @Override
    public IsWidget createContent() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();
        VistaDecoratorsSplitFlowPanel split = new VistaDecoratorsSplitFlowPanel();

        main.add(split);
        split.getLeftPanel().add(inject(proto().type()), 15);
        split.getLeftPanel().add(inject(proto().value()), 7);
        split.getLeftPanel().add(inject(proto().condition()), 7);
        split.getRightPanel().add(inject(proto().status()), 15);
        split.getLeftPanel().add(inject(proto().approvedBy()), 20);
        split.getLeftPanel().add(inject(proto().start()), 8.2);
        split.getLeftPanel().add(inject(proto().end()), 8.2);

        return new CrmScrollPanel(main);
    }
}
