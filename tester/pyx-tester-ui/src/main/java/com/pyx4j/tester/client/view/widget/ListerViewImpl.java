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
 * Created on Oct 25, 201
 * @author stanp
 * @version $Id$
 */
package com.pyx4j.tester.client.view.widget;

import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.site.client.ui.prime.lister.EntityDataTablePanel;
import com.pyx4j.site.client.ui.prime.lister.ListerDataSource;
import com.pyx4j.tester.client.domain.test.DomainFactory;
import com.pyx4j.tester.client.domain.test.ListerDataItem;

public class ListerViewImpl extends ScrollPanel implements ListerView {

    private final TestLister lister = new TestLister();

    public ListerViewImpl() {
        setSize("100%", "100%");

        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();

        int row = 0;

        content.setH1(row++, 0, 1, "Settings");
        content.setWidget(row++, 0, new HTML("Container: width = 100px, height = 100px"));
        content.setWidget(row++, 0, new HTML("Page Rows = 10"));
        content.setWidget(row++, 0, new HTML("Data: cols = 5, rows = 5"));

        content.setH1(row++, 0, 1, "Lister");
        content.setWidget(row++, 0, lister);

        add(content);
    }

    class TestLister extends EntityDataTablePanel<ListerDataItem> {
        final int MAX_COLS = 10;

        public TestLister() {
            super(ListerDataItem.class);

            populate(5, 20);
        }

        public void populate(int cols, int rows) {
            if (cols > MAX_COLS) {
                cols = MAX_COLS;
            }

            ColumnDescriptor[] columns = new ColumnDescriptor[cols];
            for (int col = 0; col < cols; col++) {
                columns[col] = new MemberColumnDescriptor.Builder(proto().getMember("field" + col), true).build();
            }
            setColumnDescriptors(columns);
            setDataSource(new TestListerDataSource(cols, rows));
            obtain(0);
        }
    }

    class TestListerDataSource extends ListerDataSource<ListerDataItem> {
        private final List<ListerDataItem> data;

        public TestListerDataSource(int cols, int rows) {
            super(ListerDataItem.class, null);
            this.data = DomainFactory.createListerData(cols, rows);
        }

        @Override
        public void obtain(EntityQueryCriteria<ListerDataItem> criteria, final AsyncCallback<EntitySearchResult<ListerDataItem>> handlingCallback) {
            EntitySearchResult<ListerDataItem> result = new EntitySearchResult<ListerDataItem>();
            // use data to populate result
            int dataSize = data.size();
            EntityListCriteria<ListerDataItem> lc = (EntityListCriteria<ListerDataItem>) criteria;
            int idxFrom = lc.getPageNumber() * lc.getPageSize();
            int idxTo = idxFrom + lc.getPageSize();
            result.setData(new Vector<ListerDataItem>(data.subList(idxFrom, idxTo < dataSize ? idxTo : dataSize)));
            result.setTotalRows(dataSize);
            result.hasMoreData(idxFrom + result.getTotalRows() < dataSize);
            handlingCallback.onSuccess(result);
        }
    }
}
