package com.propertyvista.ils.kijiji.rs;

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

public class XMLManager {

    private final ObjectFactory factory;

    public XMLManager(ObjectFactory newFactory) {
        factory = newFactory;
    }

    private ILSUnit createUnit() {
        ILSUnit unit = factory.createILSUnit();
        unit.setRentOrSale("rent");
        unit.setOfferedBy(OfferedByEnum.OWNER);
        unit.setTitle("2 Bedroom condo by the lake");
        unit.setBedrooms(BedroomsEnum.None);
        unit.setBathrooms(BathroomsEnum.Six_More);
        unit.setPrice("1134.00");
        unit.setSquareFootage(864);
        unit.setFurnished(IsFurnished.YES);
        unit.setPetsAllowed(IsPetsAllowed.NO);
        unit.setImages(createImages());
        return unit;
    }

    private Image createImage() {
        Image image = factory.createILSUnitImagesImage();
        image.setName("Building Profile Image");
        image.setSourceUrl("http://example.com/image.jpg");
        image.setClientImageId("clientImage1");
        return image;
    }

    private Images createImages() {
        Images images = factory.createILSUnitImages();
        images.setImage(createImage());
        return images;
    }

    private ILSUnits createUnits() {
        ILSUnits units = factory.createILSUnits();
        units.getUnit().add(createUnit());
        return units;
    }

    private ILSLogo createLogo() {
        ILSLogo logo = factory.createILSLogo();
        logo.setSmall("http://example.com/dealerlogo/small.png");
        logo.setMedium("http://example.com/dealerlogo/medium.png");
        logo.setLarge("http://example.com/dealerlogo/large.png");
        return logo;
    }

    private ILSLocation createLocation() {
        ILSLocation location = factory.createILSLocation();
        location.setClientLocationId(100);
        location.setBuildingName("Meadow Park Estates");
        location.setStreetAddress("100 Gladmer Park");
        location.setCity("Saskatoon");
        location.setProvince("Saskatchewan");
        location.setPostalCode("S7J 2X3");
        location.setEmail("test@ebay.com");
        location.setPhoneNumber("(306) 343-3905");
        location.setWebSite("http://www.example.com/ebay/test/website");
        location.setLogo(createLogo());
        location.getUnits().add(createUnits());
        return location;
    }

    private ILSLocations createLocations() {
        ILSLocations locations = factory.createILSLocations();
        locations.getLocation().add(createLocation());
        return locations;
    }

    public ILSLocations createRentXML() {
        ILSLocations locations = createLocations();
        return locations;
    }
}
