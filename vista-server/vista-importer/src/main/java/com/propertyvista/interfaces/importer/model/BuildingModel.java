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
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.essentials.rpc.ImportColumn;
import com.pyx4j.essentials.rpc.report.ReportColumn;

@Transient
public interface BuildingModel extends IEntity {

    @ImportColumn(ignore = true)
    @ReportColumn(ignore = true)
    @XmlTransient
    ImportInformation _import();

    @ImportColumn(ignore = true)
    @ReportColumn(ignore = true)
    @XmlTransient
    PadProcessorInformation _processorInformation();

//    @ImportColumn(names = { "Ignore", "Import Ignore" })
//    IPrimitive<Boolean> ignore();

    @ImportColumn(names = { "propertyCode" })
    IPrimitive<String> property();

    @ImportColumn(names = { "name" })
    IPrimitive<String> name();

    @ImportColumn(names = { "streetNumber" })
    IPrimitive<String> streetNumber();

    @ImportColumn(names = { "streetName" })
    IPrimitive<String> streetName();

    @ImportColumn(names = { "postalCode" })
    IPrimitive<String> postalCode();

    @ImportColumn(names = { "city" })
    IPrimitive<String> city();

    @ImportColumn(names = { "province" })
    IPrimitive<String> province();

    @ImportColumn(names = { "country" })
    IPrimitive<String> country();

}
