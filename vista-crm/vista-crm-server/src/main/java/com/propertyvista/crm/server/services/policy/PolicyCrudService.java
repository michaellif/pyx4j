/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 28, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.policy;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.rpc.shared.IService;

import com.propertyvista.domain.policy.Policy;
import com.propertyvista.domain.policy.dto.PolicyDTOBase;

public class PolicyCrudService implements IService {

    public void list(AsyncCallback<EntitySearchResult<PolicyDTOBase>> callback, EntityListCriteria<PolicyDTOBase> criteria, Policy policyProto) {
        // TODO Auto-generated method stub        
    }

    public void delete(AsyncCallback<Boolean> callback, Key entityId, Policy policyProto) {
        // TODO Auto-generated method stub

    }

    public void create(AsyncCallback<PolicyDTOBase> callback, PolicyDTOBase editableEntity, Policy policyProto) {

    }

    public void retrieve(AsyncCallback<PolicyDTOBase> callback, Key entityId, Policy policyProto) {

    }

    public void save(AsyncCallback<PolicyDTOBase> callback, PolicyDTOBase editableEntity, Policy policyProto) {

    }
}
