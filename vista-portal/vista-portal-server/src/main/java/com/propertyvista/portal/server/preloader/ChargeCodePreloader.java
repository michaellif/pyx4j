/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import java.util.ArrayList;
import java.util.List;

import com.propertvista.generator.ChargeCodeGenerator;

import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.financial.GlCode;
import com.propertyvista.domain.financial.tax.Tax;
import com.propertyvista.portal.server.preloader.util.BaseVistaDevDataPreloader;

public class ChargeCodePreloader extends BaseVistaDevDataPreloader {

    private static final int TAX_QUANTITY = 10;

    private static final int GLCODE_QUANTITY = 15;

    private static final int CHARGECODE_QUANTITY = 5;

    private static final int REASON_QUANTITY = 5;

    @Override
    public String create() {
        List<Tax> taxes = new ArrayList<Tax>();
        List<GlCode> glCodes = new ArrayList<GlCode>();
        int i, j, k, l;

        for (i = 0; i < TAX_QUANTITY; i++) {
            Tax tax = ChargeCodeGenerator.createTax(i);
            taxes.add(tax);
            Persistence.service().persist(tax);
        }
        for (j = 0; j < GLCODE_QUANTITY; j++) {
            GlCode glCode = ChargeCodeGenerator.createGlCode();
            glCodes.add(glCode);
            Persistence.service().persist(glCode);
        }

        for (k = 0; k < CHARGECODE_QUANTITY; k++) {
            Persistence.service().persist(ChargeCodeGenerator.createChargeCode(k, taxes, glCodes));
        }

        for (l = 0; l < REASON_QUANTITY; l++) {
            Persistence.service().persist(ChargeCodeGenerator.createLeaseAdjustmentReason(l, taxes, glCodes));
        }

        return "Created " + i + " demo taxes \nCreated " + j + " demo GlCodes \nCreated " + k + " demo ChargeCodes \nCreated " + l
                + " demo LeaseAdjustmentReasons";

    }

    @Override
    public String delete() {
        return null;
    }

}
