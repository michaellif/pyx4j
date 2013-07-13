/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 22, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.onboardingusers;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.site.client.ui.prime.misc.CEntityCrudHyperlink;

import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.rpc.OnboardingUserDTO;
import com.propertyvista.operations.rpc.PmcDTO;
import com.propertyvista.domain.pmc.Pmc;

public class OnboardingUserForm extends OperationsEntityForm<OnboardingUserDTO> {

    private final static I18n i18n = I18n.get(OnboardingUserForm.class);

    public OnboardingUserForm(IForm<OnboardingUserDTO> view) {
        super(OnboardingUserDTO.class, view);
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(i18n.tr("General"));
        int row = -1;
        content.setH1(++row, 0, 1, i18n.tr("General"));
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().firstName())).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().lastName())).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().email())).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().role())).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().onboardingAccountId())).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().pmc(), new CEntityCrudHyperlink<Pmc>(AppPlaceEntityMapper.resolvePlace(PmcDTO.class))),
                10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().pmcStatus())).build());

        content.setH1(++row, 0, 1, i18n.tr("Security"));
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().password())).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().passwordConfirm())).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().enabled())).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().requiredPasswordChangeOnNextLogIn())).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().credentialUpdated())).build());

        selectTab(addTab(content));
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().password()).setVisible(isNewUser());
        get(proto().passwordConfirm()).setVisible(isNewUser());
        get(proto().onboardingAccountId()).setEditable(getValue().pmcStatus().isNull());
    }

    private boolean isNewUser() {
        return getValue().id().isNull();
    }

}
