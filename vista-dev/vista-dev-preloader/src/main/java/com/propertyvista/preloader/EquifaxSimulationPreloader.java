/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 22, 2012
 * @author vlads
 */
package com.propertyvista.preloader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.biz.preloader.BaseVistaDevDataPreloader;
import com.propertyvista.operations.domain.dev.EquifaxSimulatorConfig;

public class EquifaxSimulationPreloader extends BaseVistaDevDataPreloader {

    @Override
    public String create() {
        EquifaxSimulatorConfig ec = EntityFactory.create(EquifaxSimulatorConfig.class);

        try {
            ec.approve().xml().setValue(IOUtils.getTextResource("simulation/equifax/approve.xml", StandardCharsets.UTF_8));
            ec.decline().xml().setValue(IOUtils.getTextResource("simulation/equifax/decline.xml", StandardCharsets.UTF_8));
            ec.moreInfo().xml().setValue(IOUtils.getTextResource("simulation/equifax/moreInfo.xml", StandardCharsets.UTF_8));

        } catch (IOException e) {
            throw new Error(e);
        }

        Persistence.service().persist(ec);

        return "Equifax Simulations preloaded";
    }

    @Override
    public String delete() {
        return null;
    }

}
