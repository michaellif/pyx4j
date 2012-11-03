/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 20, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.generator.gdo;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.tenant.CustomerScreening;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;

@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
//Generator/Preloader Data OBject only!
public interface TenantSummaryGDO extends IEntity {

    Customer tenant();

    LeaseTermTenant tenantInLease();

    CustomerScreening tenantScreening();

    IList<CustomerScreening> guarantorScreening();
}
