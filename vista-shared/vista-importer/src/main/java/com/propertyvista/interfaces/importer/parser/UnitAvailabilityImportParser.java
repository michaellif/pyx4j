/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-07-11
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.rpc.report.DownloadFormat;
import com.pyx4j.essentials.server.csv.EntityCSVReciver;
import com.pyx4j.essentials.server.csv.XLSLoad;
import com.pyx4j.gwt.server.DateUtils;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.interfaces.importer.model.AptUnitIO;
import com.propertyvista.interfaces.importer.model.BuildingIO;
import com.propertyvista.interfaces.importer.model.ImportIO;
import com.propertyvista.interfaces.importer.xls.UnitModel;

public class UnitAvailabilityImportParser implements ImportParser {

    private static final I18n i18n = I18n.get(UnitAvailabilityImportParser.class);

    private final static Logger log = LoggerFactory.getLogger(UnitAvailabilityImportParser.class);

    private final ImportIO importIO = EntityFactory.create(ImportIO.class);

    @Override
    public ImportIO parse(byte[] data, DownloadFormat format) {
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
            EntityCSVReciver<UnitModel> reciver = new UnitModelCSVReciver(loader.getSheetName(sheetNumber));
            try {
                if (!loader.loadSheet(sheetNumber, reciver)) {
                    new UserRuntimeException(i18n.tr("Column heder decalarion not found"));
                }
            } catch (UserRuntimeException e) {
                log.error("XLSLoad error", e);
                throw new UserRuntimeException(i18n.tr("{0} on sheet ''{1}''", e.getMessage(), loader.getSheetName(sheetNumber)));
            }
            convertUnits(reciver.getEntities());
        }

        return importIO;
    }

    private static class UnitModelCSVReciver extends EntityCSVReciver<UnitModel> {
        String sheetNumber;

        public UnitModelCSVReciver(String sheetName) {
            super(UnitModel.class);
            this.sheetNumber = sheetName;
            this.setHeaderLinesCount(1, 2);
            this.setHeadersMatchMinimum(3);
            this.setVerifyRequiredHeaders(true);
            this.setVerifyRequiredValues(true);
        }

        @Override
        public void onRow(UnitModel entity) {
            if (!entity.isNull()) {
                entity._import().row().setValue(getCurrentRow());
                entity._import().sheet().setValue(sheetNumber);
                super.onRow(entity);
            }
        }

    }

    private void convertUnits(List<UnitModel> entities) {
        Map<String, BuildingIO> buildings = new HashMap<String, BuildingIO>();
        for (UnitModel unitModel : entities) {
            BuildingIO building = buildings.get(unitModel.property().getValue());
            if (building == null) {
                building = EntityFactory.create(BuildingIO.class);
                building.propertyCode().setValue(unitModel.property().getValue());
                building._import().row().setValue(unitModel._import().row().getValue());
                building._import().sheet().setValue(unitModel._import().sheet().getValue());
                buildings.put(unitModel.property().getValue(), building);
                importIO.buildings().add(building);
            }

            AptUnitIO unit = EntityFactory.create(AptUnitIO.class);
            unit.number().setValue(unitModel.unit().getValue());
            if (!unitModel.marketRent().isNull()) {
                unit.marketRent().setValue(parseMoney(unitModel.marketRent().getValue(), unitModel));
            }
            if (!unitModel.newMarketRent().isNull()) {
                unit.marketRent().setValue(parseMoney(unitModel.newMarketRent().getValue(), unitModel));
            }
            if (!unitModel.status().isNull() && unitModel.status().getValue().toLowerCase().equals("move in")) {
                unit.availableForRent().set(null);
            }
            if (!unitModel.status().isNull() && unitModel.status().getValue().toLowerCase().equals("move out")) {
                unit.availableForRent().setValue(new LogicalDate(DateUtils.detectDateformat(unitModel.date().getValue())));
            }
            if (!unitModel.status().isNull() && unitModel.status().getValue().toLowerCase().equals("vacant")) {
                unit.availableForRent().setValue(new LogicalDate());
            }
            building = insertUnit(building, unit);
        }
    }

    private BuildingIO insertUnit(BuildingIO building, AptUnitIO unit) {
        for (AptUnitIO aptUnitIO : building.units()) {
            if (aptUnitIO.number().getValue().equals(unit.number().getValue())) {
                // TODO what's going on with duplicate units with different status here?
            }
        }
        building.units().add(unit);
        return building;
    }

    private BigDecimal parseMoney(String money, UnitModel unitModel) {
        NumberFormat nf = new DecimalFormat("#,###.##");
        try {
            return new BigDecimal(nf.parse(money).doubleValue());
        } catch (ParseException e) {
            throw new UserRuntimeException(i18n.tr("You have an erroneous Market Rent value of ''{0}'' for unit #{1} in building ''{2}''.", unitModel
                    .marketRent().getStringView(), unitModel.unit().getStringView(), unitModel.property().getStringView()));
        }
    }
}
