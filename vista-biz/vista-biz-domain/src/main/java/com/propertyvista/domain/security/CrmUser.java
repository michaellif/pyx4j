/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 5, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.security;

import javax.xml.bind.annotation.XmlTransient;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.JoinTable;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.core.AttachLevel;

import com.propertyvista.domain.preferences.CrmUserPreferences;
import com.propertyvista.domain.security.common.AbstractPmcUser;
import com.propertyvista.domain.security.common.AbstractUserCredential;

@Caption(name = "User")
@DiscriminatorValue("CrmUser")
public interface CrmUser extends AbstractPmcUser {

    public static final String VISTA_SUPPORT_ACCOUNT_EMAIL = "support@propertyvista.com";

    /**
     * This Magic can be used to build query by Roles
     */
    @RpcTransient
    @Detached(level = AttachLevel.Detached)
    @JoinTable(value = CrmUserCredential.class, mappedBy = AbstractUserCredential.UserColumnId.class)
    CrmUserCredential credential();

    @Owned
    @Detached(level = AttachLevel.Detached)
    @XmlTransient
    CrmUserPreferences preferences();
}
