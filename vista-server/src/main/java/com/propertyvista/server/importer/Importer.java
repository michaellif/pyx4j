/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 20, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.server.importer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.portal.server.importer.XmlUtil;
import com.propertyvista.portal.server.importer.bean.Residential;
import com.pyx4j.gwt.server.IOUtils;

public class Importer {
	private static final Logger log = LoggerFactory.getLogger(Importer.class);

	public static void main(String[] args) {
		log.info("Importing new Data...");
		try {
			String xml = IOUtils.getTextResource(XmlUtil.resourceFileName(
					XmlUtil.class, "data.xml"));
			log.debug("Loaded " + xml);

			Residential residential = XmlUtil.unmarshallResidential(xml);
			log.info("Residential\n " + residential + "\n");

			Mapper mapper = new Mapper();
			mapper.load(residential);
		} catch (Exception e) {
			log.error("Problem with generating xml", e);
		}
	}
}
