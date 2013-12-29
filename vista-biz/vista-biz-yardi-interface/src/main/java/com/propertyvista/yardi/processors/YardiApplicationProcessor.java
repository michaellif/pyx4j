/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 19, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.yardi.processors;

import java.util.Arrays;

import com.yardi.entity.leaseapp30.LeaseApplication;
import com.yardi.entity.leaseapp30.Tenant;

public class YardiApplicationProcessor {

    public LeaseApplication createApplication(Tenant... tenants) {
        LeaseApplication leaseApp = new LeaseApplication();
        leaseApp.getTenant().addAll(Arrays.asList(tenants));
        return leaseApp;
    }
}
