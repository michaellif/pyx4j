/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 6, 2014
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.shared.ui.util;

import com.pyx4j.forms.client.ui.CEntityLabel;

import com.propertyvista.domain.property.asset.building.Building;

public class CBuildingLabel extends CEntityLabel<Building> {

// TODO: VISTA-5535 - required format is implemented (remove 2)
//       BUT we do not want to send so many building info to client!
//    @Override
    public String format2(Building value) {
        if (value == null) {
            return "";
        } else {
            StringBuilder result = new StringBuilder();

            if (!value.complex().isNull() && !value.complex().name().isNull()) {
                result.append(value.complex().name().getStringView());
                result.append(", ");
            }

            if (!value.marketing().name().isNull()) {
                result.append(value.marketing().name().getStringView());
                result.append(", ");
            } else if (!value.info().name().isNull()) {
                result.append(value.info().name().getStringView());
                result.append(", ");
            }

            result.append(value.info().address().getStringView());

            return result.toString();
        }
    }
}
