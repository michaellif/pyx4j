/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 19, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.payment.dbp.simulator;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import com.propertyvista.operations.domain.payment.dbp.simulator.DirectDebitSimFile;

public class RemconFileWriter implements Closeable {

    private final Writer writer;

    private RemconFileWriter(File file) throws IOException {
        writer = new FileWriter(file);
    }

    public static void write(DirectDebitSimFile directDebitSimFile, File file) throws IOException {
        //TODO
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}
