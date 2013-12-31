/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-05-17
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer.pad;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.essentials.rpc.ImportColumn;

@Transient
public interface EFTModel extends IEntity {

    @ImportColumn(names = { "batch_id" })
    IPrimitive<String> batchId();

    @ImportColumn(names = { "Lease Id", "Tenant Id", "client_id" })
    IPrimitive<String> leaseId();

    @ImportColumn(names = { "Institution", "Bank Id", "bank_id" })
    @NotNull
    IPrimitive<String> bankId();

    @ImportColumn(names = { "Transit Number", "Transit", "branch_transit_number" })
    @NotNull
    IPrimitive<String> transitNumber();

    @ImportColumn(names = { "Account Number", "Account", "account_number" })
    @NotNull
    IPrimitive<String> accountNumber();

    @ImportColumn(names = { "Amount" })
    IPrimitive<String> amount();

    @ImportColumn(ignore = true)
    IPrimitive<Boolean> eftCreated();

}
