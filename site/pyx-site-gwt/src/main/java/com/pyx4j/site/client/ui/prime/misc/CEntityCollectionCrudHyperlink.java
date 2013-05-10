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

import com.pyx4j.entity.shared.ICollection;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.IFormat;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;

public class CEntityCollectionCrudHyperlink<E extends ICollection<?, ?>> extends CHyperlink<E> {

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

    public CEntityCollectionCrudHyperlink(Class<? extends CrudAppPlace> placeClass) {
        this(new AppPlaceByOwnerBuilder<E>(placeClass));
    }

    public CEntityCollectionCrudHyperlink(AppPlace place) {
        this(new FixedAppPlaceBuilder<E>(place));
    }

    public CEntityCollectionCrudHyperlink(final AppPlaceBuilder<E> placeBuilder) {
        super((String) null);
        setCommand(new Command() {
            @Override
            public void execute() {
                AppPlace place = placeBuilder.createAppPlace(getValue());
                if (place != null) {
                    AppSite.getPlaceController().goTo(place);
                }
            }
        });
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
    }

    /**
     * Allow presentation update of the same entity when setValue is called
     */
    @Override
    public boolean isValuesEquals(E value1, E value2) {
        return value1 == value2;
    }

}