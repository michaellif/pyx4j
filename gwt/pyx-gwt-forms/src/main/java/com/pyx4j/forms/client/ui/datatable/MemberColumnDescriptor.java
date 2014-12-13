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
 * Created on Feb 18, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.forms.client.ui.datatable;

import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import com.pyx4j.commons.ConverterUtils;
import com.pyx4j.commons.ConverterUtils.ToStringConverter;
import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.IPrimitiveSet;
import com.pyx4j.entity.core.ISet;
import com.pyx4j.entity.core.ObjectClassType;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.entity.shared.IUserPreferences;
import com.pyx4j.security.shared.Context;

public class MemberColumnDescriptor extends ColumnDescriptor {

    protected MemberColumnDescriptor(Builder builder) {
        super(builder);
    }

    public Path getColumnPath() {
        return ((Builder) getBuilder()).member.getPath();
    }

    public static class Builder extends ColumnDescriptor.Builder {

        private final IObject<?> member;

        public Builder(IObject<?> member) {
            super(member.getPath().toString(), member.getMeta().getCaption());
            this.member = member;
            if (EnumSet.of(ObjectClassType.EntityList, ObjectClassType.EntitySet).contains(member.getMeta().getObjectClassType())) {
                this.sortable(false);
            }
        }

        public Builder(IObject<?> member, boolean visible) {
            this(member);
            visible(visible);
        }

        public Builder title(IObject<?> member) {
            title(member.getMeta().getCaption());
            return this;
        }

        @Override
        public ColumnDescriptor build() {

            if (formatter == null) {
                MemberMeta mm = member.getMeta();
                if (mm.isEntity()) {
                    formatter = new IFormatter<IEntity, SafeHtml>() {

                        @Override
                        public SafeHtml format(IEntity value) {
                            SafeHtmlBuilder builder = new SafeHtmlBuilder();
                            builder.appendHtmlConstant(((IEntity) value.getMember(member.getPath())).getStringView());
                            return builder.toSafeHtml();
                        }
                    };
                } else if ((member instanceof ISet<?>) || (member instanceof IList<?>)) {
                    formatter = new IFormatter<IEntity, SafeHtml>() {

                        @SuppressWarnings("unchecked")
                        @Override
                        public SafeHtml format(IEntity value) {
                            SafeHtmlBuilder builder = new SafeHtmlBuilder();
                            Object memberValue = value.getMember(member.getPath());
                            if (memberValue == null) {
                            } else if (memberValue instanceof Collection<?>) {
                                builder.appendHtmlConstant(ConverterUtils.convertCollection((Collection<IEntity>) memberValue,
                                        new ToStringConverter<IEntity>() {

                                            @Override
                                            public String toString(IEntity value) {
                                                return value.getStringView();
                                            }
                                        }));
                            } else {
                                builder.appendHtmlConstant(memberValue.toString());
                            }

                            return builder.toSafeHtml();
                        }
                    };
                } else if (member instanceof IPrimitiveSet<?>) {
                    formatter = new IFormatter<IEntity, SafeHtml>() {

                        @SuppressWarnings("unchecked")
                        @Override
                        public SafeHtml format(IEntity value) {
                            SafeHtmlBuilder builder = new SafeHtmlBuilder();
                            Object memberValue = value.getMember(member.getPath());
                            if (memberValue == null) {
                            } else if (memberValue instanceof Collection<?>) {
                                builder.appendHtmlConstant(ConverterUtils.convertCollection((Collection<Object>) memberValue, new ToStringConverter<Object>() {

                                    @Override
                                    public String toString(Object value) {
                                        return value.toString();
                                    }
                                }));
                            } else {
                                builder.appendHtmlConstant(memberValue.toString());
                            }

                            return builder.toSafeHtml();
                        }
                    };
                } else if (member instanceof IPrimitive<?>) {
                    if (mm.getValueClass().equals(LogicalDate.class)) {
                        if (!Context.userPreferences(IUserPreferences.class).logicalDateFormat().isNull()) {
                            formatter = new IFormatter<IEntity, SafeHtml>() {

                                @Override
                                public SafeHtml format(IEntity value) {
                                    SafeHtmlBuilder builder = new SafeHtmlBuilder();
                                    Date memberValue = (Date) ((IPrimitive<?>) value.getMember(member.getPath())).getValue();
                                    if (memberValue != null) {
                                        builder.appendHtmlConstant(DateTimeFormat.getFormat(
                                                Context.userPreferences(IUserPreferences.class).logicalDateFormat().getValue()).format(memberValue));
                                    }
                                    return builder.toSafeHtml();
                                }
                            };
                        }
                    } else if (mm.getValueClass().equals(Date.class)) {
                        if (!Context.userPreferences(IUserPreferences.class).dateTimeFormat().isNull()) {
                            formatter = new IFormatter<IEntity, SafeHtml>() {

                                @Override
                                public SafeHtml format(IEntity value) {
                                    SafeHtmlBuilder builder = new SafeHtmlBuilder();
                                    Date memberValue = (Date) ((IPrimitive<?>) value.getMember(member.getPath())).getValue();
                                    if (memberValue != null) {
                                        builder.appendHtmlConstant(DateTimeFormat.getFormat(
                                                Context.userPreferences(IUserPreferences.class).dateTimeFormat().getValue()).format(memberValue));
                                    }
                                    return builder.toSafeHtml();
                                }
                            };
                        }
                    }
                    formatter = new IFormatter<IEntity, SafeHtml>() {

                        @Override
                        public SafeHtml format(IEntity value) {
                            SafeHtmlBuilder builder = new SafeHtmlBuilder();
                            builder.appendHtmlConstant(((IPrimitive<?>) value.getMember(member.getPath())).getStringView());
                            return builder.toSafeHtml();
                        }
                    };
                } else {
                    formatter = new IFormatter<IEntity, SafeHtml>() {

                        @Override
                        public SafeHtml format(IEntity value) {
                            Object memberValue = value.getMember(member.getPath()).getValue();
                            SafeHtmlBuilder builder = new SafeHtmlBuilder();
                            if (memberValue != null) {
                                builder.appendHtmlConstant(memberValue.toString());
                            }
                            return builder.toSafeHtml();
                        }
                    };
                }
            }
            return new MemberColumnDescriptor(this);
        }
    }
}
