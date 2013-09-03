/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-27
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.domain.payment.pad;

import java.util.Date;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.financial.FundsTransferType;

@Table(namespace = VistaNamespace.operationsNamespace)
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface PadReconciliationFile extends IEntity {

    // YYYYMMDDhhmmss_reconciliation_rpt_pad.COMPANYID
    public static String FileNameSufix = "_reconciliation_rpt";

    IPrimitive<String> fileName();

    IPrimitive<FundsTransferType> fundsTransferType();

    @Timestamp(Timestamp.Update.Created)
    IPrimitive<Date> created();

    @Owned(cascade = {})
    @Detached(level = AttachLevel.Detached)
    @OrderBy(PrimaryKey.class)
    IList<PadReconciliationSummary> batches();
}
