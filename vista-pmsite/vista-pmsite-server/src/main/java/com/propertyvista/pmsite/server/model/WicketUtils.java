/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 15, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.pmsite.server.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.resource.TextTemplateResourceReference;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.convert.converter.DateConverter;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.StringValidator;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.pmsite.server.PMSiteApplication;

public class WicketUtils {
    private static I18n i18n = I18n.get(WicketUtils.class);

    /*
     * add/remove single class from component class attribute
     */
    public static class AttributeClassModifier extends AttributeModifier {
        private static final long serialVersionUID = 1L;

        private final String find;

        public AttributeClassModifier(String find, String replace) {
            super("class", new Model<String>(replace));
            this.find = find;
        }

        @Override
        protected String newValue(final String curVal, final String repVal) {
            if (curVal == null || curVal.equals("")) {
                return repVal;
            } else if (find == null || find.equals("")) {
                return curVal + " " + repVal;
            }
            StringBuffer newVal = new StringBuffer();
            String[] valArr = curVal.split(" ");
            for (int i = 0; i < valArr.length; i++) {
                newVal.append(i > 0 ? " " : "");
                if (valArr[i].equals(find)) {
                    newVal.append(repVal == null ? "" : repVal);
                } else {
                    newVal.append(valArr[i]);
                }
            }
            return newVal.toString();
        }
    }

    /*
     * Stateless Selector with keys as values and default choice switch
     */
    public static class DropDownList<T> extends DropDownChoice<T> {
        private static final long serialVersionUID = 1L;

        private boolean useDefault;

        private boolean useKeys;

        public DropDownList(String id, List<? extends T> choices, final boolean useKeys, final boolean useDefault) {
            super(id, choices, new IChoiceRenderer<T>() {
                private static final long serialVersionUID = 1L;

                @Override
                public String getDisplayValue(T param) {
                    return param.toString();
                }

                @Override
                public String getIdValue(T param, int paramInt) {
                    return useKeys ? String.valueOf(paramInt) : getDisplayValue(param);
                }
            });
            this.useDefault = useDefault;
            this.useKeys = useKeys;
        }

        @Override
        protected CharSequence getDefaultChoice(final String selected) {
            return useDefault ? super.getDefaultChoice(selected) : "";
        }

        @SuppressWarnings("unchecked")
        @Override
        protected T convertChoiceIdToChoice(String id) {
            if (useKeys) {
                // do default key-value conversion 
                return super.convertChoiceIdToChoice(id);
            } else {
                // just return the key, as we used it as value
                return (T) id;
            }
        }

        @Override
        public boolean getStatelessHint() {
            return true;
        }
    }

    /*
     * Stateless non-resource (external) image
     */
    public static class SimpleImage extends Image {
        private static final long serialVersionUID = 1L;

        public SimpleImage(String id, String url) {
            super(id);
            add(AttributeModifier.replace("src", url));
        }

        @Override
        public boolean getStatelessHint() {
            return true;
        }
    }

    public static class SimpleRadio<T> extends Radio<T> {
        private static final long serialVersionUID = 1L;

        public SimpleRadio(String id, IModel<T> model) {
            super(id, model);
        }

        @Override
        public boolean getStatelessHint() {
            return true;
        }

        @Override
        public String getValue() {
            return getParent().getId() + ":" + getId();
        }

    }

    public static class SimpleRadioGroup<T> extends RadioChoice<T> {
        private static final long serialVersionUID = 1L;

        private Map<T, String> valueMap;

        public SimpleRadioGroup(String id, IModel<T> model) {
            super(id);
            setModel(model);
            setChoiceRenderer(new ChoiceRenderer<T>() {
                private static final long serialVersionUID = 1L;

                @Override
                public String getDisplayValue(T param) {
                    String display = valueMap.get(param);
                    return display == null ? "" : display;
                }

                @Override
                public String getIdValue(T param, int paramInt) {
                    return String.valueOf(param);
                }
            });

            valueMap = new HashMap<T, String>();
        }

        public void addChoice(final T value, final String label) {
            valueMap.put(value, label);
        }

        @Override
        public void onBeforeRender() {
            setChoices(new ArrayList<T>(valueMap.keySet()));
            super.onBeforeRender();
        }
    }

    /*
     * TextTemplateResource is auto-registered in ResourceReferenceRegistry cache
     * by the ResourceReferenceRegistry constructor, if not exist, so no changes will
     * be visible after the app is loaded for the first time.
     * We need to remove and re-register it again to pickup possible changes from CRM
     * in sub-sequential requests.
     * NOTE: The Key construction is taken from the ResourceReference.java.
     * It should match the original key used by the TextTemplateResourceReference
     */
    public static class VolatileTemplateResourceReference extends TextTemplateResourceReference {
        private static final long serialVersionUID = 1L;

