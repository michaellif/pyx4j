/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 14, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.admin.client.ui.crud.pmc;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.admin.client.ui.crud.AdminEntityForm;
import com.propertyvista.admin.domain.pmc.OnboardingMerchantAccount;

public class OnboardingMerchantAccountForm extends AdminEntityForm<OnboardingMerchantAccount> {

    private static final I18n i18n = I18n.get(OnboardingMerchantAccountForm.class);

    public OnboardingMerchantAccountForm(boolean viewMode) {
        super(OnboardingMerchantAccount.class, viewMode);
    }

    public OnboardingMerchantAccountForm() {
        this(false);
    }

    @Override
    protected void createTabs() {
        FormFlexPanel content = new FormFlexPanel(i18n.tr("General"));
        int row = -1;
        content.setWidget(0, ++row, new DecoratorBuilder(inject(proto().onboardingAccountId())).build());
        content.setWidget(0, ++row, new DecoratorBuilder(inject(proto().onboardingBankAccountId())).build());

        selectTab(addTab(content));
    }

}
