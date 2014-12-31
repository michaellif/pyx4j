/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 30, 2014
 * @author ernestog
 */
package com.propertyvista.biz.preloader;

import com.pyx4j.config.server.FacadeFactory;

import com.propertyvista.biz.preloader.pmc.PmcPreloaderFacadeImpl;

public class PmcPreloaderFacadeFactory implements FacadeFactory<PmcPreloaderFacade> {

    @Override
    public PmcPreloaderFacade getFacade() {
        return new PmcPreloaderFacadeImpl();
    }

}
