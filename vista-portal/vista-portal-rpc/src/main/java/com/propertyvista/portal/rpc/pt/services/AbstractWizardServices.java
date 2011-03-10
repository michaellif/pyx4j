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
package com.propertyvista.portal.rpc.pt.services;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.propertyvista.portal.domain.pt.IBoundToApplication;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.rpc.shared.Service;

// SubItemCriteria  is marker to retrieve one of the items in list e.g. only details for one of the TenantsInfo 

public interface AbstractWizardServices<SubItemCriteria extends Serializable, EditableEntity extends IEntity & IBoundToApplication> {

    //======== Old way: I know I can make it work in 2h  ===================
    // Cons: each command does not inherit generic of the main class

    public interface Retrieve<SubItemCriteria extends Serializable, E extends IEntity & IBoundToApplication> extends Service<SubItemCriteria, IEntity> {
    };

    public interface Save<E extends IEntity & IBoundToApplication> extends Service<E, E> {
    };

    //======== New way: I can probably make it work in 2d  ===================
    // Cons: each function on server should call AsyncCallback to return value; see example PetsServicesImpl
    // Pros: I can generate at compile time additional GWT-RPC service and this will improve code split and sequencialy initial app load time.

    public void retrieve(SubItemCriteria criteria, AsyncCallback<EditableEntity> callback);

    public void save(EditableEntity editableEntity, AsyncCallback<EditableEntity> callback);
}
