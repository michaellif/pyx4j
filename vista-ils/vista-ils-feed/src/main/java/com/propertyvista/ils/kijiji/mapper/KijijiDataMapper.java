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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kijiji.pint.rs.ILSLocation;
import com.kijiji.pint.rs.ILSLocations;
import com.kijiji.pint.rs.ILSLogo;
import com.kijiji.pint.rs.ILSUnit;
import com.kijiji.pint.rs.ILSUnit.Images;
import com.kijiji.pint.rs.ILSUnit.Images.Image;
import com.kijiji.pint.rs.ILSUnits;
import com.kijiji.pint.rs.ObjectFactory;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;

import com.propertyvista.domain.MediaFile;
import com.propertyvista.domain.PublicVisibilityType;
import com.propertyvista.domain.media.ThumbnailSize;
import com.propertyvista.domain.site.PortalLogoImageResource;
import com.propertyvista.ils.kijiji.mapper.dto.ILSBuildingDTO;
import com.propertyvista.ils.kijiji.mapper.dto.ILSFloorplanDTO;

public class KijijiDataMapper {
    private static Logger log = LoggerFactory.getLogger(KijijiDataMapper.class);

    private final ObjectFactory factory;

    public KijijiDataMapper(ObjectFactory newFactory) {
        factory = newFactory;
    }

    private ILSUnit createUnit(ILSFloorplanDTO fpDto) {
        ILSUnit ilsUnit = factory.createILSUnit();
        // map unit data
        new KijijiUnitMapper().convert(fpDto, ilsUnit);
        // add media urls
        Persistence.ensureRetrieve(fpDto.floorplan().media(), AttachLevel.Attached);
        ilsUnit.setImages(createImages(fpDto.floorplan().media()));

        return ilsUnit;
    }

    private Image createImage(MediaFile media) {
        Image image = factory.createILSUnitImagesImage();
        image.setSourceUrl(KijijiMapperUtils.getMediaImgUrl(media.getPrimaryKey().asLong(), ThumbnailSize.large));
        image.setClientImageId(media.getPrimaryKey().toString());
        image.setName(media.caption().getValue());

        return image;
    }

    private Images createImages(List<MediaFile> media) {
        Images images = factory.createILSUnitImages();
        for (MediaFile item : media) {
            if (PublicVisibilityType.global.equals(item.visibility().getValue())) {
                Image image = createImage(item);
                if (image != null) {
                    images.getImage().add(image);
                }
            }
        }

        return images;
    }

    private ILSUnits createUnits(Collection<ILSFloorplanDTO> fpList) {
        ILSUnits ilsUnits = factory.createILSUnits();
        for (ILSFloorplanDTO fpDto : fpList) {
            ilsUnits.getUnit().add(createUnit(fpDto));
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

    private ILSLocation createLocation(ILSBuildingDTO bldDto, Collection<ILSFloorplanDTO> fpList) {
        ILSLocation location = factory.createILSLocation();
        // map building data
        new KijijiLocationMapper().convert(bldDto, location);
        // logo
        location.setLogo(createLogo());
        // add units
        location.getUnits().add(createUnits(fpList));

        return location;
    }

    public ILSLocations createLocations(Map<ILSBuildingDTO, List<ILSFloorplanDTO>> listing) {
        ILSLocations locations = factory.createILSLocations();
        for (ILSBuildingDTO bldDto : listing.keySet()) {
            locations.getLocation().add(createLocation(bldDto, listing.get(bldDto)));
        }

        return locations;
    }
}
