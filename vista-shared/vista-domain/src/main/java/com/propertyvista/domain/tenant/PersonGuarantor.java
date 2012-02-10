/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 19, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.tenant;

import com.pyx4j.entity.annotations.ColumnId;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

@ToStringFormat("{0},  {1}")
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface PersonGuarantor extends IEntity {

    @Owner
    @Detached
    @NotNull
    @Indexed
    @JoinColumn
    PersonScreening guarantee();

    interface OrderInGuarantee extends ColumnId {

    }

    @OrderColumn(OrderInGuarantee.class)
    IPrimitive<Integer> orderInGuarantee();

    @NotNull
    @ReadOnly
    @ToString(index = 0)
    Guarantor guarantor();

    @NotNull
    @ToString(index = 1)
    IPrimitive<PersonRelationship> relationship();

}
