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
 * Created on Nov 14, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui.filter;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.forms.client.images.FolderImages;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.ImageButton;

public class FilterPanel<E extends IEntity> extends FlowPanel {

    private static final I18n i18n = I18n.get(FilterPanel.class);

    public FilterPanel() {
        setStyleName(FilterTheme.StyleName.FilterPanel.name());

        ImageButton addButton = new ImageButton(FolderImages.INSTANCE.addButton(), i18n.tr("Add filter..."), new Command() {
            @Override
            public void execute() {
            }
        });

        add(addButton);
    }

}
