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
package com.propertyvista.crm.client.ui.crud.settings.financial.producttype;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.CEntityComboBox;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.financial.GlCode;
import com.propertyvista.domain.financial.offering.ProductItemType;

public class ServiceTypeEditorForm extends CrmEntityForm<ProductItemType> {

    private Widget serviceType;

    private Widget featureType;

    public ServiceTypeEditorForm() {
        this(false);
    }

    public ServiceTypeEditorForm(boolean viewMode) {
        super(ProductItemType.class, viewMode);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name()), 25).build());
        main.setWidget(++row, 0, serviceType = new DecoratorBuilder(inject(proto().serviceType()), 25).build());
        main.setWidget(++row, 0, featureType = new DecoratorBuilder(inject(proto().featureType()), 25).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().glCode(), new CEntityComboBox<GlCode>(GlCode.class)), 25).build());

        return new CrmScrollPanel(main);
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();

        serviceType.setVisible(false);
        featureType.setVisible(false);

        switch (getValue().type().getValue()) {
        case service:
            serviceType.setVisible(true);
            break;

        case feature:
            featureType.setVisible(true);
            break;
        }

    }
}