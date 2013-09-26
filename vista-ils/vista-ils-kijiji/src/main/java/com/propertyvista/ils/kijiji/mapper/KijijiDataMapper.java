/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 09, 2013
 * @author Anatoly
 */
package com.propertyvista.ils.kijiji.mapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.kijiji.pint.rs.ILSLocation;
import com.kijiji.pint.rs.ILSLocations;
import com.kijiji.pint.rs.ILSLogo;
import com.kijiji.pint.rs.ILSUnit;
import com.kijiji.pint.rs.ILSUnit.Images;
import com.kijiji.pint.rs.ILSUnit.Images.Image;
import com.kijiji.pint.rs.ILSUnits;
import com.kijiji.pint.rs.ObjectFactory;

import com.propertyvista.domain.File;
import com.propertyvista.domain.media.Media;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.site.PortalLogoImageResource;

public class KijijiDataMapper {

    private final ObjectFactory factory;

    public KijijiDataMapper(ObjectFactory newFactory) {
        factory = newFactory;
    }

    private ILSUnit createUnit(Floorplan unit) {
        ILSUnit ilsUnit = factory.createILSUnit();
        // map unit data
        new KijijiUnitMapper().convert(unit, ilsUnit);
        // add media urls
        ilsUnit.setImages(createImages(unit.media()));
        return ilsUnit;
    }

    private Image createImage(String url) {
        Image image = factory.createILSUnitImagesImage();
        image.setName("Building Profile Image");
        image.setSourceUrl("http://example.com/image.jpg");
        image.setClientImageId("clientImage1");
        return image;
    }

    private Image createImage(File file) {
        // TODO - generate image url from file and call createImage(String url)
        return null;
    }

    private Images createImages(List<Media> media) {
        Images images = factory.createILSUnitImages();
        for (Media item : media) {
            switch (item.type().getValue()) {
            case externalUrl:
                images.setImage(createImage(item.url().getValue()));
                break;
            case file:
                images.setImage(createImage(item.file()));
                break;
            default:
            }
        }
        return images;
    }

    private ILSUnits createUnits(Collection<Floorplan> units) {
        ILSUnits ilsUnits = factory.createILSUnits();
        for (Floorplan unit : units) {
            ilsUnits.getUnit().add(createUnit(unit));
        }
        return ilsUnits;
    }

    private ILSLogo createLogo() {
        ILSLogo logo = factory.createILSLogo();
        PortalLogoImageResource siteLogo = KijijiMapperUtils.getSiteLogo();
        logo.setSmall(KijijiMapperUtils.getSiteImageResourceUrl(siteLogo.small()));
        logo.setLarge(KijijiMapperUtils.getSiteImageResourceUrl(siteLogo.large()));
        return logo;
    }

    private ILSLocation createLocation(Building building, Collection<Floorplan> units) {
        ILSLocation location = factory.createILSLocation();
        // map building data
        new KijijiLocationMapper().convert(building, location);
        // logo
        location.setLogo(createLogo());
        // add units
        location.getUnits().add(createUnits(units));

        return location;
    }

    public ILSLocations createLocations(Map<Building, List<Floorplan>> units) {
        ILSLocations locations = factory.createILSLocations();
        for (Building building : units.keySet()) {
            locations.getLocation().add(createLocation(building, units.get(building)));
        }
        return locations;
    }
}
