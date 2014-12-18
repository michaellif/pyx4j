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
 */
package com.pyx4j.tester.client.view.form.folder;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.folder.BoxFolderDecorator;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.CFolder;
import com.pyx4j.forms.client.ui.folder.CFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.IFolderDecorator;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.TableFolderDecorator;
import com.pyx4j.forms.client.ui.folder.TableFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormFieldDecoratorOptions;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.tester.client.domain.test.EntityI;
import com.pyx4j.tester.client.domain.test.EntityII;
import com.pyx4j.tester.client.domain.test.EntityIII;
import com.pyx4j.tester.client.domain.test.EntityIV;
import com.pyx4j.tester.client.images.Images;
import com.pyx4j.tester.client.view.form.TesterFormPanel;

public class EntityIForm extends CForm<EntityI> {

    private static final I18n i18n = I18n.get(EntityIForm.class);

    public EntityIForm() {
        super(EntityI.class);
    }

    @Override
    protected IsWidget createContent() {
        TesterFormPanel formPanel = new TesterFormPanel(this);

        formPanel.h1(i18n.tr("Main Form"));
        formPanel.append(Location.Left, proto().textBox()).decorate();
        formPanel.append(Location.Left, proto().integerBox()).decorate();

        formPanel.h2(i18n.tr("Box Folder"));
        formPanel.append(Location.Dual, proto().entityIIList(), new EntityIIFolder());

        formPanel.h2(i18n.tr("Table Folder"));
        formPanel.append(Location.Dual, proto().entityIVList(), new EntityIVFolder());
        return formPanel;
    }

    static class EntityIIFolder extends CFolder<EntityII> {
        public EntityIIFolder() {
            super(EntityII.class);
        }

        @Override
        protected IFolderDecorator<EntityII> createFolderDecorator() {
            return new BoxFolderDecorator<EntityII>(Images.INSTANCE, i18n.tr("Add EntityII"));

        }

        @Override
        public IFolderItemDecorator<EntityII> createItemDecorator() {
            BoxFolderItemDecorator<EntityII> decorator = new BoxFolderItemDecorator<EntityII>(Images.INSTANCE);
            return decorator;
        }

        @Override
        protected CForm<EntityII> createItemForm(IObject<?> member) {
            return new EntityIIEditor();
        }

    }

    static class EntityIIEditor extends CForm<EntityII> {

        public EntityIIEditor() {
            super(EntityII.class);
        }

        @Override
        protected IsWidget createContent() {
            TesterFormPanel formPanel = new TesterFormPanel(this);

            formPanel.append(Location.Left, proto().optionalTextI()).decorate();
            formPanel.append(Location.Left, proto().optionalInteger()).decorate();
            formPanel.h3(i18n.tr("Box Folder"));
            formPanel.append(Location.Left, proto().entityIIIList(), new EntityIIIFolder());
            formPanel.h3(i18n.tr("Table Folder"));
            formPanel.append(Location.Left, proto().entityIVList(), new EntityIVFolder());
            return formPanel;
        }
    }

    static class EntityIIIFolder extends CFolder<EntityIII> {

        public EntityIIIFolder() {
            super(EntityIII.class);
        }

        @Override
        protected IFolderDecorator<EntityIII> createFolderDecorator() {
            return new BoxFolderDecorator<EntityIII>(Images.INSTANCE, i18n.tr("Add EntityIII"));

        }

        @Override
        public IFolderItemDecorator<EntityIII> createItemDecorator() {
            return new BoxFolderItemDecorator<EntityIII>(Images.INSTANCE);
        }

        @Override
        protected CForm<EntityIII> createItemForm(IObject<?> member) {
            return new EntityIIIEditor();
        }

    }

    static class EntityIIIEditor extends CForm<EntityIII> {

        public EntityIIIEditor() {
            super(EntityIII.class);
        }

        @Override
        protected IsWidget createContent() {
            FlowPanel main = new FlowPanel();
            main.add(inject(proto().stringMember(), new FormFieldDecoratorOptions().build()));
            main.add(inject(proto().integerMember(), new FormFieldDecoratorOptions().build()));
            return main;
        }
    }

    static class EntityIVFolder extends CFolder<EntityIV> {

        public static final ArrayList<FolderColumnDescriptor> COLUMNS = new ArrayList<FolderColumnDescriptor>();
        static {
            EntityIV proto = EntityFactory.getEntityPrototype(EntityIV.class);
            COLUMNS.add(new FolderColumnDescriptor(proto.stringMember(), "15em"));
            COLUMNS.add(new FolderColumnDescriptor(proto.integerMember(), "15em"));
        }

        public EntityIVFolder() {
            super(EntityIV.class);
        }

        @Override
        protected IFolderDecorator<EntityIV> createFolderDecorator() {
            return new TableFolderDecorator<EntityIV>(COLUMNS, Images.INSTANCE, i18n.tr("Add EntityIV"));
        }

        @Override
        protected IFolderItemDecorator<EntityIV> createItemDecorator() {
            return new TableFolderItemDecorator<EntityIV>(Images.INSTANCE);
        }

        @Override
        protected CForm<EntityIV> createItemForm(IObject<?> member) {
            return new EntityIVEditor();
        }
    }

    static class EntityIVEditor extends CFolderRowEditor<EntityIV> {
        public EntityIVEditor() {
            super(EntityIV.class, EntityIVFolder.COLUMNS);
        }
    }

}