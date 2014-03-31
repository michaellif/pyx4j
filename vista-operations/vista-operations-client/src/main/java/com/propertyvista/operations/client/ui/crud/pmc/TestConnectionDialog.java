/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 31, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.pmc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.operations.rpc.dto.ConnectionTestResultDTO;
import com.propertyvista.operations.rpc.services.PmcCrudService;

public class TestConnectionDialog extends OkCancelDialog {

    private static final I18n i18n = I18n.get(TestConnectionDialog.class);

    private HTML html;

    public TestConnectionDialog(final PmcYardiCredential service) {
        super(i18n.tr("Testing connection"));
        this.setBody(html = new HTML("connecting  ..."));
        getOkButton().setEnabled(false);

        GWT.<PmcCrudService> create(PmcCrudService.class).testYardiConnection(new DefaultAsyncCallback<ConnectionTestResultDTO>() {

            @Override
            public void onSuccess(ConnectionTestResultDTO result) {
                html.setHTML(result.htmlMessage);
                getOkButton().setEnabled(true);

                if (result.successful) {
                    setCaption(i18n.tr("Connection tested successfully"));
                } else {
                    setCaption(i18n.tr("There are error in connection test"));
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                TestConnectionDialog.this.hide(false);
                super.onFailure(caught);
            }
        }, service);
    }

    @Override
    public boolean onClickOk() {
        return true;
    }

}
