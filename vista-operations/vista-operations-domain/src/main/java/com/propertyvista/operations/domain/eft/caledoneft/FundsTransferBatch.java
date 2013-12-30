/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 29, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.domain.eft.caledoneft;

import java.math.BigDecimal;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.pmc.Pmc;

@Table(namespace = VistaNamespace.operationsNamespace)
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface FundsTransferBatch extends IEntity {

    @Owner
    @JoinColumn
    @Indexed
    @MemberColumn(notNull = true)
    FundsTransferFile padFile();

    IPrimitive<Integer> batchNumber();

    @Owned(cascade = {})
    @Detached(level = AttachLevel.Detached)
    @OrderBy(PrimaryKey.class)
    IList<FundsTransferRecord> records();

    @ReadOnly
    @MemberColumn(notNull = true)
    @Detached
    @Indexed(group = { "m,1" })
    Pmc pmc();

    @Indexed(group = { "m,2" })
    IPrimitive<Key> merchantAccountKey();

    /**
     * Copy of merchantAccount at the time of Batch creation
     */
    @Length(8)
    IPrimitive<String> merchantTerminalId();

    @Length(3)
    @ToString
    IPrimitive<String> bankId();

    @Length(5)
    @ToString
    IPrimitive<String> branchTransitNumber();

    @Length(12)
    @ToString
    IPrimitive<String> accountNumber();

    // filed editable by CRM

    /**
     * Caledon: Description to appear on client's statement. Typically a merchant's business name.
     */
    @Length(60)
    @Caption(description = "Description to appear on client's statement. Typically a merchant's business name.")
    IPrimitive<String> chargeDescription();

    // Updated when batch is sent to Caledon
    @Format("#0.00")
    @Editor(type = EditorType.moneylabel)
    IPrimitive<BigDecimal> batchAmount();

    IPrimitive<String> acknowledgmentStatusCode();

    IPrimitive<FundsTransferBatchProcessingStatus> processingStatus();

}
