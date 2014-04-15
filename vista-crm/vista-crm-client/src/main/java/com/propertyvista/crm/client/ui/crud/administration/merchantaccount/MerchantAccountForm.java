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
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.common.client.ui.validators.EcheckAccountNumberStringValidator;
import com.propertyvista.common.client.ui.validators.EcheckBankIdValidator;
import com.propertyvista.common.client.ui.validators.EcheckBranchTransitValidator;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.MerchantAccount.MerchantAccountActivationStatus;
import com.propertyvista.domain.security.VistaCrmBehavior;

public class MerchantAccountForm extends CrmEntityForm<MerchantAccount> {

    private static final I18n i18n = I18n.get(MerchantAccountForm.class);

    public MerchantAccountForm(IForm<MerchantAccount> view) {
        super(MerchantAccount.class, view);

        TwoColumnFlexFormPanel general = new TwoColumnFlexFormPanel(i18n.tr("General"));

        int row = -1;
        general.setWidget(++row, 0, injectAndDecorate(proto().paymentsStatus(), new CEnumLabel(), 25));
        general.setWidget(++row, 0, injectAndDecorate(proto().bankId(), new CLabel<String>(), 5));
        general.setWidget(++row, 0, injectAndDecorate(proto().branchTransitNumber(), new CLabel<String>(), 5));
        general.setWidget(++row, 0, injectAndDecorate(proto().accountNumber(), new CLabel<String>(), 15));
        general.setWidget(++row, 0, injectAndDecorate(proto().accountName()));
        general.setWidget(++row, 0, injectAndDecorate(proto().chargeDescription(), 40));

        selectTab(addTab(general));

        Tab buildingsTab = addTab(isEditable() ? new HTML() : ((MerchantAccountViewerView) getParentView()).getBuildingListerView().asWidget(),
                i18n.tr("Buildings"));
        setTabEnabled(buildingsTab, !isEditable());
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        boolean editable = (getValue().status().getValue(MerchantAccountActivationStatus.PendindAppoval) == MerchantAccountActivationStatus.PendindAppoval)
                && SecurityController.checkAnyBehavior(VistaCrmBehavior.PropertyVistaAccountOwner, VistaCrmBehavior.PropertyVistaSupport);

        get(proto().bankId()).setEditable(editable);
        get(proto().branchTransitNumber()).setEditable(editable);
        get(proto().accountNumber()).setEditable(editable);
    }

    @Override
    public void addValidations() {
        get(proto().accountNumber()).addComponentValidator(new EcheckAccountNumberStringValidator());
        get(proto().branchTransitNumber()).addComponentValidator(new EcheckBranchTransitValidator());
        get(proto().bankId()).addComponentValidator(new EcheckBankIdValidator());
    }
}
