/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 6, 2013
 * @author yuriyl
 * @version $Id$
 */
package com.propertyvista.interfaces.importer.model;

import javax.xml.bind.annotation.XmlTransient;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.essentials.rpc.ImportColumn;


@Transient
public interface PadFileModel extends IEntity {

    @ImportColumn(ignore = true)
    @XmlTransient
    ImportInformation _import();

    @ImportColumn(names = { "Tenant ID" })
    @NotNull
    IPrimitive<String> tenantId();

    @ImportColumn(names = { "Bank Account Holder", "Name" })
    IPrimitive<String> name();

    @ImportColumn(names = { "Institution" })
    @NotNull
    IPrimitive<String> bankId();

    @ImportColumn(names = { "Transit Number", "Transit" })
    @NotNull
    IPrimitive<String> transitNumber();

    @ImportColumn(names = { "Account Number", "Account" })
    @NotNull
    IPrimitive<String> accountNumber();

    IPrimitive<String> charge();

}
