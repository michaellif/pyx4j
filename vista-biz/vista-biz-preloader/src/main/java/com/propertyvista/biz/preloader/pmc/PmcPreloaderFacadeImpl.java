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
package com.propertyvista.biz.preloader.pmc;

import com.propertyvista.biz.preloader.PmcPreloaderFacade;

public class PmcPreloaderFacadeImpl implements PmcPreloaderFacade {

    // TODO Create PmcPreloader Helper Obj with heavy functionality to invoke from this Facade Implementation

    @Override
    public void resetPmcTables(String pmc) {
        // TODO Auto-generated method stub
    }

    @Override
    public void preloadPmc(String pmc) {
        // TODO Auto-generated method stub
    }

    @Override
    public void resetAndPreload(String pmc) {
        resetPmcTables(pmc);
        preloadPmc(pmc);
    }

}
