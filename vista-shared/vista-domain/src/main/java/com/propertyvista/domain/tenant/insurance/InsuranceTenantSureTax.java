/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-17
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.tenant.insurance;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.ColumnId;
import com.pyx4j.entity.annotations.Inheritance;
import com.pyx4j.entity.annotations.Inheritance.InheritanceStrategy;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

@Inheritance(strategy = InheritanceStrategy.SINGLE_TABLE)
@AbstractEntity
public interface InsuranceTenantSureTax extends IEntity {

    interface OrderInOwner extends ColumnId {
    }

    @OrderColumn(OrderInOwner.class)
    IPrimitive<Integer> orderInOwner();

    IPrimitive<BigDecimal> absoluteAmount();

    IPrimitive<String> description();

    IPrimitive<String> businessLine();
}
