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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import templates.TemplateResources;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.marketing.PublicVisibilityType;
import com.propertyvista.domain.media.Media;
import com.propertyvista.domain.property.PropertyPhone;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.site.SitePalette;
import com.propertyvista.pmsite.server.PMSiteContentManager;
import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.model.StylesheetTemplateModel;
import com.propertyvista.pmsite.server.model.WicketUtils.SimpleImage;
import com.propertyvista.pmsite.server.model.WicketUtils.VolatileTemplateResourceReference;
import com.propertyvista.portal.rpc.portal.ImageConsts.ThumbnailSize;

public class AptDetailsPage extends BasePage {

    private static final long serialVersionUID = 1L;

    public AptDetailsPage() {
        super();

        Long propId = null;
        try {
            propId = getRequest().getRequestParameters().getParameterValue("propId").toLong();
        } catch (Exception e) {
            throw new RestartResponseException(FindAptPage.class);
        }

        final Building propInfo = PMSiteContentManager.getBuildingDetails(propId);
        if (propInfo == null) {
            throw new RestartResponseException(FindAptPage.class);
        }
        final Map<Floorplan, List<AptUnit>> fpUnits = PMSiteContentManager.getBuildingFloorplans(propInfo);
        final List<BuildingAmenity> amenities = PMSiteContentManager.getBuildingAmenities(propInfo);

        long mediaId = 0;
        if (propInfo.media().size() > 0) {
            mediaId = propInfo.media().get(0).getPrimaryKey().asLong();
        }
        SimpleImage pic = new SimpleImage("picture", PMSiteContentManager.getMediaImgUrl(mediaId, ThumbnailSize.large));
        final String picId = "largeView";
        add(pic.add(AttributeModifier.replace("id", picId)));
        add(new ListView<Media>("gallery", propInfo.media()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<Media> item) {
                long mediaId = item.getModelObject().getPrimaryKey().asLong();
                String largeSrc = PMSiteContentManager.getMediaImgUrl(mediaId, ThumbnailSize.large);
                SimpleImage tn = new SimpleImage("thumbnail", PMSiteContentManager.getMediaImgUrl(mediaId, ThumbnailSize.small));
                item.add(tn.add(AttributeModifier.replace("onClick", "setImgSrc('" + picId + "','" + largeSrc + "')")));
            }
        });
        AddressStructured addr = propInfo.info().address();
        String addrFmt = "";
        if (addr != null) {
            addrFmt += addr.streetNumber().getValue() + " " + addr.streetName().getValue() + ", " + addr.city().getValue() + ", "
                    + addr.province().code().getValue() + " " + addr.postalCode().getValue();
        }
        add(new Label("address", addrFmt));
        // get price range
        Double minPrice = null, maxPrice = null;
        for (Floorplan fp : fpUnits.keySet()) {
            for (AptUnit u : fpUnits.get(fp)) {
                Double _prc = u.financial().unitRent().getValue();
                if (minPrice == null || minPrice > _prc) {
                    minPrice = _prc;
                }
                if (maxPrice == null || maxPrice < _prc) {
                    maxPrice = _prc;
                }
            }
        }
        String priceFmt = "Not available";
        if (minPrice != null && maxPrice != null) {
            priceFmt = "$" + String.valueOf(Math.round(minPrice)) + " - $" + String.valueOf(Math.round(maxPrice));
        }
        add(new Label("priceRange", priceFmt));
        // phone
        String phone = "Not Available";
        for (PropertyPhone ph : propInfo.contacts().phones()) {
            if (ph.visibility().getValue() == PublicVisibilityType.global) {
                phone = ph.number().getValue();
                break;
            }
        }
        add(new Label("phone", phone));
        // right side - floorplan listing
        add(new Label("backButton", "Back").add(AttributeModifier.replace("onClick", "history.back()")));
        add(new ListView<Floorplan>("units", new ArrayList<Floorplan>(fpUnits.keySet())) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<Floorplan> item) {
                Floorplan floorPlan = item.getModelObject();
                long mediaId = 0;
                if (floorPlan.media().size() > 0) {
                    mediaId = floorPlan.media().get(0).getPrimaryKey().asLong();
                }
                item.add(new SimpleImage("plan", PMSiteContentManager.getMediaImgUrl(mediaId, ThumbnailSize.small)));
                item.add(new Label("name", floorPlan.name().getValue()));
                item.add(new Label("beds", String.valueOf(floorPlan.bedrooms().getValue())));
                item.add(new Label("bath", String.valueOf(floorPlan.bathrooms().getValue())));
                String price = "price not available";
                Double minPrice = null;
                for (AptUnit u : fpUnits.get(floorPlan)) {
                    Double _prc = u.financial().unitRent().getValue();
                    if (minPrice == null || minPrice > _prc) {
                        minPrice = _prc;
                    }
                }
                if (minPrice != null) {
                    price = "from $" + String.valueOf(Math.round(minPrice));
                }
                item.add(new Label("price", price));
                item.add(new BookmarkablePageLink<Void>("unitDetails", UnitDetailsPage.class, new PageParameters().add("fpId", floorPlan.id().getValue())));
            }
        });
        add(new Label("description", propInfo.marketing().description().getValue()));
        add(new ListView<BuildingAmenity>("amenities", amenities) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<BuildingAmenity> item) {
                item.add(new Label("amenity", item.getModelObject().name().getValue()));
            }
        });
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        int styleId = ((PMSiteWebRequest) getRequest()).getContentManager().getStyleId();
        SitePalette sitePalette = ((PMSiteWebRequest) getRequest()).getContentManager().getSiteDescriptor().sitePalette();

        String fileCSS = "aptdetails" + styleId + ".css";
        VolatileTemplateResourceReference refCSS = new VolatileTemplateResourceReference(TemplateResources.class, fileCSS, "text/css",
                new StylesheetTemplateModel(sitePalette));
        response.renderCSSReference(refCSS);
        super.renderHead(response);

    }
}
