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
 * Created on 2011-01-20
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.geo;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.maps.client.geocode.LatLngCallback;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.GeoCriteria;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.events.HasAsyncValue;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.ui.IAcceptText;
import com.pyx4j.forms.client.ui.IFormat;
import com.pyx4j.forms.client.ui.NativeTextBox;
import com.pyx4j.gwt.commons.UnrecoverableClientWarning;
import com.pyx4j.gwt.geo.MapUtils;

public class CLocationCriteriaTextField extends CTextFieldBase<GeoCriteria, NativeTextBox<GeoCriteria>> implements HasAsyncValue<GeoCriteria>, IAcceptText {

    private static I18n i18n = I18nFactory.getI18n(CLocationCriteriaTextField.class);

    public CLocationCriteriaTextField() {
        this(null);
    }

    public CLocationCriteriaTextField(String title) {
        super(title);
        setFormat(new GeoCriteriaFormat());
    }

    @Override
    protected NativeTextBox<GeoCriteria> createWidget() {
        return new NativeTextBox<GeoCriteria>(this);
    }

    @Override
    public void setValue(GeoCriteria value) {
        // merge the radius value
        GeoCriteria orig = getValue();
        if ((orig != null) && (value != null)) {
            value.radius().setValue(orig.radius().getValue());
        }
        super.setValue(value);
    }

    @Override
    public void setValueByString(String name) {
        GeoCriteria value = getValue();
        if (value == null) {
            value = EntityFactory.create(GeoCriteria.class);
        } else {
            value = (GeoCriteria) value.cloneEntity();
        }
        value.location().setValue(name);
        setValue(value);
    }

    @Override
    public boolean isValueEmpty() {
        if (super.isValueEmpty() || getValue().isNull()) {
            return true;
        } else {
            return getValue().location().isNull();
        }
    }

    private static class GeoCriteriaFormat implements IFormat<GeoCriteria> {

        @Override
        public String format(GeoCriteria value) {
            return value.location().getStringView();
        }

        @Override
        public GeoCriteria parse(String string) {
            GeoCriteria entity = EntityFactory.create(GeoCriteria.class);
            entity.location().setValue(string);
            return entity;
        }

    }

    @Override
    public boolean isAsyncValue() {
        return !isValueEmpty();
    }

    @Override
    public void obtainValue(final AsyncCallback<GeoCriteria> callback) {
        MapUtils.obtainLatLang(getValue().location().getStringView(), new LatLngCallback() {

            @Override
            public void onSuccess(LatLng fromCoordinates) {
                GeoCriteria value = (GeoCriteria) getValue().cloneEntity();
                value.geoPoint().setValue(MapUtils.newGeoPointInstance(fromCoordinates));
                setValue(value);
                callback.onSuccess(value);
            }

            @Override
            public void onFailure() {
                callback.onFailure(new UnrecoverableClientWarning(i18n.tr("We are unable to find location.")));
            }
        });

    }
}