        public VolatileTemplateResourceReference(Class<?> scope, String fileName, String contentType, IModel<Map<String, Object>> vars) {
            super(scope, fileName, contentType, vars);
            Key rcKey = new Key(scope.getName(), fileName, null, null, null);
            WebApplication.get().getResourceReferenceRegistry().unregisterResourceReference(rcKey);
            WebApplication.get().getResourceReferenceRegistry().registerResourceReference(this);

        }
    }

    public static class LocalizedPageLink extends BookmarkablePageLink<Void> {
        private static final long serialVersionUID = 1L;

        public LocalizedPageLink(final String wicketId, final Class<? extends Page> pageClass, final PageParameters params, final String lang) {
            super(wicketId, pageClass, new PageParameters(params).set(PMSiteApplication.ParamNameLang, lang));
        }
    }

    /*
     * ActionLink is a version of a StatelessLink that is intended for running
     * a javascript action on the client with sub-sequential page reload.
     */
    public static class JSActionLink extends StatelessLink<Void> {
        private static final long serialVersionUID = 1L;

        public <C extends Page> JSActionLink(final String id, final String jsAction, final boolean reload) {
            super(id);
            String onClick = jsAction;
            onClick += ";" + (reload ? "window.location.reload()" : "return false");
            super.add(AttributeModifier.replace("onClick", onClick));
        }

        @Override
        public final void onClick() {
        }

        @Override
        protected CharSequence getURL() {
            return "";
        }
    }

    public static class PhoneValidator extends StringValidator {
        private static final long serialVersionUID = 1L;

        public PhoneValidator(int minDigits, boolean allowExt) {

        }

        @Override
        protected void onValidate(IValidatable<String> validatable) {
            final String value = validatable.getValue();
            // check value format (just as an example)
            if (!value.matches("\\d{10,15}")) {
                error(validatable);
            }
        }
    }

    public static class OneRequiredFormValidator extends AbstractFormValidator {
        private static final long serialVersionUID = 1L;

        FormComponent<?>[] checkList;

        public OneRequiredFormValidator(FormComponent<?>... compList) {
            for (FormComponent<?> fc : compList) {
                if (fc == null) {
                    throw new IllegalArgumentException("FormComponent cannot be null");
                }
            }
            if (compList.length < 2) {
                throw new IllegalArgumentException("A min of two FormComponents required");
            }
            checkList = compList;
        }

        @Override
        public FormComponent<?>[] getDependentFormComponents() {
            return checkList;
        }

        @Override
        public void validate(Form<?> form) {
            boolean valid = false;
            StringBuffer list = new StringBuffer();
            String first = null;
            for (FormComponent<?> fc : checkList) {
                if (fc.getConvertedInput() != null) {
                    valid = true;
                    break;
                }
                if (first == null) {
                    first = fc.getLabel().getObject();
                    continue;
                } else if (list.length() > 0) {
                    list.insert(0, ", ");
                }
                list.insert(0, fc.getLabel().getObject());
            }
            if (!valid) {
                form.error(SimpleMessageFormat.format(i18n.tr("Either one of {0} or {1} must be provided"), list, first), null);
            }
        }
    }

    public static class DateInput extends TextField<LogicalDate> {
        private static final long serialVersionUID = 1L;

        public class LogicalDateConverter implements IConverter<LogicalDate> {
            private static final long serialVersionUID = 1L;

            @Override
            @SuppressWarnings("deprecation")
            public String convertToString(LogicalDate value, Locale locale) {
                return new DateConverter().convertToString(new Date(value.getYear(), value.getMonth(), value.getDate()), locale);
            }

            @Override
            public LogicalDate convertToObject(String value, Locale locale) {
                return new LogicalDate(new DateConverter().convertToObject(value, locale));
            }
        }

        public DateInput(String id, IModel<LogicalDate> model) {
            super(id, model);
        }

        public DateInput(String id) {
            super(id);
        }

        @SuppressWarnings("unchecked")
        @Override
        public final <C> IConverter<C> getConverter(Class<C> clazz) {
            if (LogicalDate.class.isAssignableFrom(clazz)) {
                return (IConverter<C>) new LogicalDateConverter();
            } else {
                return super.getConverter(clazz);
            }
        }
    }
}
