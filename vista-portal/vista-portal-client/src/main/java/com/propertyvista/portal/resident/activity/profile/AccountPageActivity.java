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
package com.propertyvista.portal.resident.activity.profile;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.resident.activity.AbstractEditorActivity;
import com.propertyvista.portal.resident.ui.profile.AccountPageView;
import com.propertyvista.portal.resident.ui.profile.AccountPageView.AccountPagePresenter;
import com.propertyvista.portal.rpc.portal.web.dto.ResidentAccountDTO;
import com.propertyvista.portal.rpc.portal.web.services.profile.ResidentAccountCrudService;

public class AccountPageActivity extends AbstractEditorActivity<ResidentAccountDTO> implements AccountPagePresenter {

    public AccountPageActivity(AppPlace place) {
        super(AccountPageView.class, GWT.<ResidentAccountCrudService> create(ResidentAccountCrudService.class), place);
    }

}
