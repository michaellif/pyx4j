/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 11, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.settings.merchantaccount;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.form.IForm;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.financial.MerchantAccount;

public class MerchantAccountForm extends CrmEntityForm<MerchantAccount> {

    private static final I18n i18n = I18n.get(MerchantAccountForm.class);

    public MerchantAccountForm(IForm<MerchantAccount> view) {
        super(MerchantAccount.class, view);

        if (isEditable()) {
            setViewable(true);
        }

        FormFlexPanel content = new FormFlexPanel(i18n.tr("General"));

        int row = -1;
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().paymentsStatus()), 25).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().bankId()), 5).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().branchTransitNumber()), 5).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().accountNumber()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().chargeDescription()), 30).build());

        // tweak:
        if (isEditable()) {
            get(proto().chargeDescription()).inheritViewable(false);
            get(proto().chargeDescription()).setViewable(false);
        }

        selectTab(addTab(content));

    }
}
