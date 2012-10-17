/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-17
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.settings.creditcheck;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.settings.creditchecks.PersonCreditCheckViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.SettingsViewFactory;
import com.propertyvista.crm.rpc.dto.PersonCreditCheckDTO;
import com.propertyvista.crm.rpc.services.admin.PersonCreditCheckCrudService;

public class PersonCreditCheckViewerActivity extends CrmViewerActivity<PersonCreditCheckDTO> {

    public PersonCreditCheckViewerActivity(CrudAppPlace place) {
        super(place, SettingsViewFactory.instance(PersonCreditCheckViewerView.class), GWT
                .<PersonCreditCheckCrudService> create(PersonCreditCheckCrudService.class));
    }

}
