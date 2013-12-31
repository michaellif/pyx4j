/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 26, 2012
 * @author yuriyl
 * @version $Id$
 */
package com.propertyvista.interfaces.importer.parser;

import java.io.ByteArrayInputStream;

import org.xml.sax.InputSource;

import com.pyx4j.gwt.shared.DownloadFormat;

import com.propertyvista.interfaces.importer.ImportUtils;
import com.propertyvista.interfaces.importer.model.ImportIO;

public class VistaXMLImportParser implements ImportParser {

    @Override
    public ImportIO parse(byte[] data, DownloadFormat format) {
        switch (format) {
        case XML:
            return ImportUtils.parse(ImportIO.class, new InputSource(new ByteArrayInputStream(data)));
        default:
            throw new Error("Unsupported file format");
        }
    }

}
