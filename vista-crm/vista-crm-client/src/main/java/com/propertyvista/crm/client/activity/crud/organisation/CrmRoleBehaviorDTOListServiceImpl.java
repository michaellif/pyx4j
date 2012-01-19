/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 19, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.organisation;

import java.util.EnumSet;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;

import com.propertyvista.crm.rpc.VistaCrmBehaviorDTOCoverter;
import com.propertyvista.crm.rpc.services.CrmRoleBehaviorDTOListService;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.domain.security.VistaCrmBehaviorDTO;

public class CrmRoleBehaviorDTOListServiceImpl implements CrmRoleBehaviorDTOListService {

    @Override
    public void list(AsyncCallback<EntitySearchResult<VistaCrmBehaviorDTO>> callback, EntityListCriteria<VistaCrmBehaviorDTO> criteria) {
        EntitySearchResult<VistaCrmBehaviorDTO> r = new EntitySearchResult<VistaCrmBehaviorDTO>();
        r.setData(VistaCrmBehaviorDTOCoverter.toDTO(EnumSet.allOf(VistaCrmBehavior.class)));

        //TODO vlads Add in memory filer for criteria

        callback.onSuccess(r);
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
    }

}
