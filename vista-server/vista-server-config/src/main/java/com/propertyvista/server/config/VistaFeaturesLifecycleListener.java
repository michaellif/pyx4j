/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-07-06
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.config;

import com.pyx4j.config.server.LifecycleListener;

public class VistaFeaturesLifecycleListener implements LifecycleListener {

    @Override
    public void onRequestBegin() {
        VistaFeatures.removeThreadLocale();
    }

    @Override
    public void onRequestEnd() {
    }

    @Override
    public void onRequestError() {
    }

    @Override
    public void onContextEnd() {
        VistaFeatures.removeThreadLocale();
    }

}
