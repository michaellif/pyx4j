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
 * Created on Sep 18, 2015
 * @author vlads
 */
package com.pyx4j.entity.rdb.dialect;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.NamedThreadFactory;
import com.pyx4j.log4j.LoggerConfig;

public class AsynchronousConnectionsDestoryer {

    private static Logger log = LoggerFactory.getLogger(AsynchronousConnectionsDestoryer.class);

    private ExecutorService abortExecutorService;

    private ExecutorService tryColseExecutorService;

    private AsynchronousConnectionsDestoryer() {
        tryColseExecutorService = Executors.newFixedThreadPool(5,
                new NamedThreadFactory(LoggerConfig.getContextName() + "-AsynchronousConnectionsDestoryer", "tryColse"));

        abortExecutorService = Executors.newFixedThreadPool(5,
                new NamedThreadFactory(LoggerConfig.getContextName() + "-AsynchronousConnectionsDestoryer", "abort"));

        // TODO Register system shutdown.
    }

    private static class SingletonHolder {
        public static final AsynchronousConnectionsDestoryer INSTANCE = new AsynchronousConnectionsDestoryer();
    }

    public static AsynchronousConnectionsDestoryer instance() {
        return SingletonHolder.INSTANCE;
    }

    public void destroyConnection(final Connection connection) throws SQLException {
        // Execute close with timeout, if timeout happens abort connection
        Future<Void> result = tryColseExecutorService.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                connection.close();
                return null;
            }
        });

        try {
            result.get(5, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            log.error("unable to close connection in timely manner, abort");
            connection.abort(abortExecutorService);
        } catch (ExecutionException | InterruptedException ignore) {
        }
    }

    public void shutdown() {
        tryColseExecutorService.shutdownNow();
        abortExecutorService.shutdownNow();
    }
}
