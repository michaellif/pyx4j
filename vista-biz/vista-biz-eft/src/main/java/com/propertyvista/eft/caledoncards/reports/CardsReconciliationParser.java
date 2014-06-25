/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 24, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.eft.caledoncards.reports;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.essentials.server.csv.CSVLoad;
import com.pyx4j.essentials.server.csv.CSVParser;
import com.pyx4j.essentials.server.csv.EntityCSVReciver;

import com.propertyvista.operations.domain.eft.cards.to.CardsReconciliationCardTotalRecord;
import com.propertyvista.operations.domain.eft.cards.to.CardsReconciliationMerchantTotalRecord;

public class CardsReconciliationParser {

    List<CardsReconciliationMerchantTotalRecord> parsMerchantTotalReport(File file) {
        EntityCSVReciver<CardsReconciliationMerchantTotalRecord> reciver = EntityCSVReciver.create(CardsReconciliationMerchantTotalRecord.class);

        reciver.setVerifyRequiredHeaders(true);
        reciver.setVerifyRequiredValues(true);

        InputStream is;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found");
        }
        CSVLoad.loadFile(is, StandardCharsets.US_ASCII, new CSVParser(), reciver);
        if (!reciver.isHeaderFound()) {
            throw new UserRuntimeException("Column header declaration not found in file " + file.getName());
        }
        return reciver.getEntities();
    }

    List<CardsReconciliationCardTotalRecord> parsCardTotalReport(File file) {
        EntityCSVReciver<CardsReconciliationCardTotalRecord> reciver = new EntityCSVReciver<CardsReconciliationCardTotalRecord>(
                CardsReconciliationCardTotalRecord.class) {

            @Override
            protected Object parsePrimitive(IObject<?> member, Class<?> valueClass, String value) {
                if (valueClass == CardsReconciliationCardTotalRecord.CardTotalRecordType.class) {
                    return CardsReconciliationCardTotalRecord.CardTotalRecordType.valueOf(value.replace(" ", ""));
                } else {
                    return super.parsePrimitive(member, valueClass, value);
                }
            }

        };

        reciver.setVerifyRequiredHeaders(true);
        reciver.setVerifyRequiredValues(true);
        InputStream is;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found");
        }
        CSVLoad.loadFile(is, StandardCharsets.US_ASCII, new CSVParser(), reciver);
        if (!reciver.isHeaderFound()) {
            throw new UserRuntimeException("Column header declaration not found in file " + file.getName());
        }
        return reciver.getEntities();
    }

}
