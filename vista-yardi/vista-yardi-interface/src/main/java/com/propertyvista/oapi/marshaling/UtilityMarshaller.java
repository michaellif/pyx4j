/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 24, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.marshaling;

import com.propertyvista.domain.property.asset.Utility;
import com.propertyvista.oapi.model.UtilityIO;

public class UtilityMarshaller implements Marshaller<Utility, UtilityIO> {

    @Override
    public UtilityIO unmarshal(Utility utility) {
        UtilityIO utilityIO = new UtilityIO();
        utilityIO.name = utility.name().getValue();
        return utilityIO;
    }

    @Override
    public Utility marshal(UtilityIO utilityIO) throws Exception {
        return null;
    }
}
