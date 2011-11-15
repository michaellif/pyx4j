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
package com.pyx4j.site.client.ui.crud.lister;

import java.util.List;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.client.ui.datatable.filter.DataTableFilterData;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.site.client.ui.crud.IView;
import com.pyx4j.site.rpc.CrudAppPlace;

public interface IListerView<E extends IEntity> extends IView<E> {

    public interface Presenter extends IView.Presenter {

        void setParentFiltering(Key parentID);

        List<DataTableFilterData> getPreDefinedFilters();

        void setPreDefinedFilters(List<DataTableFilterData> filters);

        void populate(int pageNumber);

        void view(Class<? extends CrudAppPlace> openPlaceClass, Key itemID);

        void edit(Class<? extends CrudAppPlace> openPlaceClass, Key itemID);

        void editNew(Class<? extends CrudAppPlace> openPlaceClass, Key parentID);

        void delete(Key itemID);
    }

    void setPresenter(Presenter presenter);

    Presenter getPresenter();

    ListerBase<E> getLister();

    int getPageSize();

    int getPageNumber();

    void populate(List<E> entityes, int pageNumber, boolean hasMoreData, int totalRows);

    List<DataTableFilterData> getFiltering();

    void setFiltering(List<DataTableFilterData> filterData);

    List<Sort> getSorting();

    void setSorting(List<Sort> sorts);
}
