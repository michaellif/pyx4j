/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-29
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.rpc.services.building.catalog;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.ProductCatalog;
import com.propertyvista.domain.financial.offering.Service;

public interface ServiceCrudService extends AbstractCrudService<Service> {

    @Transient
    public interface ServiceInitializationdata extends InitializationData {

        ProductCatalog parent();

        IPrimitive<ARCode.Type> type();
    }

    public void retrieveCatalog(AsyncCallback<ProductCatalog> callback, Key entityId);
}
