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

import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.site.client.ui.prime.misc.CEntityCrudHyperlink;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.building.catalog.ProductDepositEditor;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.misc.VistaTODO;

public class ServiceForm extends CrmEntityForm<Service> {

    private static final I18n i18n = I18n.get(ServiceForm.class);

    private Widget headerLMR, headerMoveIn, headerSecurity;

    public ServiceForm(IForm<Service> view) {
        super(Service.class, view);

        selectTab(addTab(createGeneralTab()));
        addTab(createItemsTab());
        addTab(createEligibilityTab());
    }

    public TwoColumnFlexFormPanel createGeneralTab() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(i18n.tr("General"));

        int row = -1;
        content.setH1(++row, 0, 2, i18n.tr("Information"));
        content.setWidget(++row, 0,
                new FormDecoratorBuilder(inject(proto().code(), new CEntityCrudHyperlink<ARCode>(AppPlaceEntityMapper.resolvePlace(ARCode.class))), 20).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().version().name()), 20).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().version().description()), 20).build());

        row = 0;
        content.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().expiredFrom()), 10).build());

        content.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().version().price()), 10).build());
        content.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().version().availableOnline()), 4).build());

        content.setH1(++row, 0, 2, i18n.tr("Deposits"));

        content.setH3(++row, 0, 2, i18n.tr("Last Month Rent"));
        headerLMR = content.getWidget(row, 0);
        content.setWidget(++row, 0, 2, inject(proto().version().depositLMR(), new ProductDepositEditor()));

        content.setH3(++row, 0, 2, i18n.tr("Move In"));
        headerMoveIn = content.getWidget(row, 0);
        content.setWidget(++row, 0, 2, inject(proto().version().depositMoveIn(), new ProductDepositEditor()));

        content.setH3(++row, 0, 2, i18n.tr("Security"));
        headerSecurity = content.getWidget(row, 0);
        content.setWidget(++row, 0, 2, inject(proto().version().depositSecurity(), new ProductDepositEditor()));

        // tweaks:
        ProductDepositEditor dpe;

        dpe = (ProductDepositEditor) get(proto().version().depositLMR());
        dpe.get(dpe.proto().depositType()).setEditable(false);

        dpe = (ProductDepositEditor) get(proto().version().depositMoveIn());
        dpe.get(dpe.proto().depositType()).setEditable(false);

        dpe = (ProductDepositEditor) get(proto().version().depositSecurity());
        dpe.get(dpe.proto().depositType()).setEditable(false);

        return content;
    }

    public TwoColumnFlexFormPanel createItemsTab() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(i18n.tr("Items"));

        content.setWidget(0, 0, 2, inject(proto().version().items(), new ServiceItemFolder(this)));

        return content;
    }

    public TwoColumnFlexFormPanel createEligibilityTab() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(i18n.tr("Eligibility"));

        int row = -1;
        content.setH1(++row, 0, 1, i18n.tr("Features"));
        content.setWidget(++row, 0, inject(proto().version().features(), new ServiceFeatureFolder(isEditable(), this)));

        if (!VistaTODO.VISTA_1756_Concessions_Should_Be_Hidden) {
            content.setH1(++row, 0, 1, i18n.tr("Concessions"));
            content.setWidget(++row, 0, inject(proto().version().concessions(), new ServiceConcessionFolder(isEditable(), this)));
        }
        return content;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().expiredFrom()).setVisible(isEditable() || !getValue().expiredFrom().isNull());

        if (!isEditable()) {
            headerLMR.setVisible(getValue().version().depositLMR().enabled().isBooleanTrue());
            get(proto().version().depositLMR()).setVisible(getValue().version().depositLMR().enabled().isBooleanTrue());

            headerMoveIn.setVisible(getValue().version().depositMoveIn().enabled().isBooleanTrue());
            get(proto().version().depositMoveIn()).setVisible(getValue().version().depositMoveIn().enabled().isBooleanTrue());

            headerSecurity.setVisible(getValue().version().depositSecurity().enabled().isBooleanTrue());
            get(proto().version().depositSecurity()).setVisible(getValue().version().depositSecurity().enabled().isBooleanTrue());
        }
    }
}