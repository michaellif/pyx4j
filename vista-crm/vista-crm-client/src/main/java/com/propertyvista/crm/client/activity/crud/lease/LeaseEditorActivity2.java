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
package com.propertyvista.crm.client.activity.crud.lease;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.activity.crud.lease.common.LeaseEditorActivityBase2;
import com.propertyvista.crm.client.ui.crud.lease.LeaseEditorView2;
import com.propertyvista.crm.client.ui.crud.viewfactories.LeaseViewFactory;
import com.propertyvista.crm.rpc.services.lease.LeaseEditorCrudService2;
import com.propertyvista.dto.LeaseDTO2;

public class LeaseEditorActivity2 extends LeaseEditorActivityBase2<LeaseDTO2> implements LeaseEditorView2.Presenter {

    public LeaseEditorActivity2(CrudAppPlace place) {
        super(place, LeaseViewFactory.instance(LeaseEditorView2.class), GWT.<LeaseEditorCrudService2> create(LeaseEditorCrudService2.class), LeaseDTO2.class);
    }
}
