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
 * Created on Jan 11, 2010
 * @author Michael
 */
package com.pyx4j.forms.client.ui;

import java.text.ParseException;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.IParser;
import com.pyx4j.commons.PersonalIdentityFormatter;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.shared.IPersonalIdentity;
import com.pyx4j.forms.client.validators.RegexValidator;
import com.pyx4j.i18n.shared.I18n;

/**
 * This field is used to protect personal identity by hiding part of ID
 *
 * NOTE - It's made generic to allow for properly typed new entity creation (see CPersonalIdentityField#parse())
 * This however should not be considered as a final solution as the new entity may also require some additional
 * business-related initialization steps...
 */
public class CPersonalIdentityField<T extends IPersonalIdentity> extends CTextFieldBase<T, NPersonalIdentityField<T>> {

    private static final I18n i18n = I18n.get(CPersonalIdentityField.class);

    public interface IPersonalIdentityFormat<V extends IPersonalIdentity> extends IFormatter<V, String> {

        String obfuscate(String data);

    }

    private final Class<T> entityClass;

    public CPersonalIdentityField(Class<T> entityClass) {
        this(entityClass, null);
    }

    public CPersonalIdentityField(Class<T> entityClass, PersonalIdentityFormatter formatter) {
        this(entityClass, formatter, false);
    }

    public CPersonalIdentityField(Class<T> entityClass, PersonalIdentityFormatter formatter, boolean mandatory) {
        super();
        this.entityClass = entityClass;
        setMandatory(mandatory);
        setPersonalIdentityFormatter(formatter);
        setNativeComponent(new NPersonalIdentityField<T>(this));
    }

    public void setPersonalIdentityFormatter(PersonalIdentityFormatter formatter) {
        setFormatter(new PersonalIdentityFormat<T>(formatter));
        setParser(new PersonalIdentityParser<T>(this, formatter));
    }

    public void addRegexValidator(String regex, String regexValidationMessage) {
        this.addComponentValidator(new RegexValidator<T>(regex, regexValidationMessage));
    }

    @Override
    public boolean isValueEmpty() {
        if (!getNativeComponent().isParsedOk()) {
            return false;
        }
        return getValue() == null || getValue().isNull();
    }

    @Override
    public void onEditingStop() {
        if (getNativeComponent().getEditor().isParsedOk() && getValue() != null) {
            // clear obfuscated value to indicate user input
            getValue().obfuscatedNumber().setValue(null);
        }
    }

    // catch exception here to avoid null-value set by CComponent#update(null)
    @Override
    protected T getEditorValue() throws ParseException {
        T value;
        try {
            value = super.getEditorValue();
            if (value == null) {
                value = getValue();
            }
        } catch (Throwable e) {
            // don't clear native value so that format validator could fail properly
            clear(false);
            value = getValue();
        }
        // return a copy so that CComponent#isValuesEqual() gets different object
        return (value == null ? value : value.<T> duplicate());
    }

    public void clear(boolean clearNative) {
        if (clearNative) {
            getNativeComponent().getEditor().setValue(null);
        }
        if (getValue() != null) {
            getValue().newNumber().setValue(null);
            getValue().obfuscatedNumber().setValue(null);
        }
    }

    public void postprocess() {
        IPersonalIdentity value = getValue();
        if (value != null && !value.newNumber().isNull()) {
            value.obfuscatedNumber().setValue(((IPersonalIdentityFormat<T>) getFormatter()).obfuscate(value.newNumber().getValue()));
            value.newNumber().setValue(null);
        }
    }

    @Override
    public void setValueByString(String name) {
        if (getValue() != null) {
            // clear obfuscatedNumber to indicate new input
            getValue().obfuscatedNumber().setValue(null);
        }
        super.setValueByString(name);
    }

    private static class PersonalIdentityFormat<E extends IPersonalIdentity> implements IPersonalIdentityFormat<E> {

        final PersonalIdentityFormatter formatter;

        public PersonalIdentityFormat(PersonalIdentityFormatter formatter) {
            this.formatter = formatter;
        }

        @Override
        public String format(IPersonalIdentity value) {
            if (value == null) {
                return "";
            }
            boolean obfuscate = value.newNumber().isNull();
            String input = obfuscate ? value.obfuscatedNumber().getValue() : value.newNumber().getValue();
            return formatter.format(input, obfuscate);
        }

        @Override
        public String obfuscate(String data) {
            return formatter.obfuscate(data);
        }

    }

    private static class PersonalIdentityParser<E extends IPersonalIdentity> extends PersonalIdentityFormat<E> implements IParser<E> {

        private final CPersonalIdentityField<E> component;

        public PersonalIdentityParser(CPersonalIdentityField<E> component, PersonalIdentityFormatter formatter) {
            super(formatter);
            this.component = component;
        }

        @Override
        public E parse(String string) throws ParseException {
            E value = EntityFactory.create(component.entityClass);
            if (CommonsStringUtils.isEmpty(string)) {
                // clear value on empty user input
                if (value != null && value.obfuscatedNumber().isNull()) {
                    value.newNumber().setValue(null);
                }
            } else {
                // non-empty string could be either new user input or obfuscated value (formatted) of existing entity
                if (!formatter.isValidInput(string)) {
                    throw new ParseException(i18n.tr("Invalid value format."), 0);
                }
                if (value == null) {
                    // this should never happen
                    throw new Error("Value is null");
                }
                // populate resulting value
                String data = formatter.inputFilter(string);
                if (value.obfuscatedNumber().isNull()) {
                    // if no obfuscated value then we are getting new user input
                    value.newNumber().setValue(data);
                    value.obfuscatedNumber().setValue(null);
                } else {
                    value.newNumber().setValue(null);
                    value.obfuscatedNumber().setValue(data);
                }
            }
            return value;
        }
    }

}
