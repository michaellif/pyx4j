/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-08-28
 * @author ArtyomB
 */
package com.propertyvista.crm.rpc.dto.financial.autopayreview;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.tenant.lease.BillableItem;

@Transient
public interface PapChargeReviewDTO extends IEntity, BulkEditableEntity {

    public enum ChangeType {

        Unchanged, Changed, Removed, New

    }

    /** This is a reference to DBO */
    BillableItem billableItem();

    IPrimitive<String> chargeName();

    IPrimitive<ChangeType> changeType();

    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> suspendedPrice();

    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> suspendedPapAmount();

    @Editor(type = EditorType.percentage)
    IPrimitive<BigDecimal> suspendedPapPercent();

    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> newPrice();

    /** user defined: (this field is supposed to be in sync with percent) */
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> newPapAmount();

    /** user defined: (this field is supposed to be in sync with amount) */
    @Editor(type = EditorType.percentage)
    IPrimitive<BigDecimal> newPapPercent();

    @Editor(type = EditorType.percentage)
    /** denormalized field, must be equal to <code>(suspendedPapAmount - newPapAmount)/suspenedPapAmount</code> */
    IPrimitive<BigDecimal> changePercent();

    /** this is a helper field that is used for populating DataGrid based view */
    @Deprecated
    PapReviewCaptionDTO _parentPap();

    /**
     * this is a helper field that is used for populating DataGrid based view: denotes the first charge of a charges group beloning to the same PAP for a
     * sequence of charges.
     */
    @Deprecated
    IPrimitive<Boolean> _isPivot();

}
