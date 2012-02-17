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
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.financial.offering.ProductItemType;

public class ProductDictionaryViewForm extends CrmEntityForm<ProductItemType> {

    private static final I18n i18n = I18n.get(ProductDictionaryViewForm.class);

    public ProductDictionaryViewForm() {
        this(false);
    }

    public ProductDictionaryViewForm(boolean viewMode) {
        super(ProductItemType.class, viewMode);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setH1(++row, 0, 1, i18n.tr("Service Item Types"));
        main.setWidget(++row, 0, ((ProductDictionaryView) getParentView()).getServiceListerView().asWidget());

        main.setH1(++row, 0, 1, i18n.tr("Feature Item Types"));
        main.setWidget(++row, 0, ((ProductDictionaryView) getParentView()).getFeatureListerView().asWidget());

        return new ScrollPanel(main);
    }
}