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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.property.asset.Utility;
import com.propertyvista.oapi.model.UtilityIO;

public class UtilityMarshaller implements Marshaller<Utility, UtilityIO> {

    private static class SingletonHolder {
        public static final UtilityMarshaller INSTANCE = new UtilityMarshaller();
    }

    private UtilityMarshaller() {
    }

    public static UtilityMarshaller getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public UtilityIO marshal(Utility utility) {
        if (utility == null || utility.isNull()) {
            return null;
        }
        UtilityIO utilityIO = new UtilityIO();
        utilityIO.name = utility.name().getValue();

        return utilityIO;
    }

    public List<UtilityIO> marshal(Collection<Utility> utilities) {
        List<UtilityIO> utilityIOList = new ArrayList<UtilityIO>();
        for (Utility utility : utilities) {
            utilityIOList.add(marshal(utility));
        }
        return utilityIOList;
    }

    @Override
    public Utility unmarshal(UtilityIO utilityIO) throws Exception {
        Utility utility = EntityFactory.create(Utility.class);
        utility.name().setValue(utilityIO.name);
        return utility;
    }

    public List<Utility> unmarshal(Collection<UtilityIO> utilityIOList) throws Exception {
        List<Utility> utilities = new ArrayList<Utility>();
        for (UtilityIO utilityIO : utilityIOList) {
            utilities.add(unmarshal(utilityIO));
        }
        return utilities;
    }
}
