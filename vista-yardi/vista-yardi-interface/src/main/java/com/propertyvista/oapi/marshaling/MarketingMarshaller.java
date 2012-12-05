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
import java.util.List;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.marketing.AdvertisingBlurb;
import com.propertyvista.domain.marketing.Marketing;
import com.propertyvista.oapi.model.AdvertisingBlurbIO;
import com.propertyvista.oapi.model.MarketingIO;
import com.propertyvista.oapi.xml.Action;
import com.propertyvista.oapi.xml.StringIO;

public class MarketingMarshaller implements Marshaller<Marketing, MarketingIO> {

    private static class SingletonHolder {
        public static final MarketingMarshaller INSTANCE = new MarketingMarshaller();
    }

    private MarketingMarshaller() {
    }

    public static MarketingMarshaller getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public MarketingIO marshal(Marketing marketing) {
        MarketingIO marketingIO = new MarketingIO();
        marketingIO.name = marketing.name().getValue();
        marketingIO.description = new StringIO(marketing.description().getValue());

        Persistence.service().retrieveMember(marketing.adBlurbs());
        List<AdvertisingBlurbIO> adBlurbsIO = new ArrayList<AdvertisingBlurbIO>();
        for (AdvertisingBlurb adBlurb : marketing.adBlurbs()) {
            adBlurbsIO.add(AdvertisingBlurbMarshaller.getInstance().marshal(adBlurb));
        }
        marketingIO.blurbs = adBlurbsIO;

        return marketingIO;
    }

    @Override
    public Marketing unmarshal(MarketingIO marketingIO) throws Exception {
        if (marketingIO == null) {
            return null;
        }
        Marketing marketing = EntityFactory.create(Marketing.class);
        if (marketingIO.getAction() == Action.nil) {
            marketing.set(null);
        } else {
            marketing.name().setValue(marketingIO.name);
            marketing.description().setValue(marketingIO.description.value);

            List<AdvertisingBlurb> adBlurbs = new ArrayList<AdvertisingBlurb>();
            for (AdvertisingBlurbIO adBlurbIO : marketingIO.blurbs) {
                adBlurbs.add(AdvertisingBlurbMarshaller.getInstance().unmarshal(adBlurbIO));
            }
            marketing.adBlurbs().addAll(adBlurbs);
        }
        return marketing;
    }
}
