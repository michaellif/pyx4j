/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 10, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.rpc.ptapp.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.rpc.shared.IService;

import com.propertyvista.portal.domain.ptapp.IBoundToApplication;

public interface AbstractWizardService<EditableEntity extends IEntity & IBoundToApplication> extends IService {

    public void retrieve(AsyncCallback<EditableEntity> callback, Key tenantId);

    public void save(AsyncCallback<EditableEntity> callback, EditableEntity editableEntity);
}
