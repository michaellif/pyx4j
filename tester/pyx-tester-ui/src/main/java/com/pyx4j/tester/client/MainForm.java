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
package com.pyx4j.tester.client;

import static com.pyx4j.commons.HtmlUtils.h2;

import java.util.ArrayList;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.client.ui.flex.folder.BoxFolderDecorator;
import com.pyx4j.entity.client.ui.flex.folder.BoxFolderItemDecorator;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderBoxEditor;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderItemEditor;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.flex.folder.IFolderDecorator;
import com.pyx4j.entity.client.ui.flex.folder.IFolderItemDecorator;
import com.pyx4j.entity.client.ui.flex.folder.TableFolderDecorator;
import com.pyx4j.entity.client.ui.flex.folder.TableFolderItemDecorator;
import com.pyx4j.forms.client.ui.decorators.ElegantWidgetDecorator;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.tester.client.domain.EntityI;
import com.pyx4j.tester.client.domain.EntityII;
import com.pyx4j.tester.client.images.Images;

public class MainForm extends CEntityEditor<EntityI> {

    private static I18n i18n = I18n.get(MainForm.class);

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
        main.add(new HTML("---------------- Box Folder ----------------"));
        main.add(inject(proto().entityIIList1(), createEntityIIFolder1()));
        main.add(new HTML("---------------- Table Folder ----------------"));
        main.add(inject(proto().entityIIList2(), createEntityIIFolder2()));
        return main;
    }

    private CEntityFolder<EntityII> createEntityIIFolder1() {

        return new CEntityFolder<EntityII>(EntityII.class) {

            @Override
            protected IFolderDecorator<EntityII> createDecorator() {
                return new BoxFolderDecorator<EntityII>(Images.INSTANCE, i18n.tr("Add EntityII"));

            }

            @Override
            protected CEntityFolderBoxEditor<EntityII> createItem() {
                return createEntityIISetRow();
            }

            private CEntityFolderBoxEditor<EntityII> createEntityIISetRow() {
                return new CEntityFolderBoxEditor<EntityII>(EntityII.class) {

                    @Override
                    public IsWidget createContent() {
                        FlowPanel main = new FlowPanel();
                        main.add(new ElegantWidgetDecorator(inject(proto().stringMember())));
                        main.add(new ElegantWidgetDecorator(inject(proto().integerMember())));
                        return main;
                    }

                    @Override
                    public IFolderItemDecorator<EntityII> createDecorator() {
                        return new BoxFolderItemDecorator<EntityII>(Images.INSTANCE, i18n.tr("Remove EntityII"));
                    }

                };
            }

        };
    }

    private CEntityFolder<EntityII> createEntityIIFolder2() {

        return new CEntityFolder<EntityII>(EntityII.class) {

            private final ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();

            {
                columns.add(new EntityFolderColumnDescriptor(proto().stringMember(), "15em"));
                columns.add(new EntityFolderColumnDescriptor(proto().integerMember(), "15em"));
            }

            @Override
            protected IFolderDecorator<EntityII> createDecorator() {
                return new TableFolderDecorator<EntityII>(columns, Images.INSTANCE, i18n.tr("Add EntityII"));

            }

            @Override
            protected CEntityFolderItemEditor<EntityII> createItem() {
                // TODO Auto-generated method stub
                return new CEntityFolderRowEditor<EntityII>(EntityII.class, columns) {

                    @Override
                    protected IFolderItemDecorator<EntityII> createDecorator() {
                        return new TableFolderItemDecorator<EntityII>(Images.INSTANCE, i18n.tr("Remove EntityII"));
                    }
                };
            }

        };
    }
}