/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-28
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.oapi;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.propertyvista.oapi.rs.RSLeaseService;
import com.propertyvista.oapi.rs.RSPropertyService;
import com.propertyvista.oapi.rs.RSReceivableService;

/**
 * "/interfaces/oapi/rs/*"
 */
public class OpenApiRsApplication extends Application {

    @SuppressWarnings("unchecked")
    @Override
    public Set<Class<?>> getClasses() {
        return new HashSet<Class<?>>(Arrays.asList(RSPropertyService.class, RSLeaseService.class, RSReceivableService.class));
    }
}
