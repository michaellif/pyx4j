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
package com.propertyvista.crm.client.activity.crud.lease.common;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.client.activity.crud.ViewerActivityBase;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.ui.crud.lease.common.term.LeaseTermViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.LeaseViewFactory;
import com.propertyvista.crm.rpc.services.lease.common.LeaseTermCrudService;
import com.propertyvista.dto.LeaseTermDTO;

public class LeaseTermViewerActivity extends ViewerActivityBase<LeaseTermDTO> implements LeaseTermViewerView.Presenter {

    public LeaseTermViewerActivity(CrudAppPlace place) {
        super(place, LeaseViewFactory.instance(LeaseTermViewerView.class), GWT.<LeaseTermCrudService> create(LeaseTermCrudService.class));
    }
}
