/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-10-02
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.policies.n4;

import com.pyx4j.site.client.backoffice.activity.prime.AbstractPrimeListerActivity;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.crud.policies.n4.N4PolicyListerView;
import com.propertyvista.domain.policy.dto.N4PolicyDTO;

public class N4PolicyListerActivity extends AbstractPrimeListerActivity<N4PolicyDTO> {

    public N4PolicyListerActivity(AppPlace place) {
        super(N4PolicyDTO.class, place, CrmSite.getViewFactory().getView(N4PolicyListerView.class));
    }

}
