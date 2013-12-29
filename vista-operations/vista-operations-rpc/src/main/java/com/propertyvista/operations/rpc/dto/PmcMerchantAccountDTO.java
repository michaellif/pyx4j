/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-09-14
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.rpc.dto;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IList;

import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.pmc.PmcMerchantAccountIndex;
import com.propertyvista.domain.property.asset.building.Building;

@Transient
public interface PmcMerchantAccountDTO extends PmcMerchantAccountIndex {

    MerchantAccount merchantAccount();

    IList<Building> assignedBuildings();

}
