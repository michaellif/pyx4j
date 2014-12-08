/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 18, 2014
 * @author ernestog
 * @version $Id$
 */
package com.propertyvista.operations.server.services.tools.oapi;

import com.propertyvista.operations.domain.imports.OapiConversion;
import com.propertyvista.operations.server.services.tools.oapi.base.ConverterToOAPIBase;

public class ConverterToOAPIFactory {

    @SuppressWarnings("rawtypes")
    static ConverterToOAPI create(OapiConversion data) {
        switch (data.type().getValue()) {
        //TODO case between other versions of converters
        default:
            return new ConverterToOAPIBase();
        }

    }
}
