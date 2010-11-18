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
 * Created on Apr 24, 2010
 * @author michaellif
 * @version $Id: EntityEditorPanel.java 6674 2010-08-04 11:16:46Z michaellif $
 */
package com.pyx4j.entity.ria.client.crud;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.entity.client.ui.crud.AbstractEntityEditorPanel;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.CForm.LabelAlignment;

public abstract class EntityDetailsPart<E extends IEntity> extends AbstractEntityEditorPanel<E> {

    private static I18n i18n = I18nFactory.getI18n(EntityDetailsPart.class);

    public EntityDetailsPart(Class<E> clazz) {
        super(clazz);

        populateForm(null);

        ScrollPanel contentPanel = new ScrollPanel();
        add(contentPanel);

        contentPanel.add(createFormWidget(LabelAlignment.LEFT));

    }

}