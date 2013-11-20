/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 *
 */
package com.propertyvista.ils.gottarent.rs;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

/**
 * Currently j-rs API. Should be replaced either by scheduled task or by something else
 * 
 * @author smolka
 * 
 */
public class GottarentRsApplication extends Application {

    @SuppressWarnings("unchecked")
    @Override
    public Set<Class<?>> getClasses() {
        return new HashSet<Class<?>>(Arrays.asList(GottarentExceptionMapper.class, GottarentApiRsService.class));
    }
}