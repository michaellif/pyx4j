/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 15, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.billing;

import com.pyx4j.config.server.FacadeFactory;
import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.financial.ar.ARFacade;

public class BillingFacadeFactory implements FacadeFactory<BillingFacade> {

    @Override
    public BillingFacade getFacade() {
        return ServerSideFactory.create(ARFacade.class).getBillingFacade();
    }

}
