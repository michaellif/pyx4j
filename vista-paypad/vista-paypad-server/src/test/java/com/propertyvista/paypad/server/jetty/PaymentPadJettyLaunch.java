/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-04-14
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.paypad.server.jetty;

import com.pyx4j.jetty.JettyLaunch;

public class PaymentPadJettyLaunch extends JettyLaunch {

    public static void main(String[] args) throws Exception {
        JettyLaunch.launch(new PaymentPadJettyLaunch());
    }

    @Override
    public int getServerPort() {
        return 8889;
    }

    @Override
    public String getWarResourceBase() {
        return "src/main/webapp";
    }

    @Override
    public String getContextPath() {
        return "/";
    }

}
