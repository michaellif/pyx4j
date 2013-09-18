/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 29, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.policies.common;

import com.pyx4j.site.client.activity.AbstractEditorActivity;
import com.pyx4j.site.client.ui.prime.form.IEditor;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.rpc.services.policies.policy.AbstractPolicyCrudService;
import com.propertyvista.domain.policy.framework.PolicyDTOBase;

public abstract class PolicyEditorActivityBase<POLICY_DTO extends PolicyDTOBase> extends AbstractEditorActivity<POLICY_DTO> {

    public PolicyEditorActivityBase(CrudAppPlace place, IEditor<POLICY_DTO> view, AbstractPolicyCrudService<POLICY_DTO> service, Class<POLICY_DTO> entityClass) {
        super(place, view, service, entityClass);
    }
}
