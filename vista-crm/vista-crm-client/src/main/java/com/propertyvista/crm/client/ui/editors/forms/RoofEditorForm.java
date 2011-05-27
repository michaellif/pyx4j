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

        main.add(new CrmHeaderDecorator(i18n.tr("Information")));
        main.add(inject(proto().type()), 15);
        main.add(inject(proto().year()), 8.2);

        main.add(new CrmHeaderDecorator(i18n.tr(proto().warranty().getMeta().getCaption())));
        SubtypeInjectors.injectWarranty(main, proto().warranty(), this);

        main.add(new CrmHeaderDecorator(i18n.tr(proto().maitenance().getMeta().getCaption())));
        SubtypeInjectors.injectMaintenance(main, proto().maitenance(), this);

        main.add(inject(proto().notes()), 25);

        return main;
    }
}