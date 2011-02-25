/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 12, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.portal.domain.pt;

import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.shared.IList;

public interface PotentialTenantFinancial extends IApplicationEntity {

    //TODO @Detached
    // TODO Back-end to retrieve only values for ToString
    PotentialTenantInfo tenant();

    @Owned
    IList<TenantIncome> incomes();

    @Owned
    IList<TenantAsset> assets();

    @Owned
    IList<TenantGuarantor> guarantors();

}
