/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 22, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.config;

public class VistaServerSideConfiguration66 extends VistaServerSideConfigurationCustom {

    @Override
    public Integer enviromentId() {
        return 66;
    }

    @Override
    protected String getApplicationDeploymentProtocol() {
        return "https";
    }

    @Override
    public int interfaceSSHDPort() {
        return 8826;
    }

    @Override
    public String getApplicationURLNamespace(boolean secure) {
        return "-66.birchwoodsoftwaregroup.com/";
    }

    @Override
    public String getApplicationEmailSender() {
        return "\"Property Vista Support66\" <support.www22@birchwoodsoftwaregroup.com>";
    }

}
