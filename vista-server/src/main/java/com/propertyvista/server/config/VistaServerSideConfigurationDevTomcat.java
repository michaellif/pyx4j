/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-25
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.config;

public class VistaServerSideConfigurationDevTomcat extends VistaServerSideConfigurationDev {

    @Override
    public boolean openIdrequired() {
        return true;
    }

    @Override
    public String getApplicationURLDefault() {
        return "http://localhost:9000/vista/";
    }

    @Override
    public String getApplicationURLNamespace() {
        return ".dev.birchwoodsoftwaregroup.com:9000/vista/";
    }
}
