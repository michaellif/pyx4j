/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 9, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.settings.dictionary;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.activity.crud.CrmEditorActivity;
import com.propertyvista.crm.client.ui.crud.settings.financial.producttype.ServiceTypeEditorView;
import com.propertyvista.crm.client.ui.crud.viewfactories.SettingsViewFactory;
import com.propertyvista.crm.rpc.services.building.catalog.ServiceItemTypeCrudService;
import com.propertyvista.domain.financial.offering.ServiceItemType;

public class ServiceItemTypeEditorActivity extends CrmEditorActivity<ServiceItemType> {

    @SuppressWarnings("unchecked")
    public ServiceItemTypeEditorActivity(CrudAppPlace place) {
        super(place, SettingsViewFactory.instance(ServiceTypeEditorView.class), (AbstractCrudService<ServiceItemType>) GWT
                .create(ServiceItemTypeCrudService.class), ServiceItemType.class);
    }
}
