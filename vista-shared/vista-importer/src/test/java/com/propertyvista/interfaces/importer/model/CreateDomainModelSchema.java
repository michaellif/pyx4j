/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 29, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.pyx4j.essentials.server.xml.XMLEntitySchemaWriter;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;

public class CreateDomainModelSchema {

    public static void main(String[] args) throws FileNotFoundException {
        XMLEntitySchemaWriter.printSchema(Building.class, new FileOutputStream(new File("domain-model-building.xsd")), true);
        XMLEntitySchemaWriter.printSchema(Lease.class, new FileOutputStream(new File("domain-model-lease.xsd")), true);
    }

}
