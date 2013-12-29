/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 21, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.operations.rpc.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.rpc.AbstractVersionedCrudService;

import com.propertyvista.operations.domain.legal.VistaTerms;
import com.propertyvista.operations.domain.legal.VistaTerms.Target;

public interface VistaTermsCrudService extends AbstractVersionedCrudService<VistaTerms> {

    @Transient
    public interface VistaTermsInitializationData extends InitializationData {

        IPrimitive<Target> target();
    }

    public void retrieveTerms(AsyncCallback<Key> callback, VistaTerms.Target target);
}
