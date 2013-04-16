/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-16
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.settings.tenantsecurity;

import com.google.gwt.user.client.Command;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.AbstractPane;
import com.pyx4j.widgets.client.Button;

public class TenantSecurityViewImpl extends AbstractPane implements TenantSecurityView {

    private static final I18n i18n = I18n.get(TenantSecurityViewImpl.class);

    private TenantSecurityView.Presenter presenter;

    public TenantSecurityViewImpl() {
        Button button = new Button(i18n.tr("Generate Security Code List for Tenants that have not Registered"), new Command() {
            @Override
            public void execute() {
                presenter.generatePortalSecurityCodes();
            }
        });
        addHeaderToolbarItem(button);
    }

    @Override
    public void setPresenter(TenantSecurityView.Presenter presenter) {
        this.presenter = presenter;
    }

}
