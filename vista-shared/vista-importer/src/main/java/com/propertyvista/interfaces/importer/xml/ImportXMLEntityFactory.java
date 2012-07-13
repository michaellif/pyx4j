/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 17, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer.xml;

import com.pyx4j.essentials.server.xml.XMLEntityFactoryStrict;

import com.propertyvista.domain.property.PropertyPhone;
import com.propertyvista.domain.property.asset.Boiler;
import com.propertyvista.domain.property.asset.Elevator;
import com.propertyvista.domain.property.asset.LockerArea;
import com.propertyvista.domain.property.asset.Roof;
import com.propertyvista.domain.property.vendor.WarrantyItem;
import com.propertyvista.interfaces.importer.model.AddressIO;
import com.propertyvista.interfaces.importer.model.AdvertisingBlurbIO;
import com.propertyvista.interfaces.importer.model.AptUnitIO;
import com.propertyvista.interfaces.importer.model.AptUnitOccupancyIO;
import com.propertyvista.interfaces.importer.model.BuildingAmenityIO;
import com.propertyvista.interfaces.importer.model.BuildingIO;
import com.propertyvista.interfaces.importer.model.ContactIO;
import com.propertyvista.interfaces.importer.model.FloorplanAmenityIO;
import com.propertyvista.interfaces.importer.model.FloorplanIO;
import com.propertyvista.interfaces.importer.model.ImportIO;
import com.propertyvista.interfaces.importer.model.MarketingIO;
import com.propertyvista.interfaces.importer.model.MediaIO;
import com.propertyvista.interfaces.importer.model.ParkingIO;
import com.propertyvista.interfaces.importer.model.UtilityIO;

public class ImportXMLEntityFactory extends XMLEntityFactoryStrict {

    public ImportXMLEntityFactory() {
        super(new ImportXMLEntityNamingConvention());
    }

    @Override
    protected void bind() {
        bind(ImportIO.class);
        bind(AddressIO.class);
        bind(AdvertisingBlurbIO.class);
        bind(BuildingAmenityIO.class);
        bind(FloorplanAmenityIO.class);
        bind(AptUnitIO.class);
        bind(BuildingIO.class);
        bind(ContactIO.class);
        bind(FloorplanIO.class);
        bind(MarketingIO.class);
        bind(MediaIO.class);
        bind(ParkingIO.class);
        bind(UtilityIO.class);
        bind(AptUnitOccupancyIO.class);

        // Taken from domain directly
        bind(PropertyPhone.class);
        bind(Elevator.class);
        bind(Boiler.class);
        bind(Roof.class);
        bind(LockerArea.class);
        bind(WarrantyItem.class);
    }
}
