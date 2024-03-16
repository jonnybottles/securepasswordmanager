package com.jgm.securepasswordmanager.datamodel;

public class WebsiteCredential {
    private String webSiteName;
    private String webSiteUserName;
    private String webSitePassword;
    private String notes;

    public WebsiteCredential(String webSiteName, String webSiteUserName, String webSitePassword, String notes) {
        this.webSiteName = webSiteName;
        this.webSiteUserName = webSiteUserName;
        this.webSitePassword = webSitePassword;
        this.notes = notes;
    }




}
