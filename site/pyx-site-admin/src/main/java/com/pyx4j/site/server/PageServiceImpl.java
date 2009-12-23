package com.pyx4j.site.server;

import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import com.google.appengine.api.datastore.Text;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import com.pyx4j.site.admin.PageService;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class PageServiceImpl extends RemoteServiceServlet implements PageService {

    private static final PersistenceManagerFactory pmfInstance = JDOHelper.getPersistenceManagerFactory("transactions-optional");

    public String getPageHTML(String pageName) {
        PageData page = loadPage(pageName, pmfInstance.getPersistenceManager());
        if (page != null) {
            return page.getHtml().getValue();
        } else {
            return "ERROR: Page " + pageName + " not found";
        }

    }

    public void setPageHTML(String pageName, String html) {
        PersistenceManager pm = pmfInstance.getPersistenceManager();
        PageData page = loadPage(pageName, pm);
        if (page == null) {
            page = new PageData(pageName, new Text(html));
        } else {
            page.setHtml(new Text(html));
        }
        try {
            pm.makePersistent(page);
        } finally {
            pm.close();
        }
    }

    private PageData loadPage(String pageName, PersistenceManager pm) {
        String query = "select from " + PageData.class.getName() + " where pageName == " + pageName;
        List<PageData> pages = (List<PageData>) pm.newQuery(query).execute();
        if (pages != null && pages.size() == 1) {
            return pages.get(0);
        } else {
            return null;
        }
    }

}
