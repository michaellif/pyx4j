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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.propertyvista.portal.domain.pt.Pets;

import com.pyx4j.rpc.shared.VoidSerializable;

public interface PetsServices extends AbstractWizardServices<VoidSerializable, Pets> {

    //======== Old way  ===================

    public interface Retrieve extends AbstractWizardServices.Retrieve<VoidSerializable, Pets> {
    };

    public interface Save extends AbstractWizardServices.Save<Pets> {
    };

    //======== New way, no need to declarations but here they are for clarity ===================

    @Override
    public void retrieve(VoidSerializable criteria, AsyncCallback<Pets> callback);

    @Override
    public void save(Pets editableEntity, AsyncCallback<Pets> callback);

}
