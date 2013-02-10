/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 27, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.admin.client.activity.crud.account;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.client.activity.EditorActivityBase;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.admin.client.ui.crud.adminusers.AdminUserEditorView;
import com.propertyvista.admin.client.viewfactories.crud.AdministrationVeiwFactory;
import com.propertyvista.admin.rpc.AdminUserDTO;
import com.propertyvista.admin.rpc.services.AdminUserService;

public class AccountEditorActivity extends EditorActivityBase<AdminUserDTO> {

    public AccountEditorActivity(CrudAppPlace place) {
        super(place, AdministrationVeiwFactory.instance(AdminUserEditorView.class), GWT.<AdminUserService> create(AdminUserService.class), AdminUserDTO.class);
    }

}
