/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 12, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi;

import java.util.List;

import javax.jws.WebService;

import com.propertyvista.oapi.model.TransactionRS;

@WebService(endpointInterface = "com.propertyvista.oapi.ReceivableService")
public class ReceivableServiceImpl implements ReceivableService {

    @Override
    public void postTransactions(List<TransactionRS> transactions) {

    }

}
