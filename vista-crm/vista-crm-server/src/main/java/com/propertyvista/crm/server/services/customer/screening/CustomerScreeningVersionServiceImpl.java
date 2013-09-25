/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-03
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.customer.screening;

import com.propertyvista.crm.rpc.services.customer.screening.CustomerScreeningVersionService;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.tenant.CustomerScreening;
import com.propertyvista.server.versioning.AbstractVistaVersionDataListServiceImpl;

public class CustomerScreeningVersionServiceImpl extends AbstractVistaVersionDataListServiceImpl<CustomerScreening.CustomerScreeningV> implements
        CustomerScreeningVersionService {

    public CustomerScreeningVersionServiceImpl() {
        super(CustomerScreening.CustomerScreeningV.class, CrmUser.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

}
