/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 7, 2011
 * @author vlads
 */
package com.propertyvista.biz.preloader;

import java.io.Serializable;

import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.essentials.server.preloader.DataGenerator;

import com.propertyvista.misc.VistaDataPreloaderParameter;
import com.propertyvista.misc.VistaDevPreloadConfig;

public abstract class BaseVistaDevDataPreloader extends AbstractDataPreloader {

    protected BaseVistaDevDataPreloader() {
        DataGenerator.setRandomSeed(100);
    }

    protected VistaDevPreloadConfig config() {
        return (VistaDevPreloadConfig) getParameter(VistaDataPreloaderParameter.devPreloadConfig);
    }

    protected Serializable getParameter(VistaDataPreloaderParameter param) {
        return super.getParameter(param.name());
    }
}
