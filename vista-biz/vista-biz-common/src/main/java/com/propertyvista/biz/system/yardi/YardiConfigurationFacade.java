/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 7, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.biz.system.yardi;

import java.util.List;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.settings.PmcYardiCredential;

public interface YardiConfigurationFacade {

    /**
     * Initialize ThreadLocal cache to save time on {@link PmcYardiCredential#password()} decryption
     * when processing multi-transaction batches
     */
    void initYardiCredentialCache();

    /** Remove ThreadLocal cache */
    void clearYardiCredentialCache();

    /**
     * Retrieve Yardi credentials from cache if initialized. Otherwise do full retrieve and password decryption.
     */
    PmcYardiCredential getYardiCredential(Building building);

    List<PmcYardiCredential> getYardiCredentials();

    /** Initialize ThreadLocal timer to count Yardi execution time */
    void startYardiTimer();

    /** advance Yardi execution time */
    void incrementYardiTimer(long interval);

    /** read Yardi execution time and remove the timer */
    long stopYardiTimer();
}
