/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 29, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.policy.framework;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Can be used to define the lowest node in {@link PolicyNode} hierarchy that a policy annotated by it can be applied to.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.TYPE })
@Inherited
public @interface LowestApplicableNode {

    Class<? extends PolicyNode> value();
}
