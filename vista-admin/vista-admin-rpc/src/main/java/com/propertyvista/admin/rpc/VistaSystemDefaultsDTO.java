/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-02
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.rpc;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;

import com.propertyvista.admin.domain.vista2pmc.DefaultEquifaxFee;
import com.propertyvista.admin.domain.vista2pmc.DefaultPaymentFees;
import com.propertyvista.admin.domain.vista2pmc.EquifaxMerchantAccount;
import com.propertyvista.admin.domain.vista2pmc.TenantSureMerchantAccount;

@Transient
public interface VistaSystemDefaultsDTO extends IEntity {

    DefaultPaymentFees paymentFees();

    DefaultEquifaxFee equifaxFees();

    EquifaxMerchantAccount equifaxMerchantAccount();

    TenantSureMerchantAccount tenantSureMerchantAccount();
}
