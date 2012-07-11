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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.rpc.report.DownloadFormat;
import com.pyx4j.essentials.server.csv.EntityCSVReciver;
import com.pyx4j.essentials.server.csv.XLSLoad;
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
            loader = new XLSLoad(new ByteArrayInputStream(data));
        } catch (IOException e) {
            log.error("XLSLoad error", e);
            throw new UserRuntimeException(i18n.tr("Unable to read Excel File, {1}", e.getMessage()));
        }

        int sheets = loader.getNumberOfSheets();
        for (int sheetNumber = 0; sheetNumber < sheets; sheetNumber++) {
            EntityCSVReciver<UnitModel> reciver = new UnitModelCSVReciver();
            loader.loadSheet(sheetNumber, reciver);
            convertUnits(reciver.getEntities());
        }

        return importIO;
    }

    private static class UnitModelCSVReciver extends EntityCSVReciver<UnitModel> {

        public UnitModelCSVReciver() {
            super(UnitModel.class);
            this.setHeaderLinesCount(2);
            this.setHeadersMatchMinimum(3);
        }

    }

    private void convertUnits(List<UnitModel> entities) {
        Map<String, BuildingIO> buildings = new HashMap<String, BuildingIO>();
        for (UnitModel unitModel : entities) {
            BuildingIO building = buildings.get(unitModel.property().getValue());
            if (building == null) {
                building = EntityFactory.create(BuildingIO.class);
                building.propertyCode().setValue(unitModel.property().getValue());
                buildings.put(unitModel.property().getValue(), building);
            }
            AptUnitIO unit = EntityFactory.create(AptUnitIO.class);
            //TODO convert unit.

            building.units().add(unit);
        }

        importIO.buildings().addAll(buildings.values());
    }

}
