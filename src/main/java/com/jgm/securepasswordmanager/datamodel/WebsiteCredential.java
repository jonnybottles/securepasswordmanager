package com.jgm.securepasswordmanager.datamodel;

import java.util.Objects;

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


    public String getWebSiteName() {
        return webSiteName;
    }

    public void setWebSiteName(String webSiteName) {
        this.webSiteName = webSiteName;
    }

    public String getWebSiteUserName() {
        return webSiteUserName;
    }

    public void setWebSiteUserName(String webSiteUserName) {
        this.webSiteUserName = webSiteUserName;
    }

    public String getWebSitePassword() {
        return webSitePassword;
    }

    public void setWebSitePassword(String webSitePassword) {
        this.webSitePassword = webSitePassword;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebsiteCredential that = (WebsiteCredential) o;
        return Objects.equals(webSiteName, that.webSiteName) &&
                Objects.equals(webSiteUserName, that.webSiteUserName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(webSiteName, webSiteUserName);
    }

    @Override
    public String toString() {
        return
                " - webSiteName='" + webSiteName + '\'' + "\n" +
                " - webSiteUserName='" + webSiteUserName + '\'' + "\n" +
                " - webSitePassword='" + webSitePassword + '\'' + "\n" +
                " - notes='" + notes + '\'' + "\n";
    }
}
