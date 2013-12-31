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
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.essentials.server.csv.EntityCSVReciver;
import com.pyx4j.essentials.server.csv.XLSLoad;
import com.pyx4j.gwt.server.DateUtils;
import com.pyx4j.gwt.shared.DownloadFormat;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.interfaces.importer.model.AptUnitIO;
import com.propertyvista.interfaces.importer.model.BuildingIO;
import com.propertyvista.interfaces.importer.model.FloorplanIO;
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
            if (loader.isSheetHidden(sheetNumber)) {
                continue;
            }
            EntityCSVReciver<UnitModel> receiver = new UnitModelCSVReciver(loader.getSheetName(sheetNumber));
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
            convertUnits(receiver.getEntities());
        }

        return importIO;
    }

    private static class UnitModelCSVReciver extends EntityCSVReciver<UnitModel> {
        String sheetNumber;

        public UnitModelCSVReciver(String sheetName) {
            super(UnitModel.class);
            this.sheetNumber = sheetName;
            this.setMemberNamesAsHeaders(false);
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
        Map<String, AptUnitIO> aptUnits = new HashMap<String, AptUnitIO>();
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

            AptUnitIO unit = aptUnits.get(unitModel.property().getValue() + "!" + unitModel.unit().getValue());
            if (unit == null) {
                unit = EntityFactory.create(AptUnitIO.class);
                building = insertUnit(unit, unitModel, building);
            } else {
                if (unit.availableForRent().isNull()) {
                    continue;
                }
            }
            unit._import().row().setValue(unitModel._import().row().getValue());
            unit._import().sheet().setValue(unitModel._import().sheet().getValue());
            unit.number().setValue(unitModel.unit().getValue());
            if (!unitModel.marketRent().isNull()) {
                unit.marketRent().setValue(parseMoney(unitModel.marketRent().getValue(), unitModel));
            }
            if (!unitModel.newMarketRent().isNull()) {
                unit.marketRent().setValue(parseMoney(unitModel.newMarketRent().getValue(), unitModel));
            }
            if (!unitModel.unitSqFt().isNull()) {
                unit.area().setValue(parseArea(unitModel.unitSqFt().getValue(), unitModel));
            }

            if (!unitModel.status().isNull()) {
                if (unitModel.status().getValue().toLowerCase().equals("move in")) {
                    unit.availableForRent().set(null);
                } else if (unitModel.status().getValue().toLowerCase().equals("move out")) {
                    unit.availableForRent().setValue(new LogicalDate(DateUtils.detectDateformat(unitModel.date().getValue())));
                } else if (unitModel.status().getValue().toLowerCase().equals("vacant")) {
                    unit.availableForRent().setValue(new LogicalDate());
                }
            }

            aptUnits.put(unitModel.property().getValue() + "!" + unit.number().getValue(), unit);
        }
    }

    private BigDecimal parseMoney(String money, UnitModel unitModel) {
        NumberFormat nf = new DecimalFormat("#,###.##");
        try {
            return new BigDecimal(nf.parse(money).doubleValue());
        } catch (ParseException e) {
            throw new UserRuntimeException(i18n.tr("You have an erroneous Market Rent or New Market Rent value of for unit #{0} in building ''{1}''.",
                    unitModel.unit().getStringView(), unitModel.property().getStringView()));
        }
    }

    private Double parseArea(String area, UnitModel unitModel) {
        try {
            return Double.valueOf(area);
        } catch (NumberFormatException e) {
            throw new UserRuntimeException(i18n.tr("You have an erroneous Unit Sq Ft value for unit #{0} in building ''{1}''.", unitModel.unit()
                    .getStringView(), unitModel.property().getStringView()));
        }
    }

    private Integer parseInt(String value, UnitModel unitModel) {
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            throw new UserRuntimeException(i18n.tr("You have an erroneous Beds or Baths value for unit #{0} in building ''{1}''.", unitModel.unit()
                    .getStringView(), unitModel.property().getStringView()));
        }
    }

    private BuildingIO insertUnit(AptUnitIO unit, UnitModel unitModel, BuildingIO building) {
        if (unitModel.unitType().isNull()) { // if there's not floorplan in the file, just add units to building
            building.units().add(unit);
        } else {
            int floorplansSize = building.floorplans().size();
            if (floorplansSize == 0) {
                FloorplanIO newFloorplan = EntityFactory.create(FloorplanIO.class);
                newFloorplan.name().setValue(unitModel.unitType().getValue());
                newFloorplan._import().row().setValue(unitModel._import().row().getValue());
                newFloorplan._import().sheet().setValue(unitModel._import().sheet().getValue());
                if (!unitModel.beds().isNull()) {
                    newFloorplan.bedrooms().setValue(parseInt(unitModel.beds().getValue(), unitModel));
                }
                if (!unitModel.baths().isNull()) {
                    newFloorplan.bathrooms().setValue(parseInt(unitModel.baths().getValue(), unitModel));
                }
                if (!unitModel.description().isNull()) {
                    newFloorplan.description().setValue(unitModel.description().getValue());
                }
                if (!unitModel.marketingName().isNull()) {
                    newFloorplan.marketingName().setValue(unitModel.marketingName().getValue());
                }

                newFloorplan.units().add(unit);
                building.floorplans().add(newFloorplan);
            } else {
                for (int i = 0; i < floorplansSize; i++) {
                    FloorplanIO floorplan = building.floorplans().get(i);
                    if (floorplan.name().getValue().equals(unitModel.unitType().getValue())) {
                        floorplan.units().add(unit);
                        break;
                    }
                    if (i == floorplansSize - 1) {
                        FloorplanIO newFloorplan = EntityFactory.create(FloorplanIO.class);
                        newFloorplan.name().setValue(unitModel.unitType().getValue());
                        if (!unitModel.beds().isNull()) {
                            newFloorplan.bedrooms().setValue(parseInt(unitModel.beds().getValue(), unitModel));
                        }
                        if (!unitModel.baths().isNull()) {
                            newFloorplan.bathrooms().setValue(parseInt(unitModel.baths().getValue(), unitModel));
                        }
                        if (!unitModel.description().isNull()) {
                            newFloorplan.description().setValue(unitModel.description().getValue());
                        }
                        if (!unitModel.marketingName().isNull()) {
                            newFloorplan.marketingName().setValue(unitModel.marketingName().getValue());
                        }
                        newFloorplan.units().add(unit);
                        building.floorplans().add(newFloorplan);
                    }
                }
            }
        }
        return building;
    }
}
