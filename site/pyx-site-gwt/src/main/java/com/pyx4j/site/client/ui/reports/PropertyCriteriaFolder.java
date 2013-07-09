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
 * Created on Aug 9, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.pyx4j.site.client.ui.reports;

import java.util.List;

import com.google.gwt.user.client.ui.ListBox;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.images.EntityFolderImages;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.BoxFolderDecorator;
import com.pyx4j.forms.client.ui.folder.CEntityFolder;
import com.pyx4j.forms.client.ui.folder.IFolderDecorator;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.TableFolderItemDecorator;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.shared.domain.reports.PropertyCriterionEntity;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

public class PropertyCriteriaFolder extends CEntityFolder<PropertyCriterionEntity> {

    private static final I18n i18n = I18n.get(PropertyCriteriaFolder.class);

    private final EntityFolderImages images;

    private final List<MemberColumnDescriptor> availableCriteriaColumns;

    private final Class<? extends IEntity> tableEntityClass;

    public PropertyCriteriaFolder(EntityFolderImages images, Class<? extends IEntity> tableEntityClass, List<MemberColumnDescriptor> availableCriteriaColumns) {
        super(PropertyCriterionEntity.class);
        this.images = images;
        this.availableCriteriaColumns = availableCriteriaColumns;
        this.tableEntityClass = tableEntityClass;
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof PropertyCriterionEntity) {
            return new PropertyCriterionEditor(EntityFactory.getEntityPrototype(tableEntityClass));
        } else {
            return super.create(member);
        }
    }

    @Override
    protected IFolderItemDecorator<PropertyCriterionEntity> createItemDecorator() {
        return new TableFolderItemDecorator<PropertyCriterionEntity>(images);
    }

    @Override
    protected IFolderDecorator<PropertyCriterionEntity> createFolderDecorator() {
        return new BoxFolderDecorator<PropertyCriterionEntity>(images, i18n.tr("Add"), true);
    }

    @Override
    protected void addItem() {
        new PropertyClassChoserDialog(availableCriteriaColumns) {
            @Override
            public boolean onClickOk() {
                String selectedColumnPath = getSelectedColumnPath();
                if (selectedColumnPath != null) {
                    PropertyCriterionEntity propertyCriterionEntity = EntityFactory.create(PropertyCriterionEntity.class);
                    propertyCriterionEntity.criterionName().setValue(getSelectedColumnTitle());
                    propertyCriterionEntity.path().setValue(selectedColumnPath);
                    addItem(propertyCriterionEntity);
                    return true;
                } else {
                    return false;
                }
            }
        }.show();
    }

    private static abstract class PropertyClassChoserDialog extends OkCancelDialog {

        private final ListBox comboBox;

        public PropertyClassChoserDialog(List<MemberColumnDescriptor> columnDescriptors) {
            super(i18n.tr("Choose criteria"));
            comboBox = new ListBox();
            for (ColumnDescriptor descriptor : columnDescriptors) {
                comboBox.addItem(descriptor.getColumnTitle(), descriptor.getColumnName());
            }
            setBody(comboBox);
        }

        protected String getSelectedColumnPath() {
            if (comboBox.getSelectedIndex() != -1) {
                return comboBox.getValue(comboBox.getSelectedIndex());
            } else {
                return null;
            }
        }

        protected String getSelectedColumnTitle() {
            if (comboBox.getSelectedIndex() != -1) {
                return comboBox.getItemText(comboBox.getSelectedIndex());
            } else {
                return null;
            }
        }

    }
}
