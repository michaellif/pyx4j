/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 22, 2014
 * @author stanp
 */
package com.propertyvista.crm.client.activity.crud.lease.eviction;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.lease.eviction.EvictionCaseViewerView;
import com.propertyvista.crm.rpc.services.legal.eviction.EvictionCaseCrudService;
import com.propertyvista.dto.EvictionCaseDTO;

public class EvictionCaseViewerActivity extends CrmViewerActivity<EvictionCaseDTO> {

    public EvictionCaseViewerActivity(CrudAppPlace place) {
        super(EvictionCaseDTO.class, place, CrmSite.getViewFactory().getView(EvictionCaseViewerView.class), GWT
                .<EvictionCaseCrudService> create(EvictionCaseCrudService.class));
    }

}
