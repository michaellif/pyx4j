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
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.crud.IFormView;
import com.pyx4j.site.client.ui.crud.misc.CEntityCrudHyperlink;

import com.propertyvista.admin.client.ui.crud.AdminEntityForm;
import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.admin.rpc.OnboardingMerchantAccountDTO;
import com.propertyvista.admin.rpc.PmcDTO;

public class OnboardingMerchantAccountForm extends AdminEntityForm<OnboardingMerchantAccountDTO> {

    private static final I18n i18n = I18n.get(OnboardingMerchantAccountForm.class);

    public OnboardingMerchantAccountForm(IFormView<OnboardingMerchantAccountDTO> view) {
        super(OnboardingMerchantAccountDTO.class, view);

        FormFlexPanel content = new FormFlexPanel(i18n.tr("General"));
        int row = -1;
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().pmc(), new CEntityCrudHyperlink<Pmc>(AppPlaceEntityMapper.resolvePlace(PmcDTO.class))),
                10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().onboardingBankAccountId())).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().status()), 25).readOnlyMode(true).build());

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().merchantTerminalId()), 25).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().bankId()), 5).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().branchTransitNumber()), 5).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().accountNumber()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().chargeDescription()), 30).build());

        selectTab(addTab(content));
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        get(proto().pmc()).setVisible(!getValue().pmc().isEmpty());
        get(proto().status()).setVisible(!getValue().id().isNull());
    }

}
