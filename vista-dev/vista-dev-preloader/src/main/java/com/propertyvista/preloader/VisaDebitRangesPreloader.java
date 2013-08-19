/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 19, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.preloader;

import java.util.List;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.essentials.server.csv.EntityCSVReciver;

import com.propertyvista.operations.domain.dev.VisaDebitRange;

public class VisaDebitRangesPreloader extends AbstractDataPreloader {

    @Override
    public String create() {

        EntityCSVReciver<VisaDebitRange> rcv = EntityCSVReciver.create(VisaDebitRange.class);
        rcv.setHeaderIgnoreCase(true);
        List<VisaDebitRange> importData = rcv.loadResourceFile("VisaDebitRanges.xlsx");
        Persistence.service().persist(importData);
        return null;
    }

    @Override
    public String delete() {
        return null;
    }

}
