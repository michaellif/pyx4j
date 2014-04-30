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
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFluidPanel.Location;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.site.client.ui.prime.misc.CEntityCrudHyperlink;

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

        selectTab(addTab(createGeneralTab(), i18n.tr("General")));
        addTab(createItemsTab(), i18n.tr("Items"));
    }

    public BasicCFormPanel createGeneralTab() {
        BasicCFormPanel formPanel = new BasicCFormPanel(this);

        formPanel.h1(i18n.tr("Information"));
        formPanel.append(Location.Left, proto().code(), new CEntityCrudHyperlink<ARCode>(AppPlaceEntityMapper.resolvePlace(ARCode.class))).decorate()
                .componentWidth(200);
        formPanel.append(Location.Left, proto().version().name()).decorate().componentWidth(200);
        formPanel.append(Location.Left, proto().version().description()).decorate().componentWidth(200);
        if (VistaTODO.VISTA_2256_Default_Product_Catalog_Show) {
            formPanel.append(Location.Left, injectAndDecorate(proto().defaultCatalogItem(), new CBooleanLabel(), 40));
        }

        formPanel.append(Location.Right, proto().expiredFrom()).decorate().componentWidth(100);
        formPanel.append(Location.Right, proto().version().price()).decorate().componentWidth(100);
        formPanel.append(Location.Right, proto().version().availableOnline()).decorate().componentWidth(40);
        formPanel.append(Location.Right, proto().version().mandatory()).decorate().componentWidth(40);
        formPanel.append(Location.Right, proto().version().recurring()).decorate().componentWidth(40);

        headerDeposits = formPanel.h1(i18n.tr("Deposits"));

        headerLMR = formPanel.h3(i18n.tr("Last Month Rent"));
        formPanel.append(Location.Full, proto().version().depositLMR(), new ProductDepositEditor());

        if (!VistaFeatures.instance().yardiIntegration()) {
            headerMoveIn = formPanel.h3(i18n.tr("Move In"));
            formPanel.append(Location.Full, proto().version().depositMoveIn(), new ProductDepositEditor());

            headerSecurity = formPanel.h3(i18n.tr("Security"));
            formPanel.append(Location.Full, proto().version().depositSecurity(), new ProductDepositEditor());
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

        return formPanel;
    }

    public BasicCFormPanel createItemsTab() {
        BasicCFormPanel formPanel = new BasicCFormPanel(this);

        formPanel.append(Location.Full, proto().version().items(), new FeatureItemFolder(this));

        return formPanel;
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