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
package com.propertyvista.admin.client.activity.crud.adminusers;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.site.client.activity.crud.EditorActivityBase;

import com.propertyvista.admin.client.ui.crud.adminusers.AdminUserEditorView;
import com.propertyvista.admin.client.viewfactories.crud.AdministrationVeiwFactory;
import com.propertyvista.admin.rpc.AdminUserDTO;
import com.propertyvista.admin.rpc.services.AdminUserCrudService;

public class AdminUserEditorActivity extends EditorActivityBase<AdminUserDTO> {

    public AdminUserEditorActivity(Place place) {
        super(place, AdministrationVeiwFactory.instance(AdminUserEditorView.class), GWT.<AdminUserCrudService> create(AdminUserCrudService.class),
                AdminUserDTO.class);
    }

}
