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
package com.propertyvista.common.client.ui.components.security;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.form.ViewerViewImplBase;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.portal.rpc.shared.dto.AccountRecoveryOptionsDTO;

public class AccountRecoveryOptionsViewerViewImpl extends ViewerViewImplBase<AccountRecoveryOptionsDTO> implements AccountRecoveryOptionsViewerView {

    private static final I18n i18n = I18n.get(AccountRecoveryOptionsViewerViewImpl.class);

    private final Button btnEdit;

    public AccountRecoveryOptionsViewerViewImpl() {
        setForm(new AccountRecoveryOptionsForm(true));
        setCaption(i18n.tr("Account Recovery Options"));
        btnEdit = new Button(i18n.tr("Update"), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                getPresenter().edit();
            }
        });
        addHeaderToolbarTwoItem(btnEdit);
    }

}
