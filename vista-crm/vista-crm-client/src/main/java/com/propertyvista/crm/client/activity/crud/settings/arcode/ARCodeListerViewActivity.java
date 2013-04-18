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
package com.propertyvista.crm.client.activity.crud.settings.arcode;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.client.activity.AbstractListerActivity;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.ui.crud.settings.financial.arcode.ARCodeListerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.SettingsViewFactory;
import com.propertyvista.crm.rpc.services.admin.ARCodeCrudService;
import com.propertyvista.domain.financial.ARCode;

public class ARCodeListerViewActivity extends AbstractListerActivity<ARCode> implements ARCodeListerView.Presenter {

    public ARCodeListerViewActivity(AppPlace place) {
        super(place, SettingsViewFactory.instance(ARCodeListerView.class), GWT.<ARCodeCrudService> create(ARCodeCrudService.class), ARCode.class);
    }

}
