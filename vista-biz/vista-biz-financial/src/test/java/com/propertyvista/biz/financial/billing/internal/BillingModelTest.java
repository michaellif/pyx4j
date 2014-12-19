/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-03-29
 * @author vlads
 */
package com.propertyvista.biz.financial.billing.internal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.junit.experimental.categories.Category;

import com.pyx4j.entity.xml.XMLEntitySchemaWriter;

import com.propertyvista.config.tests.VistaDBTestBase;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.test.integration.IntegrationTestBase.FunctionalTests;

@Category(FunctionalTests.class)
public class BillingModelTest extends VistaDBTestBase {

    public void testCreateBillingModel() throws FileNotFoundException {
        XMLEntitySchemaWriter.printSchema(new FileOutputStream(new File("target", "bill-model.xsd")), true, Bill.class);
        XMLEntitySchemaWriter.printSchema(new FileOutputStream(new File("target", "lease-model.xsd")), true, Lease.class);
    }
}
