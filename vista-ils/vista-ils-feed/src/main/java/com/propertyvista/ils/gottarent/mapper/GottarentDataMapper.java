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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gottarent.rs.Building;
import com.gottarent.rs.Company;
import com.gottarent.rs.Listing;
import com.gottarent.rs.ObjectFactory;
import com.gottarent.rs.Portfolio;

import com.pyx4j.entity.shared.IList;

import com.propertyvista.ils.gottarent.mapper.dto.ILSBuildingDTO;
import com.propertyvista.ils.gottarent.mapper.dto.ILSReportDTO;

/**
 * The class responsible to convert ILS DTOs into gottarent DTOs
 * 
 * @author smolka
 * 
 */
public class GottarentDataMapper {
    private static Logger log = LoggerFactory.getLogger(GottarentDataMapper.class);

    private final ObjectFactory factory;

    public GottarentDataMapper(ObjectFactory newFactory) {
        factory = newFactory;
    }

    private Portfolio createPortfolio(IList<ILSBuildingDTO> vistaListing) {
        Portfolio portfolio = factory.createPortfolio();
        List<Building> buildings = portfolio.getBuilding();
        GottarentBuildingMapper buildingMapper = new GottarentBuildingMapper(factory);
        for (ILSBuildingDTO bldDto : vistaListing) {
            // TODO: Smolka : Uncomment it if needed
            //if (bldDto.profile().vendor().equals(ILSVendor.gottarent)) {
            buildings.add(buildingMapper.createBuilding(bldDto));
            //}
        }

        return portfolio;
    }

    private Company createCompany(IList<ILSBuildingDTO> vistaListing) {
        //TODO: Smolka, how to fill company properties
        Company company = factory.createCompany();
        company.setCompanyName("propertyvista");
        company.setCompanyWebsite("http://propertyvista.com");
        company.setCompanyEmail("a@b.com");

        company.setPortfolio(createPortfolio(vistaListing));
        return company;
    }

    public Listing createListing(ILSReportDTO report) {
        Listing listing = factory.createListing();

        listing.setCompany(createCompany(report.buildings()));
        listing.setNumProperties(new BigInteger(Integer.toString(report.buildings().size())));
        listing.setNumUnits(new BigInteger(Integer.toString(report.totalUnits().getValue())));

        return listing;
    }
}
