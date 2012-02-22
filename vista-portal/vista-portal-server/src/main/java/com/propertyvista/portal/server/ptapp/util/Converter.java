/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 19, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.portal.server.ptapp.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.propertvista.generator.util.CommonsGenerator;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion.Restriction;

import com.propertyvista.domain.RangeGroup;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.marketing.PublicVisibilityType;
import com.propertyvista.domain.media.Media;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.FloorplanAmenity;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.portal.domain.dto.AmenityDTO;
import com.propertyvista.portal.domain.dto.FloorplanDTO;
import com.propertyvista.portal.domain.dto.FloorplanPropertyDTO;
import com.propertyvista.portal.domain.dto.MediaDTO;
import com.propertyvista.portal.domain.dto.PropertyDTO;
import com.propertyvista.portal.rpc.portal.dto.MaintananceDTO;

public class Converter {

    public static FloorplanDTO convert(Floorplan from) {
        FloorplanDTO to = EntityFactory.create(FloorplanDTO.class);

        to.id().set(from.id());
        to.name().setValue(from.name().getValue());
        to.area().set(CommonsGenerator.createRange(1200d, 2600d));
        to.marketRent().set(CommonsGenerator.createRange(600d, 1600d));
        to.description().setValue(from.description().getValue());

        if (!from.media().isEmpty()) {
            for (Media media : from.media()) {
                if (media.isValueDetached()) {
                    Persistence.service().retrieve(media);
                }
                if (PublicVisibilityType.global.equals(media.visibility().getValue()) && Media.Type.file == (media.type().getValue())) {
                    to.mainMedia().setValue(media.getPrimaryKey());
                    break;
                }
            }
        }

        List<FloorplanAmenity> amenities = new ArrayList<FloorplanAmenity>();
        EntityQueryCriteria<FloorplanAmenity> floorplanAmenityCriteria = EntityQueryCriteria.create(FloorplanAmenity.class);
        floorplanAmenityCriteria.add(PropertyCriterion.eq(floorplanAmenityCriteria.proto().belongsTo(), from));
        amenities.addAll(Persistence.service().query(floorplanAmenityCriteria));

        for (FloorplanAmenity amenity : amenities) {
            AmenityDTO amntDTO = EntityFactory.create(AmenityDTO.class);
            amntDTO.name().setValue(amenity.getStringView());
            to.amenities().add(amntDTO);
        }

        return to;
    }

    public static void minMax(RangeGroup src, RangeGroup dst) {
        if (dst.max().isNull() || dst.max().getValue() < src.max().getValue()) {
            dst.max().setValue(src.max().getValue());
        }
        if (dst.min().isNull() || dst.min().getValue() > src.min().getValue()) {
            dst.min().setValue(src.min().getValue());
        }
    }

    public static PropertyDTO convert(Building from, List<Floorplan> floorplans) {
        PropertyDTO to = EntityFactory.create(PropertyDTO.class);
        to.id().set(from.id());
        to.propertyCode().set(from.propertyCode());
        to.address().street1().set(from.info().address().streetName());
        to.address().street2().set(from.info().address().streetNumber());

        to.address().city().set(from.info().address().city());
        to.address().province().set(from.info().address().province());
        to.address().country().set(from.info().address().country());
        to.address().postalCode().set(from.info().address().postalCode());

        to.description().setValue(from.marketing().description().getValue());

        to.location().setValue(from.info().address().location().getValue());

        BigDecimal minPrice = null, maxPrice = null;
        // List of Floorplans
        for (Floorplan fp : floorplans) {
            FloorplanPropertyDTO fpto = EntityFactory.create(FloorplanPropertyDTO.class);
            fpto.name().set(fp.marketingName());
            fpto.bedrooms().set(fp.bedrooms());
            fpto.bathrooms().set(fp.bathrooms());
            fpto.price().set(CommonsGenerator.createRange(600d, 1600d));
            to.floorplansProperty().add(fpto);

            // list floorplan units
            EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().belongsTo(), fp.building().getPrimaryKey().asLong()));
            criteria.add(PropertyCriterion.eq(criteria.proto().floorplan(), fp));
            for (AptUnit unit : Persistence.service().query(criteria)) {
                BigDecimal _prc = unit.financial()._marketRent().getValue();
                if (minPrice == null || minPrice.compareTo(_prc) > 0) {
                    minPrice = _prc;
                }
                if (maxPrice == null || maxPrice.compareTo(_prc) < 0) {
                    maxPrice = _prc;
                }
            }
        }

        //TODO should be converted to BigDecimal
        to.price().min().setValue(minPrice.doubleValue());
        to.price().max().setValue(maxPrice.doubleValue());

        if (!from.media().isEmpty()) {
            for (Media media : from.media()) {
                if (media.isValueDetached()) {
                    Persistence.service().retrieve(media);
                }
                if (PublicVisibilityType.global.equals(media.visibility().getValue()) && Media.Type.file == (media.type().getValue())) {
                    to.mainMedia().setValue(media.getPrimaryKey());
                    break;
                }
            }
        }

        // List of amenities
        EntityQueryCriteria<BuildingAmenity> amenitysCriteria = EntityQueryCriteria.create(BuildingAmenity.class);
        amenitysCriteria.add(new PropertyCriterion(EntityFactory.getEntityPrototype(BuildingAmenity.class).belongsTo().getPath().toString(), Restriction.EQUAL,
                from));
        for (BuildingAmenity amenity : Persistence.service().query(amenitysCriteria)) {
            AmenityDTO amntDTO = EntityFactory.create(AmenityDTO.class);
            amntDTO.name().setValue(amenity.getStringView());
            to.amenities().add(amntDTO);
        }

        return to;
    }

    public static MediaDTO convert(Media from) {
        MediaDTO to = EntityFactory.create(MediaDTO.class);
        to.id().set(from.id());
        to.caption().setValue(from.caption().getValue());
        return to;
    }

    public static MaintananceDTO convert(MaintenanceRequest from) {
        MaintananceDTO to = EntityFactory.create(MaintananceDTO.class);
        to.id().set(from.id());
        to.description().set(from.description());
        to.date().set(from.submitted());
        to.status().set(from.status());
        to.satisfactionSurvey().set(from.surveyResponse());
        return to;
    }
}
