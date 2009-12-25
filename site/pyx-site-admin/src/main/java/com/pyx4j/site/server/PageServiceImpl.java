package com.pyx4j.site.server;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import com.pyx4j.site.admin.PageService;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class PageServiceImpl extends RemoteServiceServlet implements PageService {

    public void savePageHtml(String pageName, String html) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Key key = KeyFactory.createKey("Page", pageName);

        Entity entity = new Entity(key);
        entity.setProperty("html", html);

        datastore.put(entity);

    }

    public String loadPageHtml(String pageName) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Key key = KeyFactory.createKey("Page", pageName);

        Entity entity;
        try {
            entity = datastore.get(key);
            return (String) entity.getProperty("html");
        } catch (EntityNotFoundException e) {
            return "ERROR: Page not found";
        }

    }
}
