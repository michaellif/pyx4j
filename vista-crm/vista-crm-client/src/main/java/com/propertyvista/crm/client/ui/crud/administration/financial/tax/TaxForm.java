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
package com.propertyvista.crm.client.ui.crud.administration.financial.tax;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.financial.tax.Tax;

public class TaxForm extends CrmEntityForm<Tax> {

    public TaxForm(IForm<Tax> view) {
        super(Tax.class, view);

        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();

        int row = -1;
        content.setWidget(++row, 0, inject(proto().name(), new FormDecoratorBuilder(25).build()));
        content.setWidget(++row, 0, inject(proto().authority(), new FormDecoratorBuilder(25).build()));
        content.setWidget(++row, 0, inject(proto().rate(), new FormDecoratorBuilder(7).build()));
        content.setWidget(++row, 0, inject(proto().compound(), new FormDecoratorBuilder(5).build()));

        setTabBarVisible(false);
        selectTab(addTab(content));
    }
}