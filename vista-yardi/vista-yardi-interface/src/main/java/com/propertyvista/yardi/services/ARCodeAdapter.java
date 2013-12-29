/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-11
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.yardi.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.ARCode.ActionType;

public class ARCodeAdapter {

    private static final Logger log = LoggerFactory.getLogger(ARCodeAdapter.class);

    /**
     * @param customerId
     *            something to identify the charge for logging puposes
     * @return product item type or <code>null</code> if a product item type for the given chargeCode haven't been found
     */
    public ARCode findARCode(ARCode.ActionType actionType, String chargeCode, String customerId) {
        ARCode code = retrieveARCode(actionType, chargeCode);

        if (code == null) {
            log.debug("An AR Code for yardi charge code {} wasn't found: will try to find a default with action type {}, customerId={}", chargeCode,
                    actionType, customerId);
            code = retrieveDefaultUnkownARCode(actionType);
        } else if (code.type().getValue().getActionType() != actionType) {
            log.warn("An AR Code for yardi charge code has unexpected action type: got {} but expected {}, customerId={}", code.type().getValue()
                    .getActionType(), actionType, customerId);
            code = retrieveDefaultUnkownARCode(actionType);
        }

        return code;

    }

    public ARCode retrieveARCode(ARCode.ActionType actionType, String chargeCode) {
        EntityQueryCriteria<ARCode> criteria = EntityQueryCriteria.create(ARCode.class);
        criteria.eq(criteria.proto().yardiChargeCodes().$().yardiChargeCode(), chargeCode);
        List<ARCode> codes = Persistence.service().query(criteria);
        ARCode code = null;
        for (ARCode cd : codes) {
            if (cd.type().getValue().getActionType() == actionType) {
                code = cd;
                break;
            }
        }
        return code;
    }

    private ARCode retrieveDefaultUnkownARCode(ARCode.ActionType actionType) {
        EntityQueryCriteria<ARCode> unknownExternalCodeCriteria = EntityQueryCriteria.create(ARCode.class);
        unknownExternalCodeCriteria.eq(unknownExternalCodeCriteria.proto().type(), actionType == ActionType.Credit ? ARCode.Type.ExternalCredit
                : ARCode.Type.ExternalCharge);
        unknownExternalCodeCriteria.eq(unknownExternalCodeCriteria.proto().reserved(), true);
        ARCode code = Persistence.service().retrieve(unknownExternalCodeCriteria);
        if (code == null) {
            throw new IllegalStateException("ARCode for external unknown charge action type '" + actionType + "' wasn't found!");
        }
        return code;
    }
}
