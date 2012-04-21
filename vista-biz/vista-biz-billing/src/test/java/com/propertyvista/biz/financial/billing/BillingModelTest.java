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
 * @version $Id$
 */
package com.propertyvista.biz.financial.billing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import junit.framework.TestCase;

import com.pyx4j.essentials.server.xml.XMLEntitySchemaWriter;

import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.tenant.lease.Lease;

public class BillingModelTest extends TestCase {

    public void testCreateBillingModel() throws FileNotFoundException {
        XMLEntitySchemaWriter.printSchema(new FileOutputStream(new File("target", "bill-model.xsd")), true, Bill.class);
        XMLEntitySchemaWriter.printSchema(new FileOutputStream(new File("target", "lease-model.xsd")), true, Lease.class);
    }
}
