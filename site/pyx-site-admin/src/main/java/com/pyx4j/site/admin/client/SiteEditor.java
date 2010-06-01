/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Apr 28, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.admin.client;

import java.util.Arrays;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.CEntityForm;
import com.pyx4j.entity.client.ui.EntityFormFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CForm.LabelAlignment;
import com.pyx4j.ria.client.AbstractView;
import com.pyx4j.site.shared.domain.DefaultSkins;
import com.pyx4j.site.shared.domain.Site;

public class SiteEditor extends AbstractView {

    private final CEntityForm<Site> form;

    public SiteEditor(Site site) {
        super(new VerticalPanel(), site.siteId().getValue(), ImageFactory.getImages().image());

        VerticalPanel contentPane = (VerticalPanel) getContentPane();

        EntityFormFactory<Site> formFactory = new EntityFormFactory<Site>(Site.class) {

            @Override
            protected IObject<?>[][] getFormMembers() {

                return new IObject[][] {

                { meta().siteCaption(), meta().logoUrl() },

                { meta().skinType(), meta().footerCopiright() },

                };
            }

            @Override
            protected void enhanceComponents(CEntityForm<Site> form) {
                ((CComboBox<String>) (form.get(meta().skinType()))).setOptions(Arrays.asList(new String[] { DefaultSkins.light.name(),
                        DefaultSkins.dark.name(), DefaultSkins.business.name() }));
            }
        };

        form = formFactory.createForm();

        form.populate(site);

        form.setAllignment(LabelAlignment.LEFT);
        contentPane.add((Widget) form.initNativeComponent());

    }

    @Override
    public Widget getFooterPane() {
        return new Label("FooterPane" + getTitle());
    }

    @Override
    public MenuBar getMenu() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Widget getToolbarPane() {
        // TODO Auto-generated method stub
        return null;
    }

    public Site getSite() {
        return form.getValue();
    }

}
