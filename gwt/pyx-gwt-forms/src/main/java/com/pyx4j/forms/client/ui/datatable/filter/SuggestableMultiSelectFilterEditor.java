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

import java.util.Collection;
import java.util.Comparator;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.forms.client.ui.AsyncOptionLoadingDelegate;
import com.pyx4j.forms.client.ui.EntityDataSource;
import com.pyx4j.forms.client.ui.ReferenceDataManager;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.selector.MultyWordSuggestOptionsGrabber;
import com.pyx4j.widgets.client.selector.SelectorListBox;

public class SuggestableMultiSelectFilterEditor extends FilterEditorBase {

    private static final I18n i18n = I18n.get(SuggestableMultiSelectFilterEditor.class);

    private SelectorListBox<IEntity> selector;

    public SuggestableMultiSelectFilterEditor(IObject<?> member) {
        super(member);
        MemberMeta mm = member.getMeta();

        if (mm.isEntity()) {
            selector = new SelectorListBox<IEntity>(new EntitySuggestOptionsGrabber(mm), new IFormatter<IEntity, String>() {

                @Override
                public String format(IEntity value) {
                    // TODO Auto-generated method stub
                    return value.getStringView();
                }

            }, new IFormatter<IEntity, SafeHtml>() {

                @Override
                public SafeHtml format(IEntity value) {
                    SafeHtmlBuilder builder = new SafeHtmlBuilder();
                    builder.appendHtmlConstant(SimpleMessageFormat.format("<div>{0}</div>", value.getStringView()));
                    return builder.toSafeHtml();
                }

            });
        }
        initWidget(selector);
    }

    @Override
    public PropertyCriterion getCriterion() {
        if (selector.getValue() == null || selector.getValue().size() == 0) {
            return null;
        } else {
            return PropertyCriterion.in(getMember(), selector.getValue());
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

            if (!(propertyCriterion.getRestriction() == PropertyCriterion.Restriction.IN || propertyCriterion.getRestriction() == PropertyCriterion.Restriction.EQUAL)) {
                throw new Error("Filter criterion isn't supported by editor");
            }

            if (!getMember().getPath().toString().equals(propertyCriterion.getPropertyPath())) {
                throw new Error("Filter editor member doesn't match filter criterion path");
            }

            if (!(propertyCriterion.getValue() instanceof Collection)) {
                throw new Error("Filter criterion value class is" + propertyCriterion.getValue().getClass().getSimpleName() + ". Collection is expected.");
            }

            selector.setValue((Collection) propertyCriterion.getValue());
        }
    }

    @Override
    public void clear() {
        selector.setValue(null);
    }

    class EntitySuggestOptionsGrabber extends MultyWordSuggestOptionsGrabber<IEntity> {

        private MemberMeta memberMeta;

        AsyncOptionLoadingDelegate<IEntity> asyncOptionDelegate;

        EntitySuggestOptionsGrabber(MemberMeta memberMeta) {
            this.memberMeta = memberMeta;

            setFormatter(new IFormatter<IEntity, String>() {
                @Override
                public String format(IEntity value) {
                    return value.getStringView();
                }

            });

            setComparator(new Comparator<IEntity>() {

                @Override
                public int compare(IEntity o1, IEntity o2) {
                    return o1.getStringView().compareTo(o2.getStringView());
                }

            });

            EntityDataSource<IEntity> optionsDataSource = ReferenceDataManager.<IEntity> getDataSource(true);
            optionsDataSource.obtain(new EntityQueryCriteria<IEntity>((Class<IEntity>) memberMeta.getObjectClass()),
                    new AsyncCallback<EntitySearchResult<IEntity>>() {

                        @Override
                        public void onFailure(Throwable caught) {
                            setAllOptions(null);
                            //TODO log
                        }

                        @Override
                        public void onSuccess(EntitySearchResult<IEntity> result) {
                            setAllOptions(result.getData());
                        }

                    });

        }
    }
}
