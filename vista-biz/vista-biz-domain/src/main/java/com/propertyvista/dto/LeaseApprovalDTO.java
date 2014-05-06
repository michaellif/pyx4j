/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-09-30
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.dto;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

@Transient
public interface LeaseApprovalDTO extends IEntity {

    @I18n
    public enum SuggestedDecision {

        @Translate("Can not be calculated - Credit Check is missing or obsolete for one or more person")
        RunCreditCheck,

        Approve,

        RequestGuarantor,

        ManualReview,

        Decline;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> rentAmount();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> totalAmountApproved();

    @Format("#0")
    @Caption(name = "Rent Approved")
    @Editor(type = EditorType.percentage)
    IPrimitive<BigDecimal> percenrtageApproved();

    IPrimitive<SuggestedDecision> recommendedDecision();

    IPrimitive<String> reason();

    IList<LeaseParticipanApprovalDTO> participants();
}
