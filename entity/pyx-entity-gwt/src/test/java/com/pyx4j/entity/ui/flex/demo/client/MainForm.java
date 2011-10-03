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
 * Created on Dec 22, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.ui.flex.demo.client;

import static com.pyx4j.commons.HtmlUtils.h2;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.images.WidgetsImages;
import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.client.ui.flex.folder.BoxFolderDecorator;
import com.pyx4j.entity.client.ui.flex.folder.BoxFolderItemDecorator;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderItemEditor;
import com.pyx4j.entity.client.ui.flex.folder.IFolderDecorator;
import com.pyx4j.entity.client.ui.flex.folder.IFolderItemDecorator;
import com.pyx4j.entity.ui.flex.demo.client.domain.EntityI;
import com.pyx4j.entity.ui.flex.demo.client.domain.EntityII;
import com.pyx4j.forms.client.ui.decorators.ElegantWidgetDecorator;

public class MainForm extends CEntityEditor<EntityI> {

    private static I18n i18n = I18nFactory.getI18n(MainForm.class);

    public MainForm() {
        super(EntityI.class);
    }

    @Override
    public IsWidget createContent() {
        HTML header = new HTML(h2(i18n.tr("Main Form")));
        header.getElement().getStyle().setMarginBottom(1, Unit.EM);

        FlowPanel main = new FlowPanel();
        main.add(header);
        main.add(new ElegantWidgetDecorator(inject(proto().stringMember())));
        main.add(new ElegantWidgetDecorator(inject(proto().integerMember())));
        main.add(inject(proto().entityIIList(), createEntityIISetFolder()));
        return main;
    }

    private CEntityFolder<EntityII> createEntityIISetFolder() {

        return new CEntityFolder<EntityII>(EntityII.class) {

            @Override
            protected IFolderDecorator<EntityII> createDecorator() {
                return new BoxFolderDecorator<EntityII>(WidgetsImages.INSTANCE.add(), WidgetsImages.INSTANCE.addHover(), i18n.tr("Add EntityII"));

            }

            @Override
            protected CEntityFolderItemEditor<EntityII> createItem() {
                return createEntityIISetRow();
            }

            private CEntityFolderItemEditor<EntityII> createEntityIISetRow() {
                return new CEntityFolderItemEditor<EntityII>(EntityII.class) {

                    @Override
                    public IsWidget createContent() {
                        FlowPanel main = new FlowPanel();
                        main.add(new ElegantWidgetDecorator(inject(proto().stringMember())));
                        main.add(new ElegantWidgetDecorator(inject(proto().integerMember())));
                        return main;
                    }

                    @Override
                    public IFolderItemDecorator<EntityII> createDecorator() {
                        return new BoxFolderItemDecorator<EntityII>(WidgetsImages.INSTANCE.del(), WidgetsImages.INSTANCE.delHover(), i18n.tr("Remove EntityII"));
                    }

                };
            }

        };
    }

}