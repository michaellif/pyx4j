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
package com.propertyvista.crm.client.ui.crud.building.catalog.feature;


import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.forms.client.ui.CLabel;

import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsSplitFlowPanel;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.crm.client.ui.decorations.CrmSectionSeparator;
import com.propertyvista.domain.financial.offering.Feature;

public class FeatureEditorForm extends CrmEntityForm<Feature> {

    public FeatureEditorForm() {
        super(Feature.class, new CrmEditorsComponentFactory());
    }

    public FeatureEditorForm(IEditableComponentFactory factory) {
        super(Feature.class, factory);
    }

    @Override
    public IsWidget createContent() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());
        VistaDecoratorsSplitFlowPanel split = new VistaDecoratorsSplitFlowPanel(!isEditable());

        main.add(split);
        split.getLeftPanel().add(inject(proto().type(), new CLabel()), 10);
        split.getLeftPanel().add(inject(proto().name()), 10);
        split.getLeftPanel().add(inject(proto().isMandatory()), 4);

        split.getRightPanel().add(inject(proto().priceType()), 18);
        split.getRightPanel().add(inject(proto().depositType()), 15);
        split.getRightPanel().add(inject(proto().isRecurring()), 4);

        main.add(inject(proto().description()), 50);

        main.add(new CrmSectionSeparator(i18n.tr("Items:")));

        main.add(inject(proto().items(), new FeatureItemFolder(this)));

        return new CrmScrollPanel(main);
    }
}