/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 */
package com.propertyvista.ils.gottarent.mapper;

import java.math.BigInteger;
import java.util.List;

import com.gottarent.rs.Building;
import com.gottarent.rs.Company;
import com.gottarent.rs.Listing;
import com.gottarent.rs.ObjectFactory;
import com.gottarent.rs.Portfolio;

import com.pyx4j.entity.core.IList;

import com.propertyvista.ils.gottarent.mapper.dto.ILSBuildingDTO;
import com.propertyvista.ils.gottarent.mapper.dto.ILSReportDTO;

/**
 * The class responsible to convert ILS DTOs into gottarent DTOs
 * 
 * @author smolka
 * 
 */
public class GottarentDataMapper {
    private final ObjectFactory factory;

    public GottarentDataMapper(ObjectFactory newFactory) {
        factory = newFactory;
    }

    private Portfolio createPortfolio(IList<ILSBuildingDTO> vistaListing, Listing listing) {
        Portfolio portfolio = factory.createPortfolio();
        List<Building> buildings = portfolio.getBuilding();
        GottarentBuildingMapper buildingMapper = new GottarentBuildingMapper(factory);
        int totalBuildings = 0, totalUnits = 0;
        for (ILSBuildingDTO bldDto : vistaListing) {
            // TODO: Smolka : Uncomment it if needed
            //if (bldDto.profile().vendor().equals(ILSVendor.gottarent)) {
            Building building = buildingMapper.createBuilding(bldDto);
            if (building != null) {
                buildings.add(building);
                totalUnits += (building.getBuildingVacancies() == null || building.getBuildingVacancies().getBuildingVacancy() == null ? 0 : building
                        .getBuildingVacancies().getBuildingVacancy().size());
                totalBuildings++;
            }
            // }
        }

        listing.setNumProperties(new BigInteger(Integer.toString(totalBuildings)));
        listing.setNumUnits(new BigInteger(Integer.toString(totalUnits)));

        return portfolio;
    }

    private Company createCompany(IList<ILSBuildingDTO> vistaListing, Listing listing) {
        //TODO: Smolka, how to fill company properties
        Company company = factory.createCompany();
        company.setCompanyName("Property Vista");
        company.setCompanyWebsite("http://propertyvista.com");
        company.setCompanyEmail("ils-feed@propertyvista.com");

        company.setPortfolio(createPortfolio(vistaListing, listing));
        return company;
    }

    public Listing createListing(ILSReportDTO report) {
        Listing listing = factory.createListing();

        listing.setCompany(createCompany(report.buildings(), listing));

        return listing;
    }

}
