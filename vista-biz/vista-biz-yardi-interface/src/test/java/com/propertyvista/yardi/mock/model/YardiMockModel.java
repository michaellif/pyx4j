/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 16, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.yardi.mock.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.propertyvista.yardi.YardiInterface;
import com.propertyvista.yardi.mock.model.domain.YardiBuilding;
import com.propertyvista.yardi.mock.model.domain.YardiInterfaceConfig;

public class YardiMockModel {

    private final List<YardiBuilding> buildings;

    private final Map<Class<? extends YardiInterface>, YardiInterfaceConfig> ifConfig;

    public YardiMockModel() {
        buildings = new ArrayList<>();
        ifConfig = new HashMap<>();
    }

    public List<YardiBuilding> getBuildings() {
        return buildings;
    }

    public YardiInterfaceConfig getInterfaceConfig(Class<? extends YardiInterface> ifClass) {
        return ifConfig.get(ifClass);
    }
}
