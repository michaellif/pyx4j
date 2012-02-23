/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 23, 2012
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.domain.policy.policies;

import com.pyx4j.entity.annotations.ColumnId;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.policy.policies.domain.ProductTaxPolicyItem;

public interface ProductTaxPolicyJT extends IEntity {

    @JoinColumn
    ProductTaxPolicy parent();

    @JoinColumn
    ProductTaxPolicyItem child();

    @OrderColumn(OrderId.class)
    IPrimitive<Integer> orderInParent();

    interface OrderId extends ColumnId {
    }
}
