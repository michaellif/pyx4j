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

import com.google.gwt.core.client.GWT;

import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.common.client.ui.components.security.AbstractAccountRecoveryOptionsEditorActivity;
import com.propertyvista.common.client.ui.components.security.AccountRecoveryOptionsEditorView;
import com.propertyvista.crm.client.ui.viewfactories.SecurityViewFactory;
import com.propertyvista.crm.rpc.services.security.CrmAccountRecoveryOptionsUserService;
import com.propertyvista.domain.security.VistaBasicBehavior;

public class AccountRecoveryOptionsEditorActivity extends AbstractAccountRecoveryOptionsEditorActivity {

    public AccountRecoveryOptionsEditorActivity(CrudAppPlace place) {
        super(place, SecurityViewFactory.instance(AccountRecoveryOptionsEditorView.class), GWT
                .<CrmAccountRecoveryOptionsUserService> create(CrmAccountRecoveryOptionsUserService.class));
    }

    @Override
    protected boolean isPasswordChangeRequiresSecurityQuestion() {
        return SecurityController.checkBehavior(VistaBasicBehavior.CRMPasswordChangeRequiresSecurityQuestion);
    }

    @Override
    protected boolean isSetupAccountRecoveryOptionsRequired() {
        return SecurityController.checkBehavior(VistaBasicBehavior.CRMSetupAccountRecoveryOptionsRequired);
    }
}
