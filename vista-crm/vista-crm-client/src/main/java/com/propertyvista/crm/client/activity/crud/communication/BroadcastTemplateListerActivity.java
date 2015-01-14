/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 14, 2015
 * @author michaellif
 */
package com.propertyvista.crm.client.activity.crud.communication;

import com.pyx4j.site.client.backoffice.activity.prime.AbstractPrimeListerActivity;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.crud.communication.BroadcastTemplateListerView;
import com.propertyvista.domain.communication.BroadcastTemplate;

public class BroadcastTemplateListerActivity extends AbstractPrimeListerActivity<BroadcastTemplate> {

    public BroadcastTemplateListerActivity(AppPlace place) {
        super(BroadcastTemplate.class, place, CrmSite.getViewFactory().getView(BroadcastTemplateListerView.class));
    }

}
