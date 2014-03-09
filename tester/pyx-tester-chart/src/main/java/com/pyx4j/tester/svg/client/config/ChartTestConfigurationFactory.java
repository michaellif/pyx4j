/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Mar 9, 2014
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.tester.svg.client.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.rpc.SerializationException;

import com.pyx4j.entity.core.EntityFactory;

public class ChartTestConfigurationFactory {

    private static final Logger log = LoggerFactory.getLogger(ChartTestConfigurationFactory.class);

    public static ChartTestConfiguration getChartTestConfiguration() {
        if (Storage.isSupported()) {
            try {
                String payload = Storage.getLocalStorageIfSupported().getItem("chartTestConfiguration");
                if (payload != null) {
                    return EntitySerialization.deserialize(payload);
                }
            } catch (SerializationException e) {
                log.error("deserialize error", e);
            }
        }

        return defaultConfigChartTestConfiguration();
    }

    private static ChartTestConfiguration defaultConfigChartTestConfiguration() {
        ChartXYTestConfiguration defaultConfig = EntityFactory.create(ChartXYTestConfiguration.class);
        defaultConfig.chartType().setValue(ChartXYTestConfiguration.ChartType.Line);
        defaultConfig.pointsType().setValue(ChartXYTestConfiguration.PointsType.None);
        defaultConfig.points().setValue(24);

        defaultConfig.xValuesType().setValue(ChartXYTestConfiguration.ValuesType.Numbers);
        defaultConfig.xFrom().setValue(0.0);
        defaultConfig.xTo().setValue(100.0);

        defaultConfig.yValuesType().setValue(ChartXYTestConfiguration.ValuesType.Numbers);
        defaultConfig.yFrom().setValue(0.0);
        defaultConfig.yTo().setValue(100.0);

        return defaultConfig;
    }

    public static void save(ChartTestConfiguration testConfiguration) {
        if (Storage.isSupported()) {
            try {
                Storage.getLocalStorageIfSupported().setItem("chartTestConfiguration", EntitySerialization.serialize(testConfiguration));
            } catch (SerializationException e) {
                log.error("serialize error", e);
            }
        }
    }

}
