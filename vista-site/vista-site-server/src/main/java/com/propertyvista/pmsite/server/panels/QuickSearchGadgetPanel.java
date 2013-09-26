/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 26, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.pmsite.server.panels;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.panel.Panel;

import com.propertyvista.domain.site.gadgets.GadgetContent;
import com.propertyvista.domain.site.gadgets.HomePageGadget;
import com.propertyvista.domain.site.gadgets.HomePageGadget.GadgetArea;
import com.propertyvista.domain.site.gadgets.HomePageGadget.GadgetStatus;
import com.propertyvista.domain.site.gadgets.HomePageGadget.GadgetType;

public class QuickSearchGadgetPanel extends Panel {
    private static final long serialVersionUID = 1L;

    public QuickSearchGadgetPanel(String id, HomePageGadget gadget) {
        super(id);

        @SuppressWarnings("unchecked")
        GadgetType type = GadgetType.getGadgetType((Class<? extends GadgetContent>) gadget.content().getInstanceValueClass());
        if (GadgetType.quickSearch.equals(type) && GadgetStatus.published.equals(gadget.status().getValue())) {
            String cssClass = GadgetArea.wide.equals(gadget.area().getValue()) ? "wideGadgetBox" : "narrowGadgetBox";
            cssClass += " " + type.name();
            add(new QuickSearchCriteriaPanel("gadgetBox").add(AttributeModifier.replace("class", cssClass)));
        } else {
            setVisible(false);
        }
    }
}
