/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 28, 2009
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.site.shared.domain;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

public interface PageDescriptor extends IEntity<PageDescriptor> {

    IPrimitive<String, Page> name();

    IPrimitive<String, Page> linkHtml();

}
