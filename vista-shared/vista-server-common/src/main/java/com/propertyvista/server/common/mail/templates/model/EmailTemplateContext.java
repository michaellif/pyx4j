/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 2, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.server.common.mail.templates.model;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.security.AbstractUser;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.Lease;

@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface EmailTemplateContext extends IEntity {

    IPrimitive<String> accessToken();

    AbstractUser user();

    Lease lease();

    Tenant tenantInLease();
}
