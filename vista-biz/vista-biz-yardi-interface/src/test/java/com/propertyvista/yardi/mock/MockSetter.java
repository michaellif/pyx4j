/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 30, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.yardi.mock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import com.propertyvista.yardi.mock.updater.Name;

/**
 * Helper to find implementation of updater
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface MockSetter {

    Class<? extends Name> value();
}
