/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 6, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.PreloadConfig;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyReportDTO;

public class UnitVacancyReportDTOPreloader extends BaseVistaDataPreloader {

    protected UnitVacancyReportDTOPreloader(PreloadConfig config) {
        super(config);
    }

    @Override
    public String delete() {
        if (ApplicationMode.isDevelopment()) {
            return deleteAll(UnitVacancyReportDTO.class);
        } else {
            return "This is production";
        }
    }

    @Override
    public String create() {

        UnitVacancyReportDTO report = EntityFactory.create(UnitVacancyReportDTO.class);

        // TODO fill it here...

        Persistence.service().persist(report);

        StringBuilder sb = new StringBuilder();
//                sb.append("Created ").append(buildings.size()).append(" buildings, ").append(unitCount).append(" units");
        return sb.toString();
    }
}
