/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-07-31
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.dto;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IList;

import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.tenant.lease.LeaseTerm;

@Transient
public interface LeaseTermDTO extends LeaseTerm {

    // parent
    LeaseDTO2 parentLease();

    // -----------------------------------------------------
    // temporary runtime data:

    IList<ProductItem> selectedServiceItems();

    IList<ProductItem> selectedFeatureItems();

    IList<Concession> selectedConcessions();

    BillDTO billingPreview();
}
