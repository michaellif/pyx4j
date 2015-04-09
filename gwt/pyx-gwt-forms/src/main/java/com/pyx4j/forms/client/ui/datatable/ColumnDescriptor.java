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
 */
package com.pyx4j.forms.client.ui.datatable;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import com.pyx4j.commons.ConverterUtils;
import com.pyx4j.commons.ConverterUtils.ToStringConverter;
import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.Key;
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

public class ColumnDescriptor {

    public static final String DEFAULT_WIDTH = "100px";

    private final Builder builder;

    protected ColumnDescriptor(Builder builder) {
        this.builder = builder;
    }

    public Path getColumnPath() {
        return builder.member.getPath();
    }

    public String getColumnName() {
        return getColumnPath().toString();
    }

    public boolean isSearchable() {
        Class<?> valueClass = builder.member.getValueClass();
        if (builder.member.getMeta().isEntity()) {
            return builder.searchable;
        } else if (valueClass.isEnum() || valueClass.equals(Boolean.class)) {
            return builder.searchable;
        } else if (valueClass.equals(String.class)) {
            return builder.searchable;
        } else if (valueClass.equals(Date.class) || valueClass.equals(java.sql.Date.class) || valueClass.equals(LogicalDate.class)) {
            return builder.searchable;
        } else if (valueClass.equals(BigDecimal.class) || valueClass.equals(Key.class) || builder.member.getMeta().isNumberValueClass()) {
            return builder.searchable;
        }
        return false;
    }

    public void setSearchable(boolean searchable) {
        builder.searchable = searchable;
    }

    public boolean isFilterAlwaysShown() {
        return builder.filterAlwaysShown;
    }

    public boolean isSortable() {
        return builder.sortable;
    }

    public void setSortable(boolean sortable) {
        builder.sortable = sortable;
    }

    public boolean isSearchableOnly() {
        return builder.searchableOnly;
    }

    public String getColumnTitle() {
        return builder.columnTitle;
    }

    public boolean isColumnTitleShown() {
        return builder.columnTitleShown;
    }

    public void setColumnTitle(String columnTitle) {
        builder.columnTitle = columnTitle;
    }

    public String getWidth() {
        return builder.width;
    }

    public void setWidth(String width) {
        builder.width = width;
    }

    public boolean isVisible() {
        return builder.visible;
    }

    public void setVisible(boolean visible) {
        builder.visible = visible;
    }

    public IFormatter<IEntity, SafeHtml> getFormatter() {
        return builder.formatter;
    }

    public void setFormatter(IFormatter<IEntity, SafeHtml> formatter) {
        builder.formatter = formatter;
    }

    public IObject<?> getMemeber() {
        return builder.member;
    }

    protected Builder getBuilder() {
        return builder;
    }

    @Override
    public String toString() {
        return getColumnName() + "[" + getColumnTitle() + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ColumnDescriptor) {
            return builder.member.getPath().equals(((ColumnDescriptor) obj).builder.member.getPath());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        if (builder.member.getPath() != null) {
            return builder.member.getPath().hashCode();
        } else {
            return super.hashCode();
        }
    }

    public static class Builder {

        private final IObject<?> member;

        private String columnTitle;

        private boolean columnTitleShown = true;

        private boolean sortable = true;

        private boolean searchable = true;

        private boolean filterAlwaysShown = false;

        private boolean searchableOnly = false;

        private String width = DEFAULT_WIDTH;

        private boolean visible = true;

        IFormatter<IEntity, SafeHtml> formatter;

        public Builder(IObject<?> member) {
            this.member = member;
            columnTitle = member.getMeta().getCaption();

            if (EnumSet.of(ObjectClassType.EntityList, ObjectClassType.EntitySet).contains(member.getMeta().getObjectClassType())) {
                this.sortable(false);
            }
        }

        public Builder(IObject<?> member, boolean visible) {
            this(member);
            visible(visible);
        }

        public Builder columnTitle(String columnTitle) {
            this.columnTitle = columnTitle;
            return this;
        }

        public Builder columnTitleShown(boolean columnTitleShown) {
            this.columnTitleShown = columnTitleShown;
            return this;
        }

        public Builder titlePrefix(String columnTitlePrefix) {
            this.columnTitle = columnTitlePrefix + " " + columnTitle;
            return this;
        }

        public Builder sortable(boolean sortable) {
            this.sortable = sortable;
            return this;
        }

        public Builder searchable(boolean searchable) {
            this.searchable = searchable;
            return this;
        }

        public Builder filterAlwaysShown(boolean filterAlwaysShown) {
            this.filterAlwaysShown = filterAlwaysShown;
            return this;
        }

        public Builder searchableOnly() {
            this.searchable = true;
            this.searchableOnly = true;
            this.visible = false;
            return this;
        }

        public Builder displayOnly() {
            sortable(false);
            searchable(false);
            return this;
        }

        public Builder width(String width) {
            this.width = width;
            return this;
        }

        public Builder visible(boolean visible) {
            this.visible = visible;
            return this;
        }

        public Builder formatter(IFormatter<IEntity, SafeHtml> formatter) {
            this.formatter = formatter;
            return this;
        }

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
            return new ColumnDescriptor(this);
        }
    }
}
