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

import java.util.List;
import java.util.Map;

import com.kijiji.pint.rs.ILSLocation;
import com.kijiji.pint.rs.ILSLocations;
import com.kijiji.pint.rs.ILSLogo;
import com.kijiji.pint.rs.ILSUnit;
import com.kijiji.pint.rs.ILSUnit.BathroomsEnum;
import com.kijiji.pint.rs.ILSUnit.BedroomsEnum;
import com.kijiji.pint.rs.ILSUnit.Images;
import com.kijiji.pint.rs.ILSUnit.Images.Image;
import com.kijiji.pint.rs.ILSUnit.IsFurnished;
import com.kijiji.pint.rs.ILSUnit.IsPetsAllowed;
import com.kijiji.pint.rs.ILSUnit.OfferedByEnum;
import com.kijiji.pint.rs.ILSUnits;
import com.kijiji.pint.rs.ObjectFactory;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;

import com.propertyvista.domain.File;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.marketing.Marketing;
import com.propertyvista.domain.media.Media;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;

public class KijijiDataMapper {

    private final ObjectFactory factory;

    public KijijiDataMapper(ObjectFactory newFactory) {
        factory = newFactory;
    }

    private ILSUnit createUnit(AptUnit unit) {
        ILSUnit ilsUnit = factory.createILSUnit();
        ilsUnit.setRentOrSale("rent");
        ilsUnit.setOfferedBy(OfferedByEnum.OWNER);
        ilsUnit.setTitle("2 Bedroom condo by the lake");
        ilsUnit.setBedrooms(BedroomsEnum.None);
        ilsUnit.setBathrooms(BathroomsEnum.Six_More);
        ilsUnit.setPrice("1134.00");
        ilsUnit.setSquareFootage(864);
        ilsUnit.setFurnished(IsFurnished.YES);
        ilsUnit.setPetsAllowed(IsPetsAllowed.NO);
        ilsUnit.setImages(createImages(unit.floorplan().media()));
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

    private ILSUnits createUnits(List<AptUnit> units) {
        ILSUnits ilsUnits = factory.createILSUnits();
        for (AptUnit unit : units) {
            ilsUnits.getUnit().add(createUnit(unit));
        }
        return ilsUnits;
    }

    private ILSLogo createLogo() {
        ILSLogo logo = factory.createILSLogo();
        // TODO - generate logo url
        logo.setSmall("http://example.com/dealerlogo/small.png");
        logo.setMedium("http://example.com/dealerlogo/medium.png");
        logo.setLarge("http://example.com/dealerlogo/large.png");
        return logo;
    }

    private ILSLocation createLocation(Building building, List<AptUnit> units) {
        Persistence.ensureRetrieve(building, AttachLevel.Attached);
        Marketing info = building.marketing();
        ILSLocation location = factory.createILSLocation();
        location.setClientLocationId((int) building.getPrimaryKey().asLong());
        location.setBuildingName(info.name().getStringView());
        location.setStreetAddress(formatStreetAddress(info.marketingAddress()));
        location.setCity(info.marketingAddress().city().getStringView());
        location.setProvince(info.marketingAddress().province().getStringView());
        location.setPostalCode(info.marketingAddress().postalCode().getStringView());
        location.setEmail(info.marketingContacts().email().value().getStringView());
        location.setPhoneNumber(info.marketingContacts().phone().value().getStringView());
        location.setWebSite(info.marketingContacts().url().value().getStringView());
        location.setLogo(createLogo());
        location.getUnits().add(createUnits(units));
        return location;
    }

    private String formatStreetAddress(AddressStructured address) {
        Object[] args = new Object[] {
                // @formatter:off
                address.suiteNumber().getValue(),
                address.streetNumber().getValue(),
                address.streetNumberSuffix().getValue(),
                address.streetName().getValue(),
                address.streetType().getValue(),
                address.streetDirection().getValue()
        }; // @formatter:on
        return SimpleMessageFormat.format("{0,choice,null#|!null#{0}-}{1} {2} {3}{4,choice,null#|!null# {4}}{5,choice,null#|!null# {5}}", args);
    }

    public ILSLocations createLocations(Map<Building, List<AptUnit>> units) {
        ILSLocations locations = factory.createILSLocations();
        for (Building building : units.keySet()) {
            locations.getLocation().add(createLocation(building, units.get(building)));
        }
        return locations;
    }
}
