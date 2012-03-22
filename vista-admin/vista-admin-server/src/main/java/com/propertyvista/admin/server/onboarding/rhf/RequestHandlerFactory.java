/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 22, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.server.onboarding.rhf;

import java.util.HashMap;
import java.util.Map;

import com.propertyvista.onboarding.RequestIO;

public abstract class RequestHandlerFactory {

    private final Map<Class<? extends RequestIO>, Class<? extends RequestHandler<?>>> binding = new HashMap<Class<? extends RequestIO>, Class<? extends RequestHandler<?>>>();

    protected RequestHandlerFactory() {
        bind();
    }

    protected abstract void bind();

    protected void bind(Class<? extends RequestHandler<?>> requestHandlerClass) {
        RequestHandler<?> rh;
        try {
            rh = requestHandlerClass.newInstance();
        } catch (InstantiationException e) {
            throw new Error(e);
        } catch (IllegalAccessException e) {
            throw new Error(e);
        }
        rh.getRequestClass();
        binding.put(rh.getRequestClass(), requestHandlerClass);
    }

    public <T extends RequestIO> RequestHandler<T> createRequestHandler(T request) {
        Class<? extends RequestHandler<?>> requestHandlerClass = binding.get(request.getValueClass());
        if (requestHandlerClass == null) {
            return null;
        }
        try {
            @SuppressWarnings("unchecked")
            RequestHandler<T> rh = (RequestHandler<T>) requestHandlerClass.newInstance();
            return rh;
        } catch (InstantiationException e) {
            throw new Error(e);
        } catch (IllegalAccessException e) {
            throw new Error(e);
        }
    }
}
