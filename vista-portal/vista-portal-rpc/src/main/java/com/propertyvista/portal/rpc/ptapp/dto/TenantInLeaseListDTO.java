/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-09
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.rpc.ptapp.dto;

import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.IBoundToApplication;
import com.propertyvista.domain.tenant.TenantInLease;

@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface TenantInLeaseListDTO extends IEntity, IBoundToApplication {

    @Length(6)
    IList<TenantInLease> tenants();

    //TODO this should be AptUnit property
    IPrimitive<Integer> tenantsMaximum();
}
