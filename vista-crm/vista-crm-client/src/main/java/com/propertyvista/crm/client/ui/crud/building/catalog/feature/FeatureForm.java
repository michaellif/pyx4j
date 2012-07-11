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

import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.financial.offering.Feature;

public class FeatureForm extends CrmEntityForm<Feature> {

    private static final I18n i18n = I18n.get(FeatureForm.class);

    public FeatureForm() {
        this(false);
    }

    public FeatureForm(boolean viewMode) {
        super(Feature.class, viewMode);
    }

    @Override
    public void createTabs() {
        FormFlexPanel content = new FormFlexPanel(i18n.tr("General"));

        int row = -1;
        content.setH1(++row, 0, 2, i18n.tr("Information"));
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().version().type(), new CLabel()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().version().name()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().version().description()), 50).build());
        content.getFlexCellFormatter().setColSpan(row, 0, 2);

        content.setH1(++row, 0, 2, i18n.tr("Items"));

        content.setWidget(++row, 0, inject(proto().version().items(), new FeatureItemFolder(this)));
        content.getFlexCellFormatter().setColSpan(row, 0, 2);

        row = 0;
        content.setWidget(++row, 1, new DecoratorBuilder(inject(proto().version().mandatory()), 4).build());
        content.setWidget(++row, 1, new DecoratorBuilder(inject(proto().version().recurring()), 4).build());

        content.getColumnFormatter().setWidth(0, "50%");
        content.getColumnFormatter().setWidth(1, "50%");

        selectTab(addTab(content));
    }
}