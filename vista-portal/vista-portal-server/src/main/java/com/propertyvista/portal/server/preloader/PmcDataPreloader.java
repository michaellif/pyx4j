/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 4, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.MediaFile;
import com.propertyvista.domain.company.Portfolio;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductCatalog;
import com.propertyvista.domain.property.PropertyManager;
import com.propertyvista.domain.property.asset.Boiler;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.Elevator;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.Locker;
import com.propertyvista.domain.property.asset.LockerArea;
import com.propertyvista.domain.property.asset.Parking;
import com.propertyvista.domain.property.asset.ParkingSpot;
import com.propertyvista.domain.property.asset.Roof;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.AptUnitItem;
import com.propertyvista.domain.property.vendor.Vendor;
import com.propertyvista.interfaces.importer.BuildingImporter;
import com.propertyvista.interfaces.importer.ImportCounters;
import com.propertyvista.interfaces.importer.ImportUtils;
import com.propertyvista.interfaces.importer.converter.MediaConfig;
import com.propertyvista.interfaces.importer.model.AptUnitIO;
import com.propertyvista.interfaces.importer.model.BuildingIO;
import com.propertyvista.interfaces.importer.model.FloorplanIO;
import com.propertyvista.interfaces.importer.model.ImportIO;
import com.propertyvista.preloader.BaseVistaDevDataPreloader;
import com.propertyvista.server.common.reference.geo.SharedGeoLocator;
import com.propertyvista.server.domain.FileBlob;
import com.propertyvista.server.domain.FileImageThumbnailBlob;

public class PmcDataPreloader extends BaseVistaDevDataPreloader {

    private final static Logger log = LoggerFactory.getLogger(PmcDataPreloader.class);

    private String data = null;

    public PmcDataPreloader() {
        try {
            data = IOUtils.getUTF8TextResource("pmc/" + NamespaceManager.getNamespace() + ".xml", PmcDataPreloader.class);
        } catch (IOException e) {
            log.error("resource load error", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public String delete() {
        if (ApplicationMode.isDevelopment()) {
            return deleteAll(Complex.class, Building.class, AptUnit.class, AptUnitItem.class, Floorplan.class, Vendor.class, Elevator.class, Boiler.class,
                    Roof.class, Parking.class, ParkingSpot.class, LockerArea.class, Locker.class, MediaFile.class, FileImageThumbnailBlob.class,
                    FileBlob.class, Feature.class, PropertyManager.class, ProductCatalog.class);
        } else {
            return "This is production";
        }
    }

    public boolean hasData() {
        return (data != null);
    }

    @Override
    public String create() {
        if (data != null) {
            MediaConfig mediaConfig = new MediaConfig();
            mediaConfig.baseFolder = "data";
            mediaConfig.ignoreMissingMedia = true;
            mediaConfig.mimizePreloadDataSize = true;

            // TODO remove after Demo of when implemented in importer
            if (DemoData.vistaDemo) {
                // create some portfolios:
                List<Portfolio> portfolios = new Vector<Portfolio>();
                for (String pname : new String[] { "GTA", "East region", "West region" }) {
                    Portfolio p = EntityFactory.create(Portfolio.class);
                    p.name().setValue(pname);
                    portfolios.add(p);
                }
                Persistence.service().persist(portfolios);
            }

            ImportIO importIO = ImportUtils.parse(ImportIO.class, new InputSource(new StringReader(data)));
            ImportCounters counters = new ImportCounters();
            for (BuildingIO building : importIO.buildings()) {
                makeUniqueUnits(building);
                counters.add(new BuildingImporter().persist(building, mediaConfig));
                if ((config().minimizePreloadTime) && (counters.buildings > config().numResidentialBuildings)) {
                    break;
                }
            }
            SharedGeoLocator.save();
            return SimpleMessageFormat.format("Imported {0} building(s), {1} floorplan(s), {2} unit(s)", counters.buildings, counters.floorplans,
                    counters.units);
        } else {
            return null;
        }
    }

    private void makeUniqueUnits(BuildingIO building) {
        List<AptUnitIO> units = new ArrayList<AptUnitIO>();
        for (FloorplanIO floorplanIO : building.floorplans()) {
            for (AptUnitIO unit : floorplanIO.units()) {
                units.add(unit);
            }
        }
        Collections.sort(units, new Comparator<AptUnitIO>() {
            @Override
            public int compare(AptUnitIO u1, AptUnitIO u2) {
                return u1.number().getValue().compareTo(u2.number().getValue());
            }
        });
        int cnt = 1;
        for (AptUnitIO unit : units) {
            unit.number().setValue(String.valueOf(cnt));
            cnt++;
        }
    }
}
