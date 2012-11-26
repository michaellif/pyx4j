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

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.marketing.AdvertisingBlurb;
import com.propertyvista.domain.marketing.Marketing;
import com.propertyvista.oapi.model.AdvertisingBlurbIO;
import com.propertyvista.oapi.model.MarketingIO;
import com.propertyvista.oapi.xml.StringIO;

public class MarketingMarshaller implements Marshaller<Marketing, MarketingIO> {

    @Override
    public MarketingIO unmarshal(Marketing marketing) {
        MarketingIO marketingIO = new MarketingIO();
        marketingIO.name = new StringIO(marketing.name().getValue());
        marketingIO.description = new StringIO(marketing.description().getValue());

        List<AdvertisingBlurbIO> adBlurbsIO = new ArrayList<AdvertisingBlurbIO>();
        for (AdvertisingBlurb adBlurb : marketing.adBlurbs()) {
            adBlurbsIO.add(new AdvertisingBlurbMarshaller().unmarshal(adBlurb));
        }
        marketingIO.blurbs = adBlurbsIO;

        return marketingIO;
    }

    @Override
    public Marketing marshal(MarketingIO marketingIO) throws Exception {
        Marketing marketing = EntityFactory.create(Marketing.class);
        marketing.name().setValue(marketingIO.name.value);
        marketing.description().setValue(marketingIO.description.value);

        List<AdvertisingBlurb> adBlurbs = new ArrayList<AdvertisingBlurb>();
        for (AdvertisingBlurbIO adBlurbIO : marketingIO.blurbs) {
            adBlurbs.add(new AdvertisingBlurbMarshaller().marshal(adBlurbIO));
        }
        marketing.adBlurbs().addAll(adBlurbs);
        return marketing;
    }
}
