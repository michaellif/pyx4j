/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 12, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.pmsite.server.panels;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import templates.TemplateResources;

import com.pyx4j.commons.MinMaxPair;
import com.pyx4j.i18n.shared.I18n;

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

public class BuildingInfoPanel extends Panel {
    private static final long serialVersionUID = 1L;

    private static final I18n i18n = I18n.get(BuildingInfoPanel.class);

    private static final String NAString = "Not Available";

    public BuildingInfoPanel(String id, Building bld) {
        super(id);

        SimpleImage pic = new SimpleImage("picture", PMSiteContentManager.getFistVisibleMediaImgUrl(bld.media(), ThumbnailSize.large));
        final String picId = "largeView";
        add(pic.add(AttributeModifier.replace("id", picId)));
        add(new ListView<MediaFile>("gallery", PMSiteContentManager.getVisibleMedia(bld.media())) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<MediaFile> item) {
                long mediaId = item.getModelObject().getPrimaryKey().asLong();
                String largeSrc = PMSiteContentManager.getMediaImgUrl(mediaId, ThumbnailSize.large);
                SimpleImage tn = new SimpleImage("thumbnail", PMSiteContentManager.getMediaImgUrl(mediaId, ThumbnailSize.small));
                item.add(tn.add(AttributeModifier.replace("onClick", "setImgSrc('" + picId + "','" + largeSrc + "')")));
            }
        });
        // address
        AddressStructured addr = bld.info().address();
        add(new Label("address", addr != null ? addr.getStringView() : ""));
        // get price range
        MinMaxPair<BigDecimal> minMaxMarketRent = new MinMaxPair<BigDecimal>();
        final Map<Floorplan, List<AptUnit>> fpUnits = PropertyFinder.getBuildingFloorplans(bld);
        for (Floorplan fp : fpUnits.keySet()) {
            minMaxMarketRent = DomainUtil.minMaxPair(minMaxMarketRent, PropertyFinder.getMinMaxMarketRent(fpUnits.get(fp)));
        }
        String priceFmt = i18n.tr(NAString);
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
        String phone = i18n.tr(NAString);
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
        String fileCSS = skin + "/" + "buildingInfoPanel.css";
        VolatileTemplateResourceReference refCSS = new VolatileTemplateResourceReference(TemplateResources.class, fileCSS, "text/css",
                ((PMSiteWebRequest) getRequest()).getStylesheetTemplateModel());
        response.renderCSSReference(refCSS);
        super.renderHead(response);
    }
}
