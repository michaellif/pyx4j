/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 22, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.pmsite.server.panels;

import java.math.BigDecimal;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import templates.TemplateResources;

import com.pyx4j.commons.MinMaxPair;
import com.pyx4j.commons.SimpleMessageFormat;

import com.propertyvista.domain.MediaFile;
import com.propertyvista.domain.PublicVisibilityType;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.media.ThumbnailSize;
import com.propertyvista.domain.property.PropertyContact;
import com.propertyvista.domain.property.PropertyContact.PropertyContactType;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.util.DomainUtil;
import com.propertyvista.pmsite.server.PMSiteContentManager;
import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.PropertyFinder;
import com.propertyvista.pmsite.server.model.WicketUtils.SimpleImage;
import com.propertyvista.pmsite.server.model.WicketUtils.VolatileTemplateResourceReference;

public class FloorplanInfoPanel extends Panel {
    private static final long serialVersionUID = 1L;

    public FloorplanInfoPanel(String id, Floorplan fp) {
        super(id);

        final List<AptUnit> fpUnits = PropertyFinder.getFloorplanUnits(fp);

        long mediaId = 0;
        if (fp.media().size() > 0) {
            mediaId = fp.media().get(0).getPrimaryKey().asLong();
        }
        SimpleImage pic = new SimpleImage("picture", PMSiteContentManager.getMediaImgUrl(mediaId, ThumbnailSize.large));
        final String picId = "largeView";
        add(pic.add(AttributeModifier.replace("id", picId)));
        add(new ListView<MediaFile>("gallery", fp.media()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<MediaFile> item) {
                long mediaId = item.getModelObject().getPrimaryKey().asLong();
                String largeSrc = PMSiteContentManager.getMediaImgUrl(mediaId, ThumbnailSize.large);
                SimpleImage tn = new SimpleImage("thumbnail", PMSiteContentManager.getMediaImgUrl(mediaId, ThumbnailSize.small));
                item.add(tn.add(AttributeModifier.replace("onClick", "setImgSrc('" + picId + "','" + largeSrc + "')")));
            }
        });
        Building bld = PropertyFinder.getBuildingDetails(fp.building().getPrimaryKey().asLong());
        AddressStructured addr = bld.info().address();
        String addrFmt = "";
        if (addr != null) {
            addrFmt += SimpleMessageFormat.format("{0} {1}, {2}, {3} {4}", addr.streetNumber().getValue(), addr.streetName().getValue(),
                    addr.city().getValue(), addr.province().code().getValue(), addr.postalCode().getValue());
        }
        add(new Label("address", addrFmt));
        // get price range
        MinMaxPair<BigDecimal> minMaxMarketRent = PropertyFinder.getMinMaxMarketRent(fpUnits);
        String priceFmt = "Not available";
        if (minMaxMarketRent.getMin() != null && minMaxMarketRent.getMax() != null) {
            BigDecimal min = DomainUtil.roundMoney(minMaxMarketRent.getMin());
            BigDecimal max = DomainUtil.roundMoney(minMaxMarketRent.getMax());
            if (max.compareTo(min) > 0) {
                priceFmt = "$" + min + " - $" + max;
            } else {
                priceFmt = "$" + min;
            }
        }
        add(new Label("rentPrice", priceFmt));
        // phone
        String phone = "Not Available";
        String email = null;
        for (PropertyContact contact : bld.contacts().propertyContacts()) {
            if (contact.visibility().getValue() == PublicVisibilityType.global && !contact.phone().isNull()) {
                phone = contact.phone().getValue();
                if (PropertyContactType.mainOffice.equals(contact.type().getValue())) {
                    email = contact.email().getValue();
                    break;
                }
            }
        }
        add(new Label("phone", phone));
        add(new Label("email", email).setVisible(email != null));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        String skin = ((PMSiteWebRequest) getRequest()).getContentManager().getSiteSkin();
        String fileCSS = skin + "/" + "floorplanInfoPanel.css";
        VolatileTemplateResourceReference refCSS = new VolatileTemplateResourceReference(TemplateResources.class, fileCSS, "text/css",
                ((PMSiteWebRequest) getRequest()).getStylesheetTemplateModel());
        response.renderCSSReference(refCSS);
        super.renderHead(response);

    }
}
