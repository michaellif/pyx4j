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
package com.propertyvista.operations.domain.eft.caledoneft.to;

import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
@Transient
public interface FundsTransferAckRecord extends IEntity {

    @Length(8)
    @NotNull
    IPrimitive<String> terminalId();

    @Length(29)
    @NotNull
    IPrimitive<String> clientId();

    @Length(15)
    IPrimitive<String> transactionId();

    @Length(10)
    @NotNull
    IPrimitive<String> amount();

    @Length(4)
    @NotNull
    IPrimitive<String> acknowledgmentStatusCode();

}
