/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 9, 2015
 * @author ernestog
 */
package com.propertyvista.biz.preloader;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OutputHolder implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(OutputHolder.class);

    boolean pipeBroken = false;

    OutputStream out;

    public OutputHolder(OutputStream out) {
        this.out = out;
    }

    public void o(String... messages) throws IOException {
        if (this.pipeBroken) {
            return;
        }

        try {
            out.write("<pre>".getBytes());
            for (String message : messages) {
                out.write(message.getBytes());
            }

            out.write("</pre>".getBytes());
            out.flush();
        } catch (Throwable e) {
            log.error("db-reset out put error", e);
            log.error("db-reset will continue");
            pipeBroken = true;
        }
    }

    public void h(String... messages) throws IOException {
        if (pipeBroken) {
            return;
        }
        try {
            for (String message : messages) {
                out.write(message.getBytes());
            }
            out.flush();
        } catch (Throwable e) {
            log.error("db-reset out put error", e);
            pipeBroken = true;
        }
    }

    @Override
    public void close() throws IOException {
        if (this.out != null) {
            this.out.close();
            this.out = null;
        }
    }

}