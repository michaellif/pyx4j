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
package com.propertyvista.oapi.v1.marshaling;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.domain.marketing.Marketing;
import com.propertyvista.oapi.AbstractMarshaller;
import com.propertyvista.oapi.v1.model.MarketingIO;
import com.propertyvista.oapi.xml.StringIO;

public class MarketingMarshaller extends AbstractMarshaller<Marketing, MarketingIO> {

    private static class SingletonHolder {
        public static final MarketingMarshaller INSTANCE = new MarketingMarshaller();
    }

    private MarketingMarshaller() {
    }

    public static MarketingMarshaller getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    protected MarketingIO marshal(Marketing marketing) {
        if (marketing == null || marketing.isNull()) {
            return null;
        }
        MarketingIO marketingIO = new MarketingIO();
        marketingIO.name = getValue(marketing.name());

        marketingIO.description = createIo(StringIO.class, marketing.description());

        return marketingIO;
    }

    @Override
    protected Marketing unmarshal(MarketingIO marketingIO) {
        Marketing marketing = EntityFactory.create(Marketing.class);
        marketing.name().setValue(marketingIO.name);
        setValue(marketing.description(), marketingIO.description);

        return marketing;
    }
}
