/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 27, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.customer;

import com.propertyvista.crm.rpc.services.customer.GuarantorPasswordChangeService;
import com.propertyvista.domain.security.CustomerUserCredential;
import com.propertyvista.server.common.security.VistaManagedPasswordChangeServiceImpl;

public class GuarantorPasswordChangeServiceImpl extends VistaManagedPasswordChangeServiceImpl<CustomerUserCredential> implements GuarantorPasswordChangeService {

    public GuarantorPasswordChangeServiceImpl() {
        super(CustomerUserCredential.class);
    }

}
