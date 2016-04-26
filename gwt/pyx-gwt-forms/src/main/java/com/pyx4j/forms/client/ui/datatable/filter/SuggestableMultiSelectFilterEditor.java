/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Dec 22, 2014
 * @author michaellif
 */
package com.pyx4j.forms.client.ui.datatable.filter;

import java.util.Arrays;
import java.util.Collection;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.forms.client.ui.selector.EntitySelectorListBox;
import com.pyx4j.i18n.shared.I18n;

public class SuggestableMultiSelectFilterEditor<E extends IEntity> extends FilterEditorBase {

    private static final I18n i18n = I18n.get(SuggestableMultiSelectFilterEditor.class);

    private EntitySelectorListBox<E> selector;

    @SuppressWarnings("unchecked")
    public SuggestableMultiSelectFilterEditor(E member) {
        super(member);
        MemberMeta mm = member.getMeta();

        if (mm.isEntity()) {
            selector = new EntitySelectorListBox<>((Class<E>) member.getValueClass());
            selector.setWatermark(i18n.tr("+ Add item"));
        }
        initWidget(selector);
    }

    @Override
    public PropertyCriterion getCriterion() {
        if (selector.getValue() == null || selector.getValue().size() == 0) {
            return null;
        } else {
            return PropertyCriterion.in(getMember(), selector.getValue(), AttachLevel.ToStringMembers);
        }
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    public void setCriterion(Criterion criterion) {
        if (criterion == null) {
            selector.setValue(null);
        } else {
            if (!(criterion instanceof PropertyCriterion)) {
                throw new Error("Filter criterion isn't supported by editor");
            }

            PropertyCriterion propertyCriterion = (PropertyCriterion) criterion;

            if (!(propertyCriterion.getRestriction() == PropertyCriterion.Restriction.IN
                    || propertyCriterion.getRestriction() == PropertyCriterion.Restriction.EQUAL)) {
                throw new Error("Filter criterion isn't supported by editor");
            }

            if (!getMember().getPath().equals(propertyCriterion.getPropertyPath())) {
                throw new Error("Filter editor member doesn't match filter criterion path");
            }

            Collection<E> value;
            if (propertyCriterion.getValue() instanceof Collection) {
                value = (Collection<E>) propertyCriterion.getValue();
            } else {
                value = Arrays.asList((E) propertyCriterion.getValue());
            }

            selector.setValue(value);
        }
    }

    @Override
    public void onShown() {
        super.onShown();
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                selector.setFocus(true);
            }
        });
    }

    @Override
    public void clear() {
        selector.setValue(null);
    }
}
