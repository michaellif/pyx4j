/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.customer.tenant;

import com.pyx4j.site.client.backoffice.activity.prime.AbstractPrimeListerActivity;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.crud.customer.tenant.TenantListerView;
import com.propertyvista.dto.TenantDTO;

public class TenantListerActivity extends AbstractPrimeListerActivity<TenantDTO> implements TenantListerView.Presenter {

    public TenantListerActivity(AppPlace place) {
        super(TenantDTO.class, place, CrmSite.getViewFactory().getView(TenantListerView.class));
    }

}
