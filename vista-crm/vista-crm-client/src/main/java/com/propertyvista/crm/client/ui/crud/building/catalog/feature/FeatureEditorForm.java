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
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
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
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().type(), new CLabel()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().isMandatory()), 4).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().description()), 50).build());
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.setH1(++row, 0, 2, i18n.tr("Items"));

        main.setWidget(++row, 0, inject(proto().items(), new FeatureItemFolder(this)));
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        row = -1;
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().priceType()), 18).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().depositType()), 15).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().isRecurring()), 4).build());

        main.getColumnFormatter().setWidth(0, "50%");
        main.getColumnFormatter().setWidth(1, "50%");

        return new CrmScrollPanel(main);
    }
}