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
 * Created on Jun 6, 2015
 * @author michaellif
 * @version $Id: code-templates.xml 12647 2013-05-01 18:01:19Z vlads $
 */
package com.pyx4j.forms.client.ui;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.query.IBooleanCondition;
import com.pyx4j.entity.core.query.ICondition;
import com.pyx4j.entity.core.query.IDateCondition;
import com.pyx4j.entity.core.query.IDateOffsetCondition;
import com.pyx4j.entity.core.query.IDecimalRangeCondition;
import com.pyx4j.entity.core.query.IEntityCondition;
import com.pyx4j.entity.core.query.IEnumCondition;
import com.pyx4j.entity.core.query.IIntegerRangeCondition;
import com.pyx4j.entity.core.query.IQuery;
import com.pyx4j.entity.core.query.IStringCondition;
import com.pyx4j.forms.client.ui.query.QueryComposer;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Label;

@SuppressWarnings("rawtypes")
public class NQuery<E extends IQuery> extends NField<E, QueryComposer<E>, CQuery<E>, Label> {

    private static final I18n i18n = I18n.get(NQuery.class);

    public NQuery(final CQuery<E> cQuery) {
        super(cQuery);

    }

    @Override
    protected Label createViewer() {
        return new Label();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected QueryComposer<E> createEditor() {
        QueryComposer queryComposer = new QueryComposer<E>();
        queryComposer.addValueChangeHandler(new ValueChangeHandler<Collection<E>>() {

            @Override
            public void onValueChange(ValueChangeEvent<Collection<E>> event) {
                getCComponent().stopEditing();
            }
        });
        return queryComposer;
    }

    @Override
    public void setNativeValue(E value) {
        if (isViewable()) {
            getViewer().setText(queryToString(value));
        } else {
            getEditor().setQuery(value);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public E getNativeValue() throws ParseException {
        if (isViewable()) {
            assert false : "getNativeValue() shouldn't be called in viewable mode";
            return null;
        } else {
            return (E) getEditor().getQuery();
        }
    }

    private String queryToString(E value) {
        StringBuilder builder = new StringBuilder();
        if (value != null) {
            List<ICondition> conditions = new ArrayList<>();
            for (String memberName : value.getEntityMeta().getMemberNames()) {
                IObject<?> member = value.getMember(memberName);
                if (member instanceof ICondition) {
                    if (!member.isNull()) {
                        conditions.add((ICondition) member);
                    }
                }
            }

            Collections.sort(conditions, new Comparator<ICondition>() {
                @Override
                public int compare(ICondition o1, ICondition o2) {
                    return o1.displayOrder().getValue(Integer.MAX_VALUE) - o2.displayOrder().getValue(Integer.MAX_VALUE);
                }
            });
            for (ICondition condition : conditions) {
                if (builder.length() > 0) {
                    builder.append(", ");
                }
                builder.append(condition.getMeta().getCaption());
                builder.append("(");
                builder.append(conditionToString(condition));
                builder.append(")");
            }
        }
        return builder.toString();
    }

    private String conditionToString(ICondition condition) {
        if (condition instanceof IEntityCondition) {
            return i18n.tr("count") + "=" + ((IEntityCondition) condition).references().size();
        } else if (condition instanceof IEnumCondition) {
            return ((IEnumCondition) condition).values().getStringView();
        } else if (condition instanceof IBooleanCondition) {
            return ((IBooleanCondition) condition).booleanValue().getValue() + "";
        } else if (condition instanceof IStringCondition) {
            return ((IStringCondition) condition).stringValue().getValue();
        } else if (condition instanceof IDateOffsetCondition) {
            return ((IDateOffsetCondition) condition).dateOffsetValue().getValue() + " " + ((IDateOffsetCondition) condition).dateOffsetType().getValue();
        } else if (condition instanceof IDateCondition) {
            return ((IDateCondition) condition).fromDate().getValue() + "-" + ((IDateCondition) condition).toDate().getValue();
        } else if (condition instanceof IIntegerRangeCondition) {
            return ((IIntegerRangeCondition) condition).fromInteger().getValue() + "-" + ((IIntegerRangeCondition) condition).toInteger().getValue();
        } else if (condition instanceof IDecimalRangeCondition) {
            return ((IDecimalRangeCondition) condition).fromDecimal().getValue() + "-" + ((IDecimalRangeCondition) condition).toDecimal().getValue();
        } else {
            throw new Error("Filter can't be created");
        }
    }

}
