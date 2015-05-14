/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on May 14, 2015
 * @author vlads
 */
package com.pyx4j.entity.rdb.postgresql.textsearch;

import java.util.List;

import org.junit.Assert;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.rdb.PersistenceEnvironmentFactory;
import com.pyx4j.entity.server.textsearch.TextSearchFacade;
import com.pyx4j.entity.server.textsearch.TextSearchFacade.KeywordUpdateRule;
import com.pyx4j.entity.test.server.DatastoreTestBase;
import com.pyx4j.entity.test.server.PersistenceEnvironment;
import com.pyx4j.entity.test.shared.domain.fts.FtsAuthor;
import com.pyx4j.entity.test.shared.domain.fts.FtsBook;
import com.pyx4j.entity.test.shared.domain.fts.FtsBookIndex;

public class FullTextSearchTest extends DatastoreTestBase {

    @Override
    protected PersistenceEnvironment getPersistenceEnvironment() {
        return PersistenceEnvironmentFactory.getPostgreSQLPersistenceEnvironment();
    }

    public static class BookKeywordUpdateRule implements KeywordUpdateRule<FtsBook> {

        @Override
        public String buildIndex(FtsBook book) {
            return book.getStringView() + " " + book.authors().getStringView();
        }

    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        ServerSideFactory.create(TextSearchFacade.class).registerUpdateRule(FtsBookIndex.class, BookKeywordUpdateRule.class);
        ServerSideFactory.create(TextSearchFacade.class).registerUpdateChain(FtsAuthor.class, FtsBookIndex.class,
                new TextSearchFacade.UpdateChain<FtsAuthor, FtsBook>() {

                    @Override
                    public EntityQueryCriteria<FtsBook> criteria(FtsAuthor triggerEntity) {
                        EntityQueryCriteria<FtsBook> criteria = EntityQueryCriteria.create(FtsBook.class);
                        criteria.eq(criteria.proto().authors(), triggerEntity);
                        return criteria;
                    }

                });

    }

    public void testTextQuery() {
        String testId = uniqueString();

        final int dataSize = 3;
        for (int i = 0; i < dataSize; i++) {

            FtsAuthor author = EntityFactory.create(FtsAuthor.class);
            author.testId().setValue(testId);
            author.lastName().setValue("LN" + i);
            author.firstName().setValue("FN" + i);
            srv.persist(author);

            FtsBook book = EntityFactory.create(FtsBook.class);
            book.testId().setValue(testId);
            book.title().setValue("BT" + i);
            book.authors().add(author);
            srv.persist(book);
        }

        {
            EntityQueryCriteria<FtsBook> criteria = EntityQueryCriteria.create(FtsBook.class);
            criteria.eq(criteria.proto().testId(), testId);

            criteria.eq(criteria.proto().fts().keywords(), "FN1 BT1");

            List<FtsBook> retrived = srv.query(criteria);
            Assert.assertEquals("result set size", 1, retrived.size());
        }

    }
}
