/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 4, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.yardi.mock;

/**
 * Yardi errors simulations model for building.
 * Should be POJO.
 */
public class YardiMockPropertyFeatures {

    private boolean blockAccess;

    private boolean blockBatchOpening;

    public boolean isBlockAccess() {
        return blockAccess;
    }

    public void setBlockAccess(Boolean blockAccess) {
        this.blockAccess = blockAccess;
    }

    public boolean isBlockBatchOpening() {
        return blockBatchOpening;
    }

    public void setBlockBatchOpening(Boolean blockBatchOpen) {
        this.blockBatchOpening = blockBatchOpen;
    }
}
