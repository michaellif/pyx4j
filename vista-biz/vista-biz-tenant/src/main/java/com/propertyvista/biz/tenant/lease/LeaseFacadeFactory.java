/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 16, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.biz.tenant.lease;

import com.pyx4j.config.server.FacadeFactory;

import com.propertyvista.biz.tenant.lease.internal.LeaseFacadeInternalImpl;
import com.propertyvista.biz.tenant.lease.yardi.LeaseFacadeYardiImpl;
import com.propertyvista.shared.config.VistaFeatures;

public class LeaseFacadeFactory implements FacadeFactory<LeaseFacade> {

    @Override
    public LeaseFacade getFacade() {
        if (VistaFeatures.instance().yardiIntegration()) {
            return new LeaseFacadeYardiImpl();
        } else {
            return new LeaseFacadeInternalImpl();
        }
    }
}
