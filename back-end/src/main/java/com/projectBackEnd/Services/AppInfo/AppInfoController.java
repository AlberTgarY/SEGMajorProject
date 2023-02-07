package main.java.com.projectBackEnd.Services.AppInfo;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;

import main.java.com.projectBackEnd.Services.Session.SessionManager;
import main.java.com.projectBackEnd.Services.Session.SessionManagerInterface;

import static main.java.com.projectBackEnd.URLLocation.location;

/**
 * AppInfoController creating REST API endpoints for the frontend to connect to.
 */
@Controller("/appinfo")
public class AppInfoController {

    private final AppInfoManagerInterface infoManager = AppInfoManager.getInfoManager();
    protected final SessionManagerInterface sessionManager = SessionManager.getSessionManager();


    /**
     * Get the stored information out of the singleton manager
     * @return The AppInfo stored in the manager
     */
    @Get("/")
    public AppInfo getHospitalData(){
        return infoManager.getInfo();
    }

    /**
     * Update the AppInfo saved in the manager via an HTTP Put request
     * @param session   The login session with X-API Key to check permissions
     * @param info      The new information to be saved
     * @return A HTTP Response regarding success of the operation
     */
    @Put("/")
    public HttpResponse update(@Header("X-API-Key") String session, @Body AppInfo info) {

        if(!sessionManager.verifySession(session)) return HttpResponse.unauthorized();
        infoManager.updateInfo(info);

        return HttpResponse
                .noContent()
                .header(HttpHeaders.LOCATION, location(info.getHospitalName(), "/appinfo/").getPath());
    }
}