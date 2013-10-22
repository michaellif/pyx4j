/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 20, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.account;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.MessageDialog.Type;
import com.pyx4j.widgets.client.dialog.OkOption;

import com.propertyvista.common.client.ui.components.security.AccountRecoveryOptionsDialog;
import com.propertyvista.crm.rpc.services.security.CrmAccountRecoveryOptionsUserService;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.portal.rpc.shared.dto.AccountRecoveryOptionsDTO;

public class MandatoryAccountRecoveryOptionsSetupActivity extends AbstractActivity {

    private static final I18n i18n = I18n.get(MandatoryAccountRecoveryOptionsSetupActivity.class);

    private final CrmAccountRecoveryOptionsUserService accountRecoveryOptionsService;

    public MandatoryAccountRecoveryOptionsSetupActivity() {
        accountRecoveryOptionsService = GWT.<CrmAccountRecoveryOptionsUserService> create(CrmAccountRecoveryOptionsUserService.class);

    }

    @Override
    public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
        panel.setWidget(new SimplePanel());
        MessageDialog.show(i18n.tr(""), i18n.tr("Because of the privileges associated with your account, you must setup password recovery options."),
                Type.Confirm, new OkOption() {
                    @Override
                    public boolean onClickOk() {

                        accountRecoveryOptionsService.obtainRecoveryOptions(new DefaultAsyncCallback<AccountRecoveryOptionsDTO>() {
                            @Override
                            public void onSuccess(AccountRecoveryOptionsDTO result) {
                                showRecoveryOptionsDialog(result);
                            }

                        }, EntityFactory.create(AuthenticationRequest.class));

                        return true;
                    }
                });

    }

    private void showRecoveryOptionsDialog(final AccountRecoveryOptionsDTO accountRecoveryOptions) {
        new AccountRecoveryOptionsDialog(//@formatter:off
                null,
                accountRecoveryOptions,
                SecurityController.checkBehavior(VistaBasicBehavior.CRMPasswordChangeRequiresSecurityQuestion),
                true,
                accountRecoveryOptionsService
        ) {                    
            @Override
            protected void onUpdateRecoveryOptionsSuccess(com.pyx4j.security.rpc.AuthenticationResponse result) {
                ClientContext.authenticated(result);
                AppSite.getPlaceController().goTo(AppPlace.NOWHERE);
            };
            
            @Override
            protected void onUpdateRecoveryOptionsFail(com.pyx4j.commons.UserRuntimeException caught) {
                showRecoveryOptionsDialog(accountRecoveryOptions);
            };            
            
        }.show();//@formatter:on
    }
}
