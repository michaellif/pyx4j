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
package com.propertyvista.crm.client.ui.crud.settings.financial.dictionary;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.financial.ARCode;

public class ProductDictionaryViewForm extends CrmEntityForm<ARCode> {

    private static final I18n i18n = I18n.get(ProductDictionaryViewForm.class);

    public ProductDictionaryViewForm(IForm<ARCode> view) {
        super(ARCode.class, view);

        FormFlexPanel content = new FormFlexPanel(i18n.tr("General"));

        int row = -1;
        content.setH3(++row, 0, 1, i18n.tr("Product Codes"));
        content.setWidget(++row, 0, ((ProductDictionaryView) getParentView()).getProductCodeListerView().asWidget());

        content.setH3(++row, 0, 1, i18n.tr("Utilities"));
        content.setWidget(++row, 0, ((ProductDictionaryView) getParentView()).getUtilityListerView().asWidget());

        selectTab(addTab(content));
    }
}