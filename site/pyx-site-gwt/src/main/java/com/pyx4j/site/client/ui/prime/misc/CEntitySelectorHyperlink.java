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
 * Created on Sep 14, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.site.client.ui.prime.misc;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.site.client.ui.dialogs.AbstractEntitySelectorDialog;
import com.pyx4j.site.rpc.AppPlace;

/**
 * Combo component to use as entity selector. In view mode renders a hyperlink that redirects to the target place
 * (see {@link CEntitySelectorHyperlink#getTargetPlace()}). In edit mode renders s text label with an arrow image
 * that triggers Entity Selector Dialog (see {@link CEntitySelectorHyperlink#getSelectorDialog()})
 */
//TODO CEntitySelectorHyperlink should extend CEntityHyperlink ???
public abstract class CEntitySelectorHyperlink<E extends IEntity> extends CTextFieldBase<E, NEntitySelectorHyperlink<E>> {

    protected abstract AppPlace getTargetPlace();

    protected abstract AbstractEntitySelectorDialog<E> getSelectorDialog();

    @Override
    protected NEntitySelectorHyperlink<E> createWidget() {
        return new NEntitySelectorHyperlink<E>(this);
    }

    @Override
    public boolean isValueEmpty() {
        return (super.isValueEmpty() || getValue().isEmpty());
    }
}
