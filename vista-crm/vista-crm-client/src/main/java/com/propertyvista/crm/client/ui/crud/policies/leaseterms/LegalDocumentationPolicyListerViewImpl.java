/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 22, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.leaseterms;

import com.propertyvista.crm.client.ui.crud.CrmListerViewImplBase;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyListerBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.policy.dto.LegalDocumentationPolicyDTO;

public class LegalDocumentationPolicyListerViewImpl extends CrmListerViewImplBase<LegalDocumentationPolicyDTO> implements LegalDocumentationPolicyListerView {

    public LegalDocumentationPolicyListerViewImpl() {
        super(CrmSiteMap.Administration.Policies.LegalDocumentation.class);
        setLister(new LegalDocumentationPolicyLister());
    }

    private static class LegalDocumentationPolicyLister extends PolicyListerBase<LegalDocumentationPolicyDTO> {

        public LegalDocumentationPolicyLister() {
            super(LegalDocumentationPolicyDTO.class);
        }
    }
}
