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
import com.propertyvista.crm.client.ui.components.CrmEntityForm;
import com.propertyvista.crm.client.ui.components.SubtypeInjectors;
import com.propertyvista.crm.client.ui.decorations.CrmHeaderDecorator;
import com.propertyvista.crm.client.ui.editors.CrmEditorsComponentFactory;
import com.propertyvista.dto.ElevatorDTO;

public class ElevatorEditorForm extends CrmEntityForm<ElevatorDTO> {

    public ElevatorEditorForm() {
        super(ElevatorDTO.class, new CrmEditorsComponentFactory());
    }

    public ElevatorEditorForm(IEditableComponentFactory factory) {
        super(ElevatorDTO.class, factory);
    }

    @Override
    public IsWidget createContent() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();

        main.add(new CrmHeaderDecorator(i18n.tr("Information")));

        main.add(inject(proto().type()), 15);
        main.add(inject(proto().description()), 15);
        main.add(inject(proto().make()), 15);
        main.add(inject(proto().model()), 15);
        main.add(inject(proto().build()), 15);

        main.add(new CrmHeaderDecorator(i18n.tr(proto().licence().getFieldName())));
        SubtypeInjectors.injectLicence(main, proto().licence(), this);

        main.add(new CrmHeaderDecorator(i18n.tr(proto().warranty().getFieldName())));
        SubtypeInjectors.injectWarranty(main, proto().warranty(), this);

        main.add(inject(proto().notes()), 15);

        main.add(inject(proto().isForMoveInOut()), 15);

        return main;
    }
}