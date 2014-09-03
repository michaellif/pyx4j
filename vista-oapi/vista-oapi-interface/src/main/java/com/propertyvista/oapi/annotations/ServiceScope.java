/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 29, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.oapi.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.propertyvista.oapi.DetailsLevel;
import com.propertyvista.oapi.v1.service.OAPIService;

@Retention(RetentionPolicy.RUNTIME)
/**
 * Limits the use of the target property to the given list of services
 */
public @interface ServiceScope {

    Class<? extends OAPIService>[] services() default {};

    DetailsLevel attachLevel() default DetailsLevel.Always;
}
