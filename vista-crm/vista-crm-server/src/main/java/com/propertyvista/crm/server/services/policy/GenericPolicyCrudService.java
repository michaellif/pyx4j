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

import net.sf.ehcache.store.Policy;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;

import com.propertyvista.domain.policy.dto.PolicyDTOBase;

public abstract class GenericPolicyCrudService<POLICY extends Policy, POLICY_DTO extends PolicyDTOBase> implements AbstractCrudService<POLICY_DTO> {

    @Override
    public void list(AsyncCallback<EntitySearchResult<POLICY_DTO>> callback, EntityListCriteria<POLICY_DTO> criteria) {
        // FIXME implement criteria conversion
//        EntityListCriteria<PolicyAtNode> policiesAtNode 
    }

    @Override
    public void create(AsyncCallback<POLICY_DTO> callback, POLICY_DTO editableEntity) {

    }

}
