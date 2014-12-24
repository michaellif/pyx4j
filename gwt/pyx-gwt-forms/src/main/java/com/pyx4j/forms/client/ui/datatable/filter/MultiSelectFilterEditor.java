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
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.CheckGroup;
import com.pyx4j.widgets.client.OptionGroup.Layout;

public class MultiSelectFilterEditor extends FilterEditorBase implements IFilterEditor {

    private static final I18n i18n = I18n.get(MultiSelectFilterEditor.class);

    private CheckGroup<?> checkGroup;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public MultiSelectFilterEditor(IObject<?> member) {
        super(member);
        MemberMeta mm = member.getMeta();
        if (mm.getValueClass().isEnum()) {
            checkGroup = new CheckGroup<>(Layout.VERTICAL);
            checkGroup.setOptions(new ArrayList(EnumSet.allOf((Class<Enum>) mm.getValueClass())));
        } else if (mm.getValueClass().equals(Boolean.class)) {

            CheckGroup<Boolean> booleanGroup = new CheckGroup<>(Layout.HORISONTAL);
            booleanGroup.setFormatter(new IFormatter<Boolean, SafeHtml>() {

                @Override
                public SafeHtml format(Boolean value) {
                    String title;
                    if (value == null) {
                        title = i18n.tr("Empty");
                    } else if (value) {
                        title = i18n.tr("Yes");
                    } else {
                        title = i18n.tr("No");
                    }
                    return SafeHtmlUtils.fromTrustedString(title);
                }
            });
            if (mm.isAnnotationPresent(NotNull.class)) {
                booleanGroup.setOptions(Arrays.asList(new Boolean[] { Boolean.FALSE, Boolean.TRUE }));
            } else {
                booleanGroup.setOptions(Arrays.asList(new Boolean[] { Boolean.FALSE, Boolean.TRUE, null }));
            }
            checkGroup = booleanGroup;
        }

        initWidget(checkGroup);
    }

    @Override
    public PropertyCriterion getCriterion() {
        if (checkGroup.getValue() == null || checkGroup.getValue().size() == 0) {
            return null;
        } else {
            return PropertyCriterion.in(getMember(), checkGroup.getValue());
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void setCriterion(Criterion criterion) {
        if (criterion == null) {
            checkGroup.setValue(null);
        } else {
            if (!(criterion instanceof PropertyCriterion)) {
                throw new Error("Filter criterion isn't supported by editor");
            }

            PropertyCriterion propertyCriterion = (PropertyCriterion) criterion;

            if (propertyCriterion.getRestriction() != PropertyCriterion.Restriction.IN
                    || propertyCriterion.getRestriction() != PropertyCriterion.Restriction.EQUAL) {
                throw new Error("Filter criterion isn't supported by editor");
            }

            if (!getMember().getPath().toString().equals(propertyCriterion.getPropertyPath())) {
                throw new Error("Filter editor member doesn't mach filter criterion path");
            }

            if (!(propertyCriterion.getValue() instanceof Collection)) {
                throw new Error("Filter criterion value class is" + propertyCriterion.getValue().getClass().getSimpleName() + ". Collection is expected.");
            }

            checkGroup.setValue((Collection) propertyCriterion.getValue());
        }
    }
}
