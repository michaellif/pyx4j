/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 12, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.settings.role;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.rpc.AbstractCrudService;

import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.settings.role.CrmRoleViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.SettingsViewFactory;
import com.propertyvista.crm.rpc.services.admin.CrmRoleCrudService;
import com.propertyvista.domain.security.CrmRole;

public class CrmRoleViewerActivity extends CrmViewerActivity<CrmRole> {

    public CrmRoleViewerActivity(Place place) {
        super(place, SettingsViewFactory.instance(CrmRoleViewerView.class), GWT.<AbstractCrudService<CrmRole>> create(CrmRoleCrudService.class));
    }

}
