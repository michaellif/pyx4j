/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-03-28
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.communication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.pyx4j.essentials.server.xml.XMLEntitySchemaWriter;

import com.propertyvista.biz.communication.mail.template.model.EmailTemplateRoot;

public class PrintEmailTemplateVariables {

    public static void main(String[] args) throws FileNotFoundException {
        XMLEntitySchemaWriter.printSchema(new FileOutputStream(new File("target", "EmailTemplateVariables.xsd")), true, EmailTemplateRoot.class,
                EmailTemplateRoot.class);
    }

}
