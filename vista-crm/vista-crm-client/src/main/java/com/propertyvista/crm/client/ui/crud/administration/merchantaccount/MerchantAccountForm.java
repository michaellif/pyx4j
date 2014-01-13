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
package com.propertyvista.crm.client.ui.crud.administration.merchantaccount;

import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.financial.MerchantAccount;

public class MerchantAccountForm extends CrmEntityForm<MerchantAccount> {

    private static final I18n i18n = I18n.get(MerchantAccountForm.class);

    public MerchantAccountForm(IForm<MerchantAccount> view) {
        super(MerchantAccount.class, view);

        TwoColumnFlexFormPanel general = new TwoColumnFlexFormPanel(i18n.tr("General"));

        int row = -1;
        general.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().paymentsStatus(), new CEnumLabel()), 25).build());
        general.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().bankId(), new CLabel<String>()), 5).build());
        general.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().branchTransitNumber(), new CLabel<String>()), 5).build());
        general.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().accountNumber(), new CLabel<String>()), 15).build());
        general.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().accountName())).build());
        general.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().chargeDescription()), 40).build());

        selectTab(addTab(general));

        Tab buildingsTab = addTab(isEditable() ? new HTML() : ((MerchantAccountViewerView) getParentView()).getBuildingListerView().asWidget(),
                i18n.tr("Buildings"));
        setTabEnabled(buildingsTab, !isEditable());
    }
}
