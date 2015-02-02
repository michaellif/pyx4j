/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 19, 2015
 * @author stanp
 */
package com.propertyvista.domain.eviction;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Adapters;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.legal.n4.N4Batch;
import com.propertyvista.domain.legal.n4.N4LeaseArrears;
import com.propertyvista.domain.legal.n4.N4LeaseData;
import com.propertyvista.shared.adapters.EvictionStatusRecordRemovalAdapter;

@DiscriminatorValue("N4")
@Adapters(entityModificationAdapters = EvictionStatusRecordRemovalAdapter.class)
public interface EvictionStatusN4 extends EvictionStatus {

    @Detached
    N4LeaseArrears leaseArrears();

    /**
     * This property is only created if the entity is edited and saved;
     * otherwise the originating batch data is used
     */
    @Detached
    N4LeaseData n4Data();

    /**
     * N4Batch reference is only available if the status record is created by N4 Batch process
     */
    @JoinColumn
    @Indexed
    @ReadOnly
    @Detached
    N4Batch originatingBatch();

    IPrimitive<LogicalDate> terminationDate();

    @NotNull
    IPrimitive<LogicalDate> expiryDate();

    @Editor(type = EditorType.money)
    @NotNull
    IPrimitive<BigDecimal> cancellationBalance();

    @Detached
    @OrderBy(EvictionDocument.PrintOrderId.class)
    IList<EvictionDocument> generatedForms();
}
