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
 * Created on Sep 17, 2012
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.ui.prime.misc;

import com.google.gwt.user.client.Command;

import com.pyx4j.commons.IFormat;
import com.pyx4j.entity.core.ICollection;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;

public class CEntityCollectionCrudHyperlink<E extends ICollection<?, ?>> extends CLabel<E> {

    public interface AppPlaceBuilder<E extends ICollection<?, ?>> {

        AppPlace createAppPlace(E value);

    }

    public static class AppPlaceByOwnerBuilder<E extends ICollection<?, ?>> implements AppPlaceBuilder<E> {

        final Class<? extends CrudAppPlace> placeClass;

        public AppPlaceByOwnerBuilder(final Class<? extends CrudAppPlace> placeClass) {
            this.placeClass = placeClass;
        }

        @Override
        public AppPlace createAppPlace(E value) {
            if (value.getOwner().getPrimaryKey() != null) {
                CrudAppPlace place = AppSite.getHistoryMapper().createPlace(placeClass);
                return place.formListerPlace(value.getOwner().getPrimaryKey());
            } else {
                return null;
            }
        }

    }

    public static class FixedAppPlaceBuilder<E extends ICollection<?, ?>> implements AppPlaceBuilder<E> {

        AppPlace place;

        public FixedAppPlaceBuilder(final AppPlace place) {
            this.place = place;
        }

        @Override
        public AppPlace createAppPlace(E value) {
            return place;
        }

    }

    private final AppPlaceBuilder<E> placeBuilder;

    public CEntityCollectionCrudHyperlink(Class<? extends CrudAppPlace> placeClass) {
        this(new AppPlaceByOwnerBuilder<E>(placeClass));
    }

    public CEntityCollectionCrudHyperlink(AppPlace place) {
        this(new FixedAppPlaceBuilder<E>(place));
    }

    public CEntityCollectionCrudHyperlink(final AppPlaceBuilder<E> placeBuilder) {
        super();
        this.placeBuilder = placeBuilder;
        setFormat(new IFormat<E>() {
            @Override
            public String format(E value) {
                if (value != null) {
                    return value.size() + "";
                } else {
                    return null;
                }
            }

            @Override
            public E parse(String string) {
                return null;
            }
        });

    }

    @Override
    protected void onValueSet(boolean populate) {
        this.setEnabled((getValue() != null) && (getValue().getOwner().getPrimaryKey() != null));

        Command onClick = null;
        if (getValue() != null && getValue().size() > 0) {
            onClick = new Command() {
                @Override
                public void execute() {
                    AppPlace place = placeBuilder.createAppPlace(getValue());
                    if (place != null) {
                        AppSite.getPlaceController().goTo(place);
                    }
                }
            };
        }
        setNavigationCommand(onClick);
    }

    /**
     * Allow presentation update of the same entity when setValue is called
     */
    @Override
    public boolean isValuesEquals(E value1, E value2) {
        return value1 == value2;
    }

    @Override
    protected String getDebugInfo() {
        //TODO overrides CField.getDebugInfo() because of error - investigate
        //  java.lang.ClassCastException: java.lang.Integer cannot be cast to java.util.Set
        //  at com.pyx4j.entity.core.impl.SetHandler.getValue(SetHandler.java:90)
        //  at com.pyx4j.entity.core.impl.SetHandler.getValue(SetHandler.java:1)
        //  at com.pyx4j.entity.core.impl.AbstractCollectionHandler.toString(AbstractCollectionHandler.java:274)
        //  at java.lang.String.valueOf(String.java:2854)
        //  at java.lang.StringBuilder.append(StringBuilder.java:128)
        //  at com.pyx4j.forms.client.ui.CField.getDebugInfo(CField.java:156)
        return "";
    }
}