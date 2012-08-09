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

import com.pyx4j.entity.client.images.EntityFolderImages;
import com.pyx4j.entity.client.ui.folder.BoxFolderDecorator;
import com.pyx4j.entity.client.ui.folder.CEntityFolder;
import com.pyx4j.entity.client.ui.folder.IFolderDecorator;
import com.pyx4j.entity.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.entity.client.ui.folder.TableFolderItemDecorator;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.reports.PropertyCriterionEntity;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.i18n.shared.I18n;

public class PropertyCriteriaFolder extends CEntityFolder<PropertyCriterionEntity> {

    private static final I18n i18n = I18n.get(PropertyCriteriaFolder.class);

    private final EntityFolderImages images;

    public PropertyCriteriaFolder(EntityFolderImages images) {
        super(PropertyCriterionEntity.class);
        this.images = images;

    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof PropertyCriterionEntity) {
            return new PropertyCriterionEditor();
        } else {
            return super.create(member);
        }
    }

    @Override
    protected IFolderItemDecorator<PropertyCriterionEntity> createItemDecorator() {
        return new TableFolderItemDecorator<PropertyCriterionEntity>(images);
    }

    @Override
    protected IFolderDecorator<PropertyCriterionEntity> createDecorator() {
        return new BoxFolderDecorator<PropertyCriterionEntity>(images, i18n.tr("Add"), true);
    }

    @Override
    protected void addItem() {
        super.addItem();
    }

}
