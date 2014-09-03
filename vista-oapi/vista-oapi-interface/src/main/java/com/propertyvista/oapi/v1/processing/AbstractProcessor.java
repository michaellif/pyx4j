/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 3, 2014
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.v1.processing;

import com.propertyvista.oapi.ServiceType;

public class AbstractProcessor {

    private static class ProcessorContext {

        public ProcessorContext(ServiceType serviceType) {
            this.serviceType = serviceType;
        }

        ServiceType serviceType;

    }

    private static final ThreadLocal<ProcessorContext> threadLocale = new ThreadLocal<ProcessorContext>();

    public static ServiceType getServiceType() {
        return threadLocale.get().serviceType;
    }

    public AbstractProcessor(ServiceType serviceType) {
        threadLocale.set(new ProcessorContext(serviceType));
    }

    public void destroy() {
        threadLocale.remove();
    }

}
