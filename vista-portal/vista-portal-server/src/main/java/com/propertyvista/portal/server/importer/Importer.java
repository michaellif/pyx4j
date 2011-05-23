/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 20, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.portal.server.importer;

import java.io.IOException;
import java.text.ParseException;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.Utility;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.AptUnitOccupancy;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.IEntity;

public class Importer {
    private static final Logger log = LoggerFactory.getLogger(Importer.class);

    private Reader reader;

    private Mapper mapper;

    public Importer() {
    }

    public void read() throws IOException, JAXBException, ParseException {
        reader = new Reader();
        reader.readCsv();
        reader.readXml();
    }

    public void map() {
        // map
        mapper = new Mapper();
        mapper.load(reader.getResidential(), reader.getUnits());
    }

    public void save() {
        // save
        for (Building building : mapper.getBuildings()) {
            persist(building);
        }

        for (Floorplan floorplan : mapper.getFloorplans()) {
            persist(floorplan);
        }

        for (AptUnit unit : mapper.getUnits()) {
            for (AptUnitOccupancy occupancy : unit.currentOccupancies()) {
                persist(occupancy);
            }
            for (Utility utility : unit.info().utilities()) {
                persist(utility);
            }
            // for (AptUnitAmenity amenity : unit.amenities()) {
            // persist(amenity);
            // }
            // for (AptUnitItem detail : unit.info().details()) {
            // persist(detail);
            // }
            // for (AddOn addOn : unit.addOns()) {
            // persist(addOn);
            // }
            // for (Concession concession : unit.concessions()) {
            // persist(concession);
            // }
            persist(unit);
        }
    }

    public void start() throws Exception {
        read();
        map();
        save();
    }

    private static void persist(IEntity entity) {
        PersistenceServicesFactory.getPersistenceService().persist(entity);
    }

    public Mapper getMapper() {
        return mapper;
    }
}
