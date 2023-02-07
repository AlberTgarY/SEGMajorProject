package main.java.com.projectBackEnd.Services.AppInfo;

/**
 * Manager Interface for the AppInfoManagers and what they should be able to do.
 */
public interface AppInfoManagerInterface {

    void updateInfo(AppInfo updatedVersion);

    AppInfo getInfo();
}
