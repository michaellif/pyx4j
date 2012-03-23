/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 24, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.security;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.Key;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AbstractPasswordChangeService;
import com.pyx4j.security.rpc.PasswordChangeRequest;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.security.PasswordChangeView;
import com.propertyvista.crm.client.ui.viewfactories.SecurityViewFactory;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.organization.ManagedCrmUserService;
import com.propertyvista.crm.rpc.services.security.CrmPasswordChangeUserService;
import com.propertyvista.crm.rpc.services.tenant.TenantPasswordChangeService;

public class PasswordChangeActivity extends AbstractActivity implements PasswordChangeView.Presenter {

    private final static I18n i18n = I18n.get(PasswordChangeActivity.class);

    private final PasswordChangeView view;

    private final Key userPk;

    private final PasswordChangeView.Presenter.PrincipalClass principalClass;

    public PasswordChangeActivity(Place place) {

        view = SecurityViewFactory.instance(PasswordChangeView.class);
        view.setPresenter(this);

        try {
            assert place instanceof CrmSiteMap.PasswordChange;
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
        String userName = isSelfAdmin() ? null : ((AppPlace) place).getFirstArg(PRINCIPAL_NAME_ARG);
        view.initialize(userPk, userName);

    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
    }

    @Override
    public void changePassword(PasswordChangeRequest request) {
        AbstractPasswordChangeService service = null;

        if (principalClass.equals(PrincipalClass.EMPLOYEE)) {
            if (isSelfAdmin()) {
                service = GWT.<CrmPasswordChangeUserService> create(CrmPasswordChangeUserService.class);
            } else {
                service = GWT.<ManagedCrmUserService> create(ManagedCrmUserService.class);
            }
        } else if (principalClass.equals(PrincipalClass.TENANT)) {
            service = GWT.<TenantPasswordChangeService> create(TenantPasswordChangeService.class);
        } else {
            throw new UnrecoverableClientError("Got unknown principal class or changing password for this principal has not yet been implemented");
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

    private boolean isSelfAdmin() {
        return EqualsHelper.equals(userPk, ClientContext.getUserVisit().getPrincipalPrimaryKey());
    }

}
