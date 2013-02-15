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

import com.google.gwt.user.client.Command;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.domain.security.VistaOnboardingBehavior;
import com.propertyvista.operations.client.ui.crud.OperationsViewerViewImplBase;
import com.propertyvista.operations.rpc.OnboardingUserDTO;

public class OnboardingUserViewerViewImpl extends OperationsViewerViewImplBase<OnboardingUserDTO> implements OnboardingUserViewerView {

    private final static I18n i18n = I18n.get(OnboardingUserViewerViewImpl.class);

    private final Button passwordAction;

    private final Button createPmcAction;

    public OnboardingUserViewerViewImpl() {
        setForm(new OnboardingUserForm(this));

        passwordAction = new Button(i18n.tr("Change Password"), new Command() {
            @Override
            public void execute() {
                ((OnboardingUserViewerView.Presenter) getPresenter()).goToChangePassword(getForm().getValue().getPrimaryKey(), getForm().getValue().name()
                        .getStringView());
            }
        });
        addHeaderToolbarItem(passwordAction.asWidget());

        createPmcAction = new Button(i18n.tr("Create PMC"), new Command() {

            @Override
            public void execute() {
                ((OnboardingUserViewerView.Presenter) getPresenter()).createPmc(getForm().getValue());
            }

        });
        addHeaderToolbarItem(createPmcAction.asWidget());

    }

    @Override
    public void populate(OnboardingUserDTO value) {
        super.populate(value);
        passwordAction.setVisible(getPresenter().canEdit());
        createPmcAction.setVisible(value.pmc().isNull() & (value.role().getValue() == VistaOnboardingBehavior.ProspectiveClient));
    }

}
