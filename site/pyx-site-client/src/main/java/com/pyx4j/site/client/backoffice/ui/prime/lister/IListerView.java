/*
 * Pyx4j framework
 * Copyright (C) 2006-2010 pyx4j.com.
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
 * Created on 2011-05-03
 * @author Vlad
 * @version $Id$
 */
package com.pyx4j.site.client.backoffice.ui.prime.lister;

import java.util.List;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractCrudService.InitializationData;
import com.pyx4j.forms.client.ui.datatable.ListerDataSource;
import com.pyx4j.site.client.backoffice.ui.prime.IPrimePaneView;
import com.pyx4j.site.client.backoffice.ui.prime.IPrimePaneView.IPrimePanePresenter;
import com.pyx4j.site.client.backoffice.ui.prime.lister.IListerView.IListerPresenter;
import com.pyx4j.site.rpc.CrudAppPlace;

public interface IListerView<E extends IEntity> extends IPrimePaneView<IListerPresenter<E>> {

    public interface IListerPresenter<E extends IEntity> extends IPrimePanePresenter {

        public ListerDataSource<E> getDataSource();

        Key getParent();

        Class<? extends IEntity> getParentClass();

        void setParent(Key parentID);

        void setParent(Key parentID, Class<? extends IEntity> parentClass);

        void setPreDefinedFilters(List<Criterion> filters);

        void addPreDefinedFilters(List<Criterion> filters);

        void addPreDefinedFilter(Criterion filter);

        void clearPreDefinedFilters();

        void view(Class<? extends CrudAppPlace> openPlaceClass, Key itemID);

        void edit(Class<? extends CrudAppPlace> openPlaceClass, Key itemID);

        void editNew(Class<? extends CrudAppPlace> openPlaceClass);

        void editNew(Class<? extends CrudAppPlace> openPlaceClass, InitializationData initializationData);

        /**
         * Should be called after populate, return value is security feature
         */
        boolean canCreateNewItem();

        void delete(Key itemID);
    }

    EntityDataTablePanel<E> getDataTablePanel();

    int getPageSize();

    int getPageNumber();

    void discard();

    List<Criterion> getFilters();

    void setFilters(List<Criterion> filterData);

    List<Sort> getSortCriteria();

    void setSortCriteria(List<Sort> sorts);

    void onDeleted(Key itemID, boolean isSuccessful);

}