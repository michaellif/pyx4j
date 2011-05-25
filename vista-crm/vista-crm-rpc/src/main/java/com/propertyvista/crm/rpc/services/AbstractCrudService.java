/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 17, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.rpc.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntitySearchCriteria;
import com.pyx4j.rpc.shared.IService;

public interface AbstractCrudService<EditableEntity extends IEntity> extends IService {

    public void create(AsyncCallback<EditableEntity> callback, EditableEntity editableEntity);

    public void retrieve(AsyncCallback<EditableEntity> callback, String entityId);

    public void save(AsyncCallback<EditableEntity> callback, EditableEntity editableEntity);

    public void search(AsyncCallback<EntitySearchResult<EditableEntity>> callback, EntitySearchCriteria<EditableEntity> criteria);

}
