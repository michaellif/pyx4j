/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.tenant;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.site.client.ui.crud.IEditorView;

import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.dto.TenantDTO;

public interface TenantEditorView extends IEditorView<TenantDTO>, TenantView {

    interface Presenter extends IEditorView.Presenter, TenantView.Presenter {
    }

    void showSelectTypePopUp(AsyncCallback<Tenant.Type> callback);
}
