/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Jan 9, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.log4gwt.rpc;

import java.util.Vector;

import com.pyx4j.log4gwt.shared.LogEvent;
import com.pyx4j.rpc.shared.Service;
import com.pyx4j.rpc.shared.VoidSerializable;

public interface LogServices {

    public interface Log extends Service<Vector<LogEvent>, VoidSerializable> {

    };

}
