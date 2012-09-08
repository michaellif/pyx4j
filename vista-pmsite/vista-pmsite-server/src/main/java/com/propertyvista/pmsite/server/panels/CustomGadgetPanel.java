/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Sep 9, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.pmsite.server.panels;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

import com.propertyvista.domain.site.gadgets.CustomGadgetContent;
import com.propertyvista.domain.site.gadgets.GadgetContent;
import com.propertyvista.domain.site.gadgets.HomePageGadget;
import com.propertyvista.domain.site.gadgets.HomePageGadget.GadgetArea;
import com.propertyvista.domain.site.gadgets.HomePageGadget.GadgetStatus;
import com.propertyvista.domain.site.gadgets.HomePageGadget.GadgetType;

public class CustomGadgetPanel extends Panel {

    private static final long serialVersionUID = 1L;

    public CustomGadgetPanel(String id, HomePageGadget gadget) {
        super(id);

        @SuppressWarnings("unchecked")
        GadgetType type = GadgetType.getGadgetType((Class<? extends GadgetContent>) gadget.content().getInstanceValueClass());
        if (GadgetType.custom.equals(type) && GadgetStatus.published.equals(gadget.status().getValue())) {
            WebMarkupContainer gadgetBox = new WebMarkupContainer("gadgetBox");
            gadgetBox.add(new Label("moduleTitle", gadget.name().getValue()));
            CustomGadgetContent content = gadget.content().cast();
            gadgetBox.add(new Label("moduleContent", content.htmlContent().html().getValue()).setEscapeModelStrings(false));
            String cssClass = GadgetArea.wide.equals(gadget.area().getValue()) ? "wideGadgetBox" : "narrowGadgetBox";
            add(gadgetBox.add(AttributeModifier.replace("class", cssClass)));
        } else {
            setVisible(false);
        }
    }
}
