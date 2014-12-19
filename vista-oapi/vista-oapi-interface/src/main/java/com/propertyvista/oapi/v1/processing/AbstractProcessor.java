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
 */
package com.propertyvista.oapi.v1.processing;

import com.propertyvista.oapi.ServiceType;
import com.propertyvista.oapi.v1.service.OAPIService;

public class AbstractProcessor {

    private static class ProcessorContext {

        public ProcessorContext(Class<? extends OAPIService> serviceClass, ServiceType serviceType) {
            this.serviceClass = serviceClass;
            this.serviceType = serviceType;
        }

        Class<? extends OAPIService> serviceClass;

        ServiceType serviceType;

    }

    private static final ThreadLocal<ProcessorContext> threadLocale = new ThreadLocal<ProcessorContext>();

    public static Class<? extends OAPIService> getServiceClass() {
        return threadLocale.get() == null ? null : threadLocale.get().serviceClass;
    }

    public static ServiceType getServiceType() {
        return threadLocale.get() == null ? null : threadLocale.get().serviceType;
    }

    public AbstractProcessor(Class<? extends OAPIService> serviceClass, ServiceType serviceType) {
        threadLocale.set(new ProcessorContext(serviceClass, serviceType));
    }

    public void destroy() {
        threadLocale.remove();
    }

}
