/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 18, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.portal.shared.activity.account;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.rpc.portal.shared.services.account.CustomerAccountCrudService;
import com.propertyvista.portal.rpc.shared.dto.CustomerAccountDTO;
import com.propertyvista.portal.shared.activity.AbstractEditorActivity;
import com.propertyvista.portal.shared.ui.account.AccountPageView;
import com.propertyvista.portal.shared.ui.account.AccountPageView.AccountPagePresenter;

public class AccountPageActivity extends AbstractEditorActivity<CustomerAccountDTO> implements AccountPagePresenter {

    public AccountPageActivity(AppPlace place) {
        super(AccountPageView.class, GWT.<CustomerAccountCrudService> create(CustomerAccountCrudService.class), place);
    }

}
