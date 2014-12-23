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
 * @version $Id: code-templates.xml 12647 2013-05-01 18:01:19Z vlads $
 */
package com.pyx4j.forms.client.ui.datatable.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.widgets.client.CheckGroup;
import com.pyx4j.widgets.client.OptionGroup.Layout;

public class MultiSelectFilterEditor extends FilterEditorBase implements IFilterEditor {

    private final CheckGroup<?> checkGroup;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public MultiSelectFilterEditor(IObject<?> member) {
        super(member);
        checkGroup = new CheckGroup<>(Layout.VERTICAL);
        initWidget(checkGroup);
        MemberMeta mm = member.getMeta();
        if (mm.isEntity()) {
            //TODO talk to Slava
        } else if (mm.getValueClass().isEnum()) {
            checkGroup.setOptions(new ArrayList(EnumSet.allOf((Class<Enum>) mm.getValueClass())));
        }
    }

    @Override
    public PropertyCriterion getPropertyCriterion() {
        if (checkGroup.getValue() == null || checkGroup.getValue().size() == 0) {
            return null;
        } else {
            return PropertyCriterion.in(getMember(), checkGroup.getValue());
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void setPropertyCriterion(PropertyCriterion criterion) {
        if (criterion == null || criterion.getValue() == null) {
            checkGroup.setValue(null);
        } else {
            if (criterion.getRestriction() != PropertyCriterion.Restriction.IN) {
                throw new Error("Filter criterion isn't supported by editor");
            }

            if (!getMember().getPath().toString().equals(criterion.getPropertyPath())) {
                throw new Error("Filter editor member doesn't mach filter criterion path");
            }

            if (!(criterion.getValue() instanceof Collection)) {
                throw new Error("Filter criterion value class is" + criterion.getValue().getClass().getSimpleName() + ". Collection is expected.");
            }

            checkGroup.setValue((Collection) criterion.getValue());
        }
    }
}
