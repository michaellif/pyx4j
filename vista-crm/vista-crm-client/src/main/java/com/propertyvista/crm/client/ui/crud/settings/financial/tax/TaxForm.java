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
package com.propertyvista.crm.client.ui.crud.settings.financial.tax;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.financial.tax.Tax;

public class TaxForm extends CrmEntityForm<Tax> {

    private static final I18n i18n = I18n.get(TaxForm.class);

    public TaxForm(IForm<Tax> view) {
        super(Tax.class, view);

        FormFlexPanel content = new FormFlexPanel(i18n.tr("General"));

        int row = -1;
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().name()), 25).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().authority()), 25).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().rate()), 7).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().compound()), 5).build());

        selectTab(addTab(content));

    }
}