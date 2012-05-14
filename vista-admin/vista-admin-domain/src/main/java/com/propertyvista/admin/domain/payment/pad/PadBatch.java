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
package com.propertyvista.admin.domain.payment.pad;

import java.math.BigDecimal;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.GwtBlacklist;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;

@Table(namespace = VistaNamespace.adminNamespace)
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
@GwtBlacklist
public interface PadBatch extends IEntity {

    @Override
    @Indexed
    @OrderColumn
    IPrimitive<Key> id();

    @Owner
    @JoinColumn
    PadFile padFile();

    @Owned(cascade = {})
    @Detached(level = AttachLevel.Detached)
    IList<PadDebitRecord> records();

    @Indexed(group = { "m,1" })
    IPrimitive<String> pmcNamespace();

    @Indexed(group = { "m,2" })
    IPrimitive<Key> merchantAccountKey();

    /**
     * TBD Copy of merchantAccount at the time of Batch creation
     */
    @Length(8)
    @RpcTransient
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
    IObject<BigDecimal> batchAmount();

    IPrimitive<String> acknowledgementStatusCode();

}
