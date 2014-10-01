/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 10, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.security;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.core.AttachLevel;

import com.propertyvista.domain.security.common.AbstractPmcUser;
import com.propertyvista.domain.tenant.CustomerPreferences;

@Caption(name = "User")
@DiscriminatorValue("CustomerUser")
public interface CustomerUser extends AbstractPmcUser {

    @Owned
    @Detached(level = AttachLevel.Detached)
    CustomerPreferences preferences();

}
