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
package com.propertyvista.crm.client.ui.crud.building.mech;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;

import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsSplitFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaLineSeparator;
import com.propertyvista.common.client.ui.validators.PastDateValidation;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.components.SubtypeInjectors;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.crm.client.ui.decorations.CrmSectionSeparator;
import com.propertyvista.dto.BoilerDTO;

public class BoilerEditorForm extends MechlBaseEditorForm<BoilerDTO> {

    public BoilerEditorForm() {
        super(BoilerDTO.class, new CrmEditorsComponentFactory());
    }

    public BoilerEditorForm(IEditableComponentFactory factory) {
        super(BoilerDTO.class, factory);
    }

    @Override
    protected Widget createGeneralTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());
        VistaDecoratorsSplitFlowPanel split = new VistaDecoratorsSplitFlowPanel(!isEditable());
        main.add(split);

        split.getLeftPanel().add(inject(proto().type()), 15);
        split.getLeftPanel().add(inject(proto().make()), 15);
        split.getLeftPanel().add(inject(proto().model()), 15);
        split.getRightPanel().add(inject(proto().build()), 9);
        split.getRightPanel().add(inject(proto().description()), 20);

        main.add(new CrmSectionSeparator("Licence"));
        VistaDecoratorsSplitFlowPanel split2 = new VistaDecoratorsSplitFlowPanel(!isEditable());
        SubtypeInjectors.injectLicence(main, split2, proto().license(), this);
        main.add(split2);

        main.add(new VistaLineSeparator());
        main.add(new HTML("&nbsp"));
        main.add(inject(proto().notes()), 60);

        return new CrmScrollPanel(main);
    }

    @Override
    public void addValidations() {
        new PastDateValidation(get(proto().build()));
    }
}