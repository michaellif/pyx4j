/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-11
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.config;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.domain.settings.PmcYardiCredential;

public class DevYardiCredentials {

    enum YardiCredential {
        localNew( //
                "gran0002, aven2175", //
                "http://yardi.birchwoodsoftwaregroup.com:8080/Voyager6008sp17", //
                "vista_dev", //
                "vista_dev", //
                "WIN-CO5DPAKNUA4\\YARDI", //
                "vista_dev" //
        ),

        localOld( //
                "gran0002, aven2175", //
                "http://yardi.birchwoodsoftwaregroup.com/Voyager60", //
                "vista_dev", //
                "vista_dev", //
                "WIN-CO5DPAKNUA4\\YARDI", //
                "vista_dev" //
        ),

        hostedNew(//
                "prvista2", //
                "https://www.iyardiasp.com/8223third_17", //
                "propertyvistadb", //
                "52673", //
                "aspdb04", //
                "afqoml_live" //
        );

        final String propertyListCodes;

        final String serviceURLBase;

        final String username;

        final String password;

        final String serverName;

        final String database;

        private YardiCredential( //
                String propertyListCodes, //
                String serviceURLBase, //
                String username, //
                String password, //
                String serverName, //
                String database //
        ) {
            this.propertyListCodes = propertyListCodes;
            this.serviceURLBase = serviceURLBase;
            this.username = username;
            this.password = password;
            this.serverName = serverName;
            this.database = database;
        }
    }

    public static PmcYardiCredential getTestPmcYardiCredential() {
        return getTestPmcYardiCredential(YardiCredential.localNew);
    }

    public static PmcYardiCredential getTestPmcYardiCredential(YardiCredential yc) {
        PmcYardiCredential cr = EntityFactory.create(PmcYardiCredential.class);
        // See http://jira.birchwoodsoftwaregroup.com/wiki/display/VISTA/Yardi
        if (yc != null) {
            cr.propertyListCodes().setValue(yc.propertyListCodes);
            cr.serviceURLBase().setValue(yc.serviceURLBase);
            cr.username().setValue(yc.username);
            cr.password().number().setValue(yc.password);
            cr.serverName().setValue(yc.serverName);
            cr.database().setValue(yc.database);
            cr.platform().setValue(PmcYardiCredential.Platform.SQL);
        }

        return cr;
    }

    public static List<PmcYardiCredential> getTestPmcYardiCredentialList() {
        List<PmcYardiCredential> ycList = new ArrayList<PmcYardiCredential>();
        for (YardiCredential yc : YardiCredential.values()) {
            PmcYardiCredential cr = getTestPmcYardiCredential(yc);
            // to go by wire
            cr.password().obfuscatedNumber().set(cr.password().number());
            ycList.add(cr);
        }
        return ycList;
    }
}
