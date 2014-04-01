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

import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.CBooleanLabel;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.site.client.ui.prime.misc.CEntityCrudHyperlink;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.building.catalog.ProductDepositEditor;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.shared.config.VistaFeatures;

public class FeatureForm extends CrmEntityForm<Feature> {

    private static final I18n i18n = I18n.get(FeatureForm.class);

    private Widget headerDeposits, headerLMR, headerMoveIn, headerSecurity;

    public FeatureForm(IForm<Feature> view) {
        super(Feature.class, view);

        selectTab(addTab(createGeneralTab()));
        addTab(createItemsTab());
    }

    public TwoColumnFlexFormPanel createGeneralTab() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(i18n.tr("General"));

        int row = -1;
        content.setH1(++row, 0, 2, i18n.tr("Information"));
        content.setWidget(++row, 0,
                new FormDecoratorBuilder(inject(proto().code(), new CEntityCrudHyperlink<ARCode>(AppPlaceEntityMapper.resolvePlace(ARCode.class))), 20).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().version().name()), 20).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().version().description()), 20).build());

        int rrow = 0;
        content.setWidget(++rrow, 1, new FormDecoratorBuilder(inject(proto().expiredFrom()), 10).build());
        content.setWidget(++rrow, 1, new FormDecoratorBuilder(inject(proto().version().price()), 10).build());
        content.setWidget(++rrow, 1, new FormDecoratorBuilder(inject(proto().version().availableOnline()), 4).build());
        content.setWidget(++rrow, 1, new FormDecoratorBuilder(inject(proto().version().mandatory()), 4).build());
        content.setWidget(++rrow, 1, new FormDecoratorBuilder(inject(proto().version().recurring()), 4).build());
        if (VistaTODO.VISTA_2256_Default_Product_Catalog_Show) {
            content.setWidget(++rrow, 0, new FormDecoratorBuilder(inject(proto().defaultCatalogItem(), new CBooleanLabel()), 4).build());
        }

        row = Math.max(row, rrow);
        content.setH1(++row, 0, 2, i18n.tr("Deposits"));
        headerDeposits = content.getWidget(row, 0);

        content.setH3(++row, 0, 2, i18n.tr("Last Month Rent"));
        headerLMR = content.getWidget(row, 0);
        content.setWidget(++row, 0, 2, inject(proto().version().depositLMR(), new ProductDepositEditor()));

        if (!VistaFeatures.instance().yardiIntegration()) {
            content.setH3(++row, 0, 2, i18n.tr("Move In"));
            headerMoveIn = content.getWidget(row, 0);
            content.setWidget(++row, 0, 2, inject(proto().version().depositMoveIn(), new ProductDepositEditor()));

            content.setH3(++row, 0, 2, i18n.tr("Security"));
            headerSecurity = content.getWidget(row, 0);
            content.setWidget(++row, 0, 2, inject(proto().version().depositSecurity(), new ProductDepositEditor()));
        }

        // tweaks:
        ProductDepositEditor dpe;

        dpe = (ProductDepositEditor) get(proto().version().depositLMR());
        dpe.get(dpe.proto().depositType()).setEditable(false);

        if (!VistaFeatures.instance().yardiIntegration()) {
            dpe = (ProductDepositEditor) get(proto().version().depositMoveIn());
            dpe.get(dpe.proto().depositType()).setEditable(false);

            dpe = (ProductDepositEditor) get(proto().version().depositSecurity());
            dpe.get(dpe.proto().depositType()).setEditable(false);
        }

        return content;
    }

    public TwoColumnFlexFormPanel createItemsTab() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(i18n.tr("Items"));

        content.setWidget(0, 0, 2, inject(proto().version().items(), new FeatureItemFolder(this)));

        return content;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().expiredFrom()).setVisible(isEditable() || !getValue().expiredFrom().isNull());

        if (!isEditable()) {
            headerLMR.setVisible(getValue().version().depositLMR().enabled().getValue(false));
            get(proto().version().depositLMR()).setVisible(getValue().version().depositLMR().enabled().getValue(false));

            if (!VistaFeatures.instance().yardiIntegration()) {
                headerMoveIn.setVisible(getValue().version().depositMoveIn().enabled().getValue(false));
                get(proto().version().depositMoveIn()).setVisible(getValue().version().depositMoveIn().enabled().getValue(false));

                headerSecurity.setVisible(getValue().version().depositSecurity().enabled().getValue(false));
                get(proto().version().depositSecurity()).setVisible(getValue().version().depositSecurity().enabled().getValue(false));

                headerDeposits.setVisible(headerLMR.isVisible() || headerMoveIn.isVisible() || headerSecurity.isVisible());
            } else {
                headerDeposits.setVisible(headerLMR.isVisible());
            }
        }
    }

    @Override
    public void addValidations() {
        super.addValidations();

        // revalidate items folder (see FeatureItemFolder.addValidations()) on mandatory changes:  
        get(proto().version().mandatory()).addValueChangeHandler(new RevalidationTrigger<Boolean>(get(proto().version().items())));
    }
}