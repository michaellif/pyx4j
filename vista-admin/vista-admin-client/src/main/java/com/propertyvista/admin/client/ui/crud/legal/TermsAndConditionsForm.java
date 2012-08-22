/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 17, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.admin.client.ui.crud.legal;

import com.pyx4j.entity.client.ui.CEntityComboBox;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.admin.client.ui.crud.AdminEntityForm;
import com.propertyvista.admin.domain.legal.LegalLocale;
import com.propertyvista.admin.domain.legal.TermsAndConditions;

public class TermsAndConditionsForm extends AdminEntityForm<TermsAndConditions> {
    private final static I18n i18n = I18n.get(TermsAndConditionsForm.class);

    public TermsAndConditionsForm(boolean viewMode) {
        super(TermsAndConditions.class, viewMode);
    }

    @Override
    protected void createTabs() {
        FormFlexPanel content = new FormFlexPanel(i18n.tr("General"));

        int row = -1;
        CEntityComboBox<LegalLocale> locale = new CEntityComboBox<LegalLocale>(LegalLocale.class);
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().document().locale(), locale), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().document().content()), 60).build());

        selectTab(addTab(content));
    }

}
