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
package com.propertyvista.crm.client.ui.crud.building.catalog.service;

import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.misc.VistaTODO;

public class ServiceForm extends CrmEntityForm<Service> {

    private static final I18n i18n = I18n.get(ServiceForm.class);

    public ServiceForm() {
        this(false);
    }

    public ServiceForm(boolean viewMode) {
        super(Service.class, viewMode);
    }

    @Override
    public void createTabs() {

        Tab tab = addTab(createGeneralTab(i18n.tr("General")));
        selectTab(tab);

        addTab(createEligibilityTab(i18n.tr("Eligibility")));

    }

    public FormFlexPanel createGeneralTab(String title) {
        FormFlexPanel main = new FormFlexPanel(title);

        int row = -1;
        main.setH1(++row, 0, 2, i18n.tr("Information"));
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().version().type(), new CEnumLabel()), 20).build());

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().version().name()), 20).build());
        main.setWidget(row, 1, new DecoratorBuilder(inject(proto().version().visibility()), 10).build());

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().version().description()), 55).build());
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.setH1(++row, 0, 2, i18n.tr("Items"));
        main.setWidget(++row, 0, inject(proto().version().items(), new ServiceItemFolder(this)));
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.getColumnFormatter().setWidth(0, "50%");
        main.getColumnFormatter().setWidth(1, "50%");

        return main;
    }

    public FormFlexPanel createEligibilityTab(String title) {
        FormFlexPanel main = new FormFlexPanel(title);

        int row = -1;
        main.setH1(++row, 0, 1, i18n.tr("Features"));
        main.setWidget(++row, 0, inject(proto().version().features(), new ServiceFeatureFolder(isEditable(), this)));

        if (!VistaTODO.removedForProduction) {
            main.setH1(++row, 0, 1, i18n.tr("Concessions"));
            main.setWidget(++row, 0, inject(proto().version().concessions(), new ServiceConcessionFolder(isEditable(), this)));
        }
        return main;
    }
}