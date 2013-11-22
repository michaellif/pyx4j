/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-10-03
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto.legal.n4;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.crm.rpc.dto.financial.autopayreview.BulkEditableEntity;
import com.propertyvista.domain.legal.LegalNoticeCandidate;
import com.propertyvista.domain.legal.n4.N4LegalLetter;

@Transient
public interface LegalNoticeCandidateDTO extends LegalNoticeCandidate, BulkEditableEntity {

    IPrimitive<String> building();

    IPrimitive<String> address();

    IPrimitive<String> unit();

    IPrimitive<String> leaseIdString();

    IPrimitive<LogicalDate> moveIn();

    IPrimitive<LogicalDate> moveOut();

    @Override
    @Editor(type = EditorType.money)
    @Format("#,##0.00")
    IPrimitive<BigDecimal> amountOwed();

    N4LegalLetter n4LetterId();

}
