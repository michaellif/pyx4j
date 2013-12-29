/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 10, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.ils.gottarent.setup;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.essentials.rpc.ImportColumn;

@Transient
public interface PropertyId extends IEntity {
    @ImportColumn(names = { "GRID", "id" })
    IPrimitive<String> identificator();

    @ImportColumn(names = { "Street Number", "info_address_street_number" })
    IPrimitive<String> streetNumber();

    @ImportColumn(names = { "Street Address" })
    IPrimitive<String> grStreetAddress();

    @ImportColumn(names = { "info_address_street_name" })
    IPrimitive<String> pvStreetName();

    @ImportColumn(names = { "info_address_street_type" })
    IPrimitive<String> pvStreetType();

    @ImportColumn(names = { "info_address_street_direction" })
    IPrimitive<String> pvStreetDir();

    @ImportColumn(names = { "Postal Code", "info_address_postal_code" })
    IPrimitive<String> postalCode();

    @ImportColumn(names = { "City", "info_address_city" })
    IPrimitive<String> city();

    @ImportColumn(names = { "Province", "info_address_province" })
    IPrimitive<String> province();
}
