/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 */
package com.propertyvista.crm.client.activity.crud.financial.paps;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.financial.paps.AutoPayHistoryViewerView;
import com.propertyvista.crm.rpc.dto.financial.AutoPayHistoryDTO;
import com.propertyvista.crm.rpc.services.financial.AutoPayHistoryCrudService;

public class AutoPayHistoryViewerActivity extends CrmViewerActivity<AutoPayHistoryDTO> implements AutoPayHistoryViewerView.Presenter {

    public AutoPayHistoryViewerActivity(CrudAppPlace place) {
        super(AutoPayHistoryDTO.class, place, CrmSite.getViewFactory().getView(AutoPayHistoryViewerView.class), GWT
                .<AutoPayHistoryCrudService> create(AutoPayHistoryCrudService.class));
    }
}