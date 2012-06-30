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
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.lease.application;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.activity.crud.lease.common.LeaseEditorActivityBase;
import com.propertyvista.crm.client.ui.crud.lease.application.LeaseApplicationEditorView;
import com.propertyvista.crm.client.ui.crud.viewfactories.LeaseViewFactory;
import com.propertyvista.crm.rpc.services.lease.LeaseApplicationEditorCrudService;
import com.propertyvista.dto.LeaseApplicationDTO;

public class LeaseApplicationEditorActivity extends LeaseEditorActivityBase<LeaseApplicationDTO> implements LeaseApplicationEditorView.Presenter {

    public LeaseApplicationEditorActivity(CrudAppPlace place) {
        super(place, LeaseViewFactory.instance(LeaseApplicationEditorView.class), GWT
                .<LeaseApplicationEditorCrudService> create(LeaseApplicationEditorCrudService.class), LeaseApplicationDTO.class);
    }
}
