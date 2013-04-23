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

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.settings.PmcYardiCredential;

public class DevYardiCredentials {

    public static PmcYardiCredential getTestPmcYardiCredential() {
        PmcYardiCredential cr = EntityFactory.create(PmcYardiCredential.class);
        // See http://jira.birchwoodsoftwaregroup.com/wiki/display/VISTA/Yardi
        if (true) {
            cr.propertyCode().setValue(".pvberk");
            cr.serviceURLBase().setValue("http://yardi.birchwoodsoftwaregroup.com/Voyager60");
            cr.username().setValue("gran0002");
            cr.credential().setValue("akan1212");
            cr.serverName().setValue("WIN-CO5DPAKNUA4\\YARDI");
            cr.database().setValue("sl_0404");
        } else if (false) {
            cr.propertyCode().setValue("prvista2");
            cr.serviceURLBase().setValue("https://www.iyardiasp.com/8223thirddev");
            cr.username().setValue("propertyvistaws");
            cr.credential().setValue("52673");
            cr.serverName().setValue("aspdb04");
            cr.database().setValue("afqoml_live");
        } else if (false) {
            cr.serviceURLBase().setValue("https://www.iyardiasp.com/8223thirdqa");
            cr.username().setValue("propertyvista");
            cr.credential().setValue("52673");
            cr.serverName().setValue("aspdb06\\sql2k5");
            cr.database().setValue("afqoml_qa6008");
        } else if (false) {
            cr.propertyCode().setValue(".pvberk");
            cr.serviceURLBase().setValue("https://yardi.starlightinvest.com/voyager6008sp17");
            cr.username().setValue("propvist");
            cr.credential().setValue("access@123");
            cr.serverName().setValue("SLDB02");
            cr.database().setValue("PropertyVista_TEST");
        } else if (false) {
            cr.propertyCode().setValue("prvista1");
            cr.serviceURLBase().setValue("https://www.iyardiasp.com/8223thirddev");
            cr.username().setValue("propertyvista-srws");
            cr.credential().setValue("55548");
            cr.serverName().setValue("aspdb04");
            cr.database().setValue("afqoml_live");
        }
        cr.platform().setValue(PmcYardiCredential.Platform.SQL);

        return cr;
    }
}
