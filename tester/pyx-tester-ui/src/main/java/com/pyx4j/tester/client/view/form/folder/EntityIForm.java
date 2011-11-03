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
package com.pyx4j.tester.client.view.form.folder;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.client.ui.flex.folder.BoxFolderDecorator;
import com.pyx4j.entity.client.ui.flex.folder.BoxFolderItemDecorator;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.flex.folder.IFolderDecorator;
import com.pyx4j.entity.client.ui.flex.folder.IFolderItemDecorator;
import com.pyx4j.entity.client.ui.flex.folder.TableFolderDecorator;
import com.pyx4j.entity.client.ui.flex.folder.TableFolderItemDecorator;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.tester.client.domain.EntityI;
import com.pyx4j.tester.client.domain.EntityII;
import com.pyx4j.tester.client.domain.EntityIII;
import com.pyx4j.tester.client.domain.EntityIV;
import com.pyx4j.tester.client.images.Images;

public class EntityIForm extends CEntityEditor<EntityI> {

    private static I18n i18n = I18n.get(EntityIForm.class);

    public EntityIForm() {
        super(EntityI.class);
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        if (member instanceof EntityII) {
            return new EntityIIEditor();
        } else if (member instanceof EntityIII) {
            return new EntityIIIEditor();
        } else if (member instanceof EntityIV) {
            return new EntityIVEditor();
        } else {
            return super.create(member);
        }
    }

    @Override
    public IsWidget createContent() {

        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setH1(++row, 0, 1, i18n.tr("Main Form"));

        main.setWidget(++row, 0, new WidgetDecorator(inject(proto().stringMember())));
        main.setWidget(++row, 0, new WidgetDecorator(inject(proto().integerMember())));

        main.setH2(++row, 0, 1, i18n.tr("Box Folder"));
        main.setWidget(++row, 0, inject(proto().entityIIList(), new EntityIIFolder()));

        main.setH2(++row, 0, 1, i18n.tr("Table Folder"));
        main.setWidget(++row, 0, inject(proto().entityIVList(), new EntityIVFolder()));
        return main;
    }

    static class EntityIIFolder extends CEntityFolder<EntityII> {
        public EntityIIFolder() {
            super(EntityII.class);
        }

        @Override
        protected IFolderDecorator<EntityII> createDecorator() {
            return new BoxFolderDecorator<EntityII>(Images.INSTANCE, i18n.tr("Add EntityII"));

        }

        @Override
        public IFolderItemDecorator<EntityII> createItemDecorator() {
            BoxFolderItemDecorator<EntityII> decorator = new BoxFolderItemDecorator<EntityII>(Images.INSTANCE);
            return decorator;
        }

    }

    static class EntityIIEditor extends CEntityEditor<EntityII> {

        public EntityIIEditor() {
            super(EntityII.class);
        }

        @Override
        public IsWidget createContent() {

            FormFlexPanel main = new FormFlexPanel();

            int row = -1;

            main.setWidget(++row, 0, new WidgetDecorator(inject(proto().stringMember())));
            main.setWidget(++row, 0, new WidgetDecorator(inject(proto().integerMember())));
            main.setH3(++row, 0, 1, i18n.tr("Box Folder"));
            main.setWidget(++row, 0, inject(proto().entityIIIList(), new EntityIIIFolder()));
            main.setH3(++row, 0, 1, i18n.tr("Table Folder"));
            main.setWidget(++row, 0, inject(proto().entityIVList(), new EntityIVFolder()));
            return main;
        }

    }

    static class EntityIIIFolder extends CEntityFolder<EntityIII> {

        public EntityIIIFolder() {
            super(EntityIII.class);
        }

        @Override
        protected IFolderDecorator<EntityIII> createDecorator() {
            return new BoxFolderDecorator<EntityIII>(Images.INSTANCE, i18n.tr("Add EntityIII"));

        }

        @Override
        public IFolderItemDecorator<EntityIII> createItemDecorator() {
            return new BoxFolderItemDecorator<EntityIII>(Images.INSTANCE);
        }

    }

    static class EntityIIIEditor extends CEntityEditor<EntityIII> {

        public EntityIIIEditor() {
            super(EntityIII.class);
        }

        @Override
        public IsWidget createContent() {
            FlowPanel main = new FlowPanel();
            main.add(new WidgetDecorator(inject(proto().stringMember())));
            main.add(new WidgetDecorator(inject(proto().integerMember())));
            return main;
        }

    }

    static class EntityIVFolder extends CEntityFolder<EntityIV> {

        public static final ArrayList<EntityFolderColumnDescriptor> COLUMNS = new ArrayList<EntityFolderColumnDescriptor>();
        static {
            EntityIV proto = EntityFactory.getEntityPrototype(EntityIV.class);
            COLUMNS.add(new EntityFolderColumnDescriptor(proto.stringMember(), "15em"));
            COLUMNS.add(new EntityFolderColumnDescriptor(proto.integerMember(), "15em"));
        }

        public EntityIVFolder() {
            super(EntityIV.class);
        }

        @Override
        protected IFolderDecorator<EntityIV> createDecorator() {
            return new TableFolderDecorator<EntityIV>(COLUMNS, Images.INSTANCE, i18n.tr("Add EntityIV"));
        }

        @Override
        protected IFolderItemDecorator<EntityIV> createItemDecorator() {
            return new TableFolderItemDecorator<EntityIV>(Images.INSTANCE);
        }

    }

    static class EntityIVEditor extends CEntityFolderRowEditor<EntityIV> {
        public EntityIVEditor() {
            super(EntityIV.class, EntityIVFolder.COLUMNS);
        }
    }

}