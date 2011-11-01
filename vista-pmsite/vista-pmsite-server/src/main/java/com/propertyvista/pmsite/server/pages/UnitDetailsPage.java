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
package com.propertyvista.pmsite.server.pages;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import templates.TemplateResources;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.marketing.PublicVisibilityType;
import com.propertyvista.domain.media.Media;
import com.propertyvista.domain.property.PropertyPhone;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.FloorplanAmenity;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.site.SitePalette;
import com.propertyvista.pmsite.server.PMSiteContentManager;
import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.model.StylesheetTemplateModel;
import com.propertyvista.pmsite.server.model.WicketUtils.SimpleImage;
import com.propertyvista.pmsite.server.model.WicketUtils.VolatileTemplateResourceReference;
import com.propertyvista.portal.rpc.portal.ImageConsts.ThumbnailSize;

public class UnitDetailsPage extends BasePage {
    private static final long serialVersionUID = 1L;

    public UnitDetailsPage() {
        super();

        Long planId = null;
        try {
            planId = getRequest().getRequestParameters().getParameterValue("fpId").toLong();
        } catch (Exception e) {
            throw new RuntimeException();
//            throw new RestartResponseException(FindAptPage.class);
        }

        final Floorplan fp = PMSiteContentManager.getFloorplanDetails(planId);
        if (fp == null) {
            throw new RestartResponseException(FindAptPage.class);
        }
        final List<AptUnit> fpUnits = PMSiteContentManager.getFloorplanUnits(fp);
        final List<FloorplanAmenity> amenities = PMSiteContentManager.getFloorplanAmenities(fp);

        long mediaId = 0;
        if (fp.media().size() > 0) {
            mediaId = fp.media().get(0).getPrimaryKey().asLong();
        }
        SimpleImage pic = new SimpleImage("picture", PMSiteContentManager.getMediaImgUrl(mediaId, ThumbnailSize.large));
        final String picId = "largeView";
        add(pic.add(AttributeModifier.replace("id", picId)));
        add(new ListView<Media>("gallery", fp.media()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<Media> item) {
                long mediaId = item.getModelObject().getPrimaryKey().asLong();
                String largeSrc = PMSiteContentManager.getMediaImgUrl(mediaId, ThumbnailSize.large);
                SimpleImage tn = new SimpleImage("thumbnail", PMSiteContentManager.getMediaImgUrl(mediaId, ThumbnailSize.small));
                item.add(tn.add(AttributeModifier.replace("onClick", "setImgSrc('" + picId + "','" + largeSrc + "')")));
            }
        });
        Building bld = PMSiteContentManager.getBuildingDetails(fp.building().getPrimaryKey().asLong());
        AddressStructured addr = bld.info().address();
        String addrFmt = "";
        if (addr != null) {
            addrFmt += addr.streetNumber().getValue() + " " + addr.streetName().getValue() + ", " + addr.city().getValue() + ", "
                    + addr.province().code().getValue() + " " + addr.postalCode().getValue();
        }
        add(new Label("address", addrFmt));
        // get price range
        Double minPrice = null, maxPrice = null, minArea = null;
        String areaUnits = null;
        for (AptUnit u : fpUnits) {
            Double _prc = u.financial().unitRent().getValue();
            if (minPrice == null || minPrice > _prc) {
                minPrice = _prc;
            }
            if (maxPrice == null || maxPrice < _prc) {
                maxPrice = _prc;
            }
            Double _area = u.info().area().getValue();
            if (minArea == null || minArea > _area) {
                minArea = _area;
                areaUnits = u.info().areaUnits().getValue().toString();
            }
        }
        String priceFmt = "Not available";
        if (minPrice != null && maxPrice != null) {
            priceFmt = "$" + String.valueOf(Math.round(minPrice)) + " - $" + String.valueOf(Math.round(maxPrice));
        }
        add(new Label("priceRange", priceFmt));
        // phone
        String phone = "Not Available";
        for (PropertyPhone ph : bld.contacts().phones()) {
            if (ph.visibility().getValue() == PublicVisibilityType.global) {
                phone = ph.number().getValue();
                break;
            }
        }
        add(new Label("phone", phone));

        // right side - floorplan details
        add(new Label("backButton", "Back").add(AttributeModifier.replace("onClick", "history.back()")));
        add(new Label("name", fp.name().getValue()));
        add(new Label("rooms", "bedrooms: " + fp.bedrooms().getValue() + ", bathrooms: " + fp.bathrooms().getValue() + ", from " + Math.round(minArea) + " "
                + areaUnits));
        add(new Label("description", fp.description().getValue()));
        add(new ListView<FloorplanAmenity>("amenities", amenities) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<FloorplanAmenity> item) {
                item.add(new Label("amenity", item.getModelObject().type().getValue().toString()));
            }
        });
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        int styleId = ((PMSiteWebRequest) getRequest()).getContentManager().getStyleId();
        SitePalette sitePalette = ((PMSiteWebRequest) getRequest()).getContentManager().getSiteDescriptor().sitePalette();

        String fileCSS = "unitdetails" + styleId + ".css";
        VolatileTemplateResourceReference refCSS = new VolatileTemplateResourceReference(TemplateResources.class, fileCSS, "text/css",
                new StylesheetTemplateModel(sitePalette));
        response.renderCSSReference(refCSS);
        super.renderHead(response);

    }
}
