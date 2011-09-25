/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Sep 24, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.admin;

import java.io.File;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;

import com.pyx4j.commons.Consts;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.rpc.SystemState;
import com.pyx4j.essentials.rpc.admin.SystemMaintenanceState;
import com.pyx4j.essentials.server.xml.XMLEntityConverter;
import com.pyx4j.i18n.shared.I18nFactory;
import com.pyx4j.log4j.LoggerConfig;

public class SystemMaintenance {

    private final static Logger log = LoggerFactory.getLogger(SystemMaintenance.class);

    private static I18n i18n = I18nFactory.getI18n();

    private static final String STATE_FILE_NAME = "systemMaintenance.state.xml";

    private static int gracePeriodMin = 5;

    private static long maintenanceScheduled = 0;

    private static long maintenanceStarted = 0;

    private static long maintenanceScheduledEnd;

    private static SystemMaintenanceState systemMaintenanceState = EntityFactory.create(SystemMaintenanceState.class);

    static {
        loadState();
    }

    public static boolean isSystemMaintenance() {
        if (maintenanceStarted != 0) {
            if (maintenanceScheduledEnd <= System.currentTimeMillis()) {
                stopSystemMaintenance();
                return false;
            } else {
                return true;
            }
        } else if (maintenanceScheduled != 0) {
            if (maintenanceScheduled <= System.currentTimeMillis()) {
                maintenanceStarted = System.currentTimeMillis() + Consts.MIN2MSEC * gracePeriodMin;
                return true;
            }
        }
        return false;
    }

    public static SystemState getState() {
        if (isSystemMaintenance()) {
            return systemMaintenanceState.type().getValue();
        } else {
            return null;
        }
    }

    public static long getSystemMaintenanceDellay() {
        return maintenanceStarted - System.currentTimeMillis();
    }

    public static void startSystemMaintenance(int gracePeriodMin) {
        maintenanceStarted = System.currentTimeMillis() + Consts.MIN2MSEC * gracePeriodMin;
        maintenanceScheduledEnd = System.currentTimeMillis() + Consts.MIN2MSEC * (gracePeriodMin + 60);
        systemMaintenanceState = EntityFactory.create(SystemMaintenanceState.class);
        systemMaintenanceState.gracePeriod().setValue(Consts.MIN2MSEC * gracePeriodMin);
    }

    public static void stopSystemMaintenance() {
        maintenanceScheduled = 0;
        maintenanceStarted = 0;
    }

    public static SystemMaintenanceState getSystemMaintenanceClientInfo() {
        SystemMaintenanceState info = EntityFactory.create(SystemMaintenanceState.class);
        info.inEffect().setValue(isSystemMaintenance());
        info.message().setValue(getApplicationMaintenanceMessage());
        info.gracePeriod().setValue(getSystemMaintenanceDellay());
        info.duration().setValue((int) ((maintenanceScheduledEnd - System.currentTimeMillis()) / Consts.MIN2MSEC));
        return info;
    }

    public static String getApplicationMaintenanceMessage() {
        if (!systemMaintenanceState.message().isNull()) {
            return systemMaintenanceState.message().getStringView();
        } else {
            switch (systemMaintenanceState.type().getValue()) {
            case ReadOnly:
                //TODO calculate time period
                return i18n.tr("Application is in read-only due to short maintenance.\nPlease try again in one hour");
            default:
                return i18n.tr("Application is Unavailable due to short maintenance.\nPlease try again in one hour");
            }
        }
    }

    public static SystemMaintenanceState getSystemMaintenanceInfo() {
        systemMaintenanceState.inEffect().setValue(isSystemMaintenance());
        return systemMaintenanceState;
    }

    public static void setSystemMaintenanceInfo(SystemMaintenanceState state) {
        stopSystemMaintenance();
        if (!state.startTime().isNull()) {
            systemMaintenanceState = state;
            if (state.startTime().getValue().after(new Date())) {
                maintenanceScheduled = state.startTime().getValue().getTime() - state.gracePeriod().getValue();
                maintenanceScheduledEnd = state.startTime().getValue().getTime() + Consts.MIN2MSEC * state.duration().getValue();
                if (systemMaintenanceState.type().isNull()) {
                    systemMaintenanceState.type().setValue(SystemState.ReadOnly);
                }
                log.info("maintenanceScheduled {}", state.startTime());
            }
        }
        saveState();
    }

    private static File getStorageFile() {
        if (LoggerConfig.getContextName() != null) {
            return new File(LoggerConfig.getContextName() + "-" + STATE_FILE_NAME);
        } else {
            return new File(STATE_FILE_NAME);
        }
    }

    private static void loadState() {
        try {
            File file = getStorageFile();
            if (file.canRead()) {
                SystemMaintenanceState sm = XMLEntityConverter.readFile(SystemMaintenanceState.class, file);
                if (sm != null) {
                    setSystemMaintenanceInfo(sm);
                }
            }
        } catch (Throwable e) {
            log.error("system error", e);
        }
        if (systemMaintenanceState.type().isNull()) {
            systemMaintenanceState.type().setValue(SystemState.Online);
        }
    }

    private static void saveState() {
        try {
            XMLEntityConverter.writeFile(systemMaintenanceState, getStorageFile(), true);
        } catch (Throwable e) {
            log.error("system error", e);
        }
    }
}
