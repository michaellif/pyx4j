/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-08
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.admin;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.propertyvista.crm.rpc.dto.admin.CreditCheckStatusDTO;
import com.propertyvista.crm.rpc.services.admin.CreditCheckStatusService;

public class CreditCheckStatusServiceImpl implements CreditCheckStatusService {

    @Override
    public void obtainStatus(AsyncCallback<CreditCheckStatusDTO> callback) {
        if (true) {
            callback.onSuccess(null);
        }
    }

}
