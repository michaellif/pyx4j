/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 25, 2014
 * @author stanp
 */
package com.propertyvista.yardi.mock.model.manager.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.propertyvista.yardi.YardiInterface;
import com.propertyvista.yardi.mock.model.YardiMock;
import com.propertyvista.yardi.mock.model.domain.YardiInterfaceConfig;
import com.propertyvista.yardi.mock.model.manager.YardiConfigurationManager;

public class YardiConfigurationManagerImpl implements YardiConfigurationManager {

    @Override
    public void addProperty(Class<? extends YardiInterface> service, String propertyCode) {
        Collection<String> codes = toList(getConfig(service).properties().getValue());
        codes.add(propertyCode);
        getConfig(service).properties().setValue(fromList(codes));
    }

    @Override
    public void addChargeCode(Class<? extends YardiInterface> service, String chargeCode) {
        Collection<String> codes = toList(getConfig(service).chargeCodes().getValue());
        codes.add(chargeCode);
        getConfig(service).chargeCodes().setValue(fromList(codes));
    }

    @Override
    public Collection<String> getProperties(Class<? extends YardiInterface> service) {
        return toList(getConfig(service).properties().getValue());
    }

    @Override
    public Collection<String> getChargeCodes(Class<? extends YardiInterface> service) {
        return toList(getConfig(service).chargeCodes().getValue());
    }

    private YardiInterfaceConfig getConfig(Class<? extends YardiInterface> service) {
        return YardiMock.server().getModel().getInterfaceConfig(service);
    }

    private Set<String> toList(String csList) {
        Set<String> result = new HashSet<>();
        if (csList != null) {
            for (String item : csList.split(",")) {
                result.add(item.trim());
            }
        }
        return result;
    }

    private String fromList(Collection<String> list) {
        StringBuilder result = new StringBuilder();
        if (list != null) {
            for (String item : list) {
                result.append(result.length() > 0 ? "," : "").append(item);
            }
        }
        return result.toString();
    }
}
