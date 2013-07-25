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
package com.propertyvista.operations.client.activity.security;

import java.util.Arrays;
import java.util.EnumSet;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.Key;
import com.pyx4j.forms.client.validators.password.DefaultPasswordStrengthRule;
import com.pyx4j.forms.client.validators.password.PasswordStrengthRule.PasswordStrengthVerdict;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AbstractPasswordChangeService;
import com.pyx4j.security.rpc.PasswordChangeRequest;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.security.PasswordChangeView;
import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.rpc.OperationsSiteMap;
import com.propertyvista.operations.rpc.services.AdminPasswordChangeManagedService;
import com.propertyvista.operations.rpc.services.AdminPasswordChangeUserService;

public class PasswordChangeActivity extends AbstractActivity implements PasswordChangeView.Presenter {

    private final static I18n i18n = I18n.get(PasswordChangeActivity.class);

    private final PasswordChangeView view;

    private PrincipalClass principalClass;

    private Key userPk;

    private final String userName;

    public PasswordChangeActivity(Place place) {
        view = OperationsSite.getViewFactory().instantiate(PasswordChangeView.class);
        view.setPresenter(this);

        try {
            assert place instanceof OperationsSiteMap.PasswordChange;
            String userPkStr = ((AppPlace) place).getFirstArg(PRINCIPAL_PK_ARG);
            userPk = new Key(userPkStr);
            userPk.asLong();

            principalClass = PrincipalClass.valueOf(((AppPlace) place).getFirstArg(PRINCIPAL_CLASS)); // warning! may throw IllegalArgumentException
            if (principalClass == null) {
                throw new Error("principal class was not specified");
            }

        } catch (Throwable ex) {
            History.back();
            throw new Error("wrong principal information", ex);
        }
        userName = isSelfAdmin() & principalClass == PrincipalClass.ADMIN ? null : ((AppPlace) place).getFirstArg(PRINCIPAL_NAME_ARG);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setAskForCurrentPassword(isSelfAdmin());
        view.setAskForRequireChangePasswordOnNextSignIn(!isSelfAdmin(), !isSelfAdmin() ? true : null, PasswordStrengthVerdict.Weak);
        view.setEnforcedPasswordStrengths(!isSelfAdmin() ? null : EnumSet.of(PasswordStrengthVerdict.Fair, PasswordStrengthVerdict.Good,
                PasswordStrengthVerdict.Strong));
        view.setMaskPassword(isSelfAdmin());
        DefaultPasswordStrengthRule strengthRule = new DefaultPasswordStrengthRule();
        if (isSelfAdmin()) {
            strengthRule.setDictionary(Arrays.asList(ClientContext.getUserVisit().getName(), ClientContext.getUserVisit().getEmail()));
        } else {
            strengthRule.setDictionary(Arrays.asList(userName));
        }
        view.setPasswordStrengthRule(strengthRule);
        view.initialize(userPk, userName);
        panel.setWidget(view);
    }

    @Override
    public void changePassword(PasswordChangeRequest request) {
        AbstractPasswordChangeService service = null;
        switch (principalClass) {
        case ADMIN:
            if (isSelfAdmin()) {
                service = GWT.<AbstractPasswordChangeService> create(AdminPasswordChangeUserService.class);
            } else {
                service = GWT.<AbstractPasswordChangeService> create(AdminPasswordChangeManagedService.class);
            }
            break;
        default:
            throw new Error("don't know which service to user for principal " + principalClass);
        }
        service.changePassword(new DefaultAsyncCallback<VoidSerializable>() {

            @Override
            public void onSuccess(VoidSerializable result) {
                MessageDialog.info(i18n.tr("Password was changed successfully"));
                History.back();
            }

            @Override
            public void onFailure(Throwable caught) {
                MessageDialog.error(i18n.tr("Failed to change the password"), caught);
            }

        }, request);
    }

    @Override
    public void cancel() {
        History.back();
    }

    private boolean isSelfAdmin() {
        return (principalClass == PrincipalClass.ADMIN) & EqualsHelper.equals(userPk, ClientContext.getUserVisit().getPrincipalPrimaryKey());
    }

}
