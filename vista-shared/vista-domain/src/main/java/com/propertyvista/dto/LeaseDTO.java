/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-29
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.dto;

import com.pyx4j.entity.annotations.ExtendsDBO;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IList;

import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseAgreement;

@Transient
@ExtendsDBO
public interface LeaseDTO extends Lease {

    TransactionHistoryDTO transactionHistory();

    // -----------------------------------------------------
    // temporary runtime data:

    IList<ProductItem> selectedServiceItems();

    IList<ProductItem> selectedFeatureItems();

    IList<Concession> selectedConcessions();

    LeaseAgreement currentLeaseAgreement();

    BillDTO billingPreview();
}
