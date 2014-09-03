/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 17, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.oapi.v1.rs;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class OapiRsApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        return new HashSet<Class<?>>(Arrays.asList( //
                // helpers
                RSExceptionMapper.class, //
                RSConverterProvider.class, //
                // service
                RSPortationServiceImpl.class, //
                RSPropertyServiceImpl.class, //
                RSLeaseServiceImpl.class, //
                RSMarketingServiceImpl.class //
                ));
    }
}
