/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-07-09
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer.xls;

import javax.xml.bind.annotation.XmlTransient;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.essentials.rpc.ImportColumn;

import com.propertyvista.interfaces.importer.model.ImportInformation;

@Transient
public interface UnitModel extends IEntity {

    @ImportColumn(ignore = true)
    @XmlTransient
    ImportInformation _import();

    @ImportColumn(names = { "Property", "Property Code" })
    @NotNull
    IPrimitive<String> property();

    @ImportColumn(names = { "Unit", "Unit#" })
    @NotNull
    IPrimitive<String> unit();

    IPrimitive<String> unitType();

    IPrimitive<String> unitSqFt();

    // Rent Role Update

    IPrimitive<String> marketRent();

    IPrimitive<String> newMarketRent();

    IPrimitive<String> status();

    IPrimitive<String> date();

    IPrimitive<String> marketingName();

    IPrimitive<String> description();

    IPrimitive<String> beds();

    IPrimitive<String> baths();

}
