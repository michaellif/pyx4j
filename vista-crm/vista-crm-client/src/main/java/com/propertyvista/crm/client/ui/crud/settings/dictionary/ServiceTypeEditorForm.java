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
package com.propertyvista.crm.client.ui.crud.settings.dictionary;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.financial.offering.ServiceItemType;

public class ServiceTypeEditorForm extends CrmEntityForm<ServiceItemType> {

    private Widget serviceType;

    private Widget featureType;

    public ServiceTypeEditorForm() {
        super(ServiceItemType.class, new CrmEditorsComponentFactory());
    }

    public ServiceTypeEditorForm(IEditableComponentFactory factory) {
        super(ServiceItemType.class, factory);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, decorate(inject(proto().name()), 25));
        main.setWidget(++row, 0, serviceType = decorate(inject(proto().serviceType()), 25));
        main.setWidget(++row, 0, featureType = decorate(inject(proto().featureType()), 25));

        return new CrmScrollPanel(main);
    }

    @Override
    public void populate(ServiceItemType value) {

        serviceType.setVisible(false);
        featureType.setVisible(false);

        switch (value.type().getValue()) {
        case service:
            serviceType.setVisible(true);
            break;

        case feature:
            featureType.setVisible(true);
            break;
        }

        super.populate(value);
    }
}