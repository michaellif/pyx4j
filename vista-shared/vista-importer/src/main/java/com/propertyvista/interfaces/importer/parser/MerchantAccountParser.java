/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 7, 2013
 * @author yuriyl
 * @version $Id$
 */
package com.propertyvista.interfaces.importer.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.essentials.server.csv.EntityCSVReciver;
import com.pyx4j.essentials.server.csv.XLSLoad;
import com.pyx4j.gwt.shared.DownloadFormat;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.interfaces.importer.model.MerchantAccountFileModel;

public class MerchantAccountParser {

    private final static Logger log = LoggerFactory.getLogger(MerchantAccountParser.class);

    private static final I18n i18n = I18n.get(MerchantAccountParser.class);

    public List<MerchantAccountFileModel> parseFile(byte[] data, DownloadFormat format) {
        List<MerchantAccountFileModel> accounts = new ArrayList<MerchantAccountFileModel>();
        if ((format != DownloadFormat.XLS) && (format != DownloadFormat.XLSX)) {
            throw new IllegalArgumentException();
        }
        XLSLoad loader;
        try {
            loader = new XLSLoad(new ByteArrayInputStream(data), format == DownloadFormat.XLSX);
        } catch (IOException e) {
            log.error("XLSLoad error", e);
            throw new UserRuntimeException(i18n.tr("Unable to read Excel File, {0}", e.getMessage()));
        }
        int sheets = loader.getNumberOfSheets();

        for (int sheetNumber = 0; sheetNumber < sheets; sheetNumber++) {
            if (loader.isSheetHidden(sheetNumber)) {
                continue;
            }
            EntityCSVReciver<MerchantAccountFileModel> receiver = new MerchantAccountFileCSVReciver(loader.getSheetName(sheetNumber));
            try {
                if (loader.loadSheet(sheetNumber, receiver)) {
                    if (!receiver.isHeaderFound()) {
                        throw new UserRuntimeException(i18n.tr("Column header declaration not found"));
                    }
                }
            } catch (UserRuntimeException e) {
                log.error("XLSLoad error", e);
                throw new UserRuntimeException(i18n.tr("{0} on sheet ''{1}''", e.getMessage(), loader.getSheetName(sheetNumber)));
            }
            accounts.addAll(receiver.getEntities());
        }
        return accounts;

    }

    private static class MerchantAccountFileCSVReciver extends EntityCSVReciver<MerchantAccountFileModel> {
        String sheetNumber;

        public MerchantAccountFileCSVReciver(String sheetName) {
            super(MerchantAccountFileModel.class);
            this.sheetNumber = sheetName;
            this.setMemberNamesAsHeaders(false);
            this.setHeaderLinesCount(1, 2);
            this.setHeaderIgnoreCase(true);
            this.setHeadersMatchMinimum(3);
            this.setVerifyRequiredHeaders(true);
            this.setVerifyRequiredValues(true);
        }

        @Override
        public void onRow(MerchantAccountFileModel entity) {
            if (!entity.isNull()) {
                entity._import().row().setValue(getCurrentRow());
                entity._import().sheet().setValue(sheetNumber);
                super.onRow(entity);
            }
        }

    }
}
