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
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.misc.VistaTODO;

public class ServiceForm extends CrmEntityForm<Service> {

    private static final I18n i18n = I18n.get(ServiceForm.class);

    public ServiceForm(IForm<Service> view) {
        super(Service.class, view);

        Tab tab = addTab(createGeneralTab(i18n.tr("General")));
        selectTab(tab);

// TODO uncomment in final version:        
//        if (!VistaFeatures.instance().genericProductCatalog()) {
        addTab(createEligibilityTab(i18n.tr("Eligibility")));
//        }
    }

    public TwoColumnFlexFormPanel createGeneralTab(String title) {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel(title);

        int row = -1;
        main.setH1(++row, 0, 2, i18n.tr("Information"));
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().type(), new CEnumLabel()), 20).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().version().name()), 20).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().version().description()), 55).build());
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.setH1(++row, 0, 2, i18n.tr("Items"));
        main.setWidget(++row, 0, inject(proto().version().items(), new ServiceItemFolder(this)));
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        return main;
    }

    public TwoColumnFlexFormPanel createEligibilityTab(String title) {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel(title);

        int row = -1;
        main.setH1(++row, 0, 1, i18n.tr("Features"));
        main.setWidget(++row, 0, inject(proto().version().features(), new ServiceFeatureFolder(isEditable(), this)));

        if (!VistaTODO.VISTA_1756_Concessions_Should_Be_Hidden) {
            main.setH1(++row, 0, 1, i18n.tr("Concessions"));
            main.setWidget(++row, 0, inject(proto().version().concessions(), new ServiceConcessionFolder(isEditable(), this)));
        }
        return main;
    }
}