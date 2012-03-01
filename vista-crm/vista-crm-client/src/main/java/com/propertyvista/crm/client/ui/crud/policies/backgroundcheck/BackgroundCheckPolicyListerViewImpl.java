/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 30, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.backgroundcheck;

import com.propertyvista.crm.client.ui.crud.CrmListerViewImplBase;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyListerBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.policy.dto.BackgroundCheckPolicyDTO;

public class BackgroundCheckPolicyListerViewImpl extends CrmListerViewImplBase<BackgroundCheckPolicyDTO> implements BackgroundCheckPolicyListerView {

    public BackgroundCheckPolicyListerViewImpl() {
        super(CrmSiteMap.Settings.Policies.BackgroundCheck.class);
        setLister(new BackgroundCheckPolicyLister());
    }

    private static class BackgroundCheckPolicyLister extends PolicyListerBase<BackgroundCheckPolicyDTO> {

        public BackgroundCheckPolicyLister() {
            super(BackgroundCheckPolicyDTO.class, CrmSiteMap.Settings.Policies.BackgroundCheck.class);
        }
    }

}
