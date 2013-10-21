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
package com.propertyvista.crm.client.activity.crud.administration.financial.arcode;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.client.activity.AbstractListerActivity;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.crud.administration.financial.arcode.ARCodeListerView;
import com.propertyvista.crm.rpc.services.admin.ARCodeCrudService;
import com.propertyvista.domain.financial.ARCode;

public class ARCodeListerActivity extends AbstractListerActivity<ARCode> implements ARCodeListerView.Presenter {

    public ARCodeListerActivity(AppPlace place) {
        super(place, CrmSite.getViewFactory().instantiate(ARCodeListerView.class), GWT.<ARCodeCrudService> create(ARCodeCrudService.class), ARCode.class);
    }

}
