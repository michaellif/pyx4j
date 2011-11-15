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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.domain.contact.Email;
import com.propertyvista.domain.contact.Phone;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ServiceCatalog;
import com.propertyvista.domain.marketing.yield.Amenity;
import com.propertyvista.domain.media.Media;
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
import com.propertyvista.interfaces.importer.BuildingUpdater;
import com.propertyvista.interfaces.importer.ImportCounters;
import com.propertyvista.interfaces.importer.ImportUtils;
import com.propertyvista.interfaces.importer.converter.MediaConfig;
import com.propertyvista.interfaces.importer.model.BuildingIO;
import com.propertyvista.interfaces.importer.model.ImportIO;
import com.propertyvista.portal.domain.ptapp.LeaseTerms;
import com.propertyvista.server.common.reference.geo.SharedGeoLocator;
import com.propertyvista.server.domain.FileBlob;
import com.propertyvista.server.domain.ThumbnailBlob;

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
            return deleteAll(Complex.class, Building.class, AptUnit.class, AptUnitItem.class, Floorplan.class, Email.class, Phone.class, Amenity.class,
                    LeaseTerms.class, Vendor.class, Elevator.class, Boiler.class, Roof.class, Parking.class, ParkingSpot.class, LockerArea.class, Locker.class,
                    Media.class, ThumbnailBlob.class, FileBlob.class, Feature.class, PropertyManager.class, ServiceCatalog.class);
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

            ImportIO importIO = ImportUtils.parse(ImportIO.class, new InputSource(new StringReader(data)));
            ImportCounters counters = new ImportCounters();
            for (BuildingIO building : importIO.buildings()) {
                //counters.add(new BuildingImporter().persist(building, imagesBaseFolder, true));
                counters.add(new BuildingUpdater().updateData(building, mediaConfig));

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

}
