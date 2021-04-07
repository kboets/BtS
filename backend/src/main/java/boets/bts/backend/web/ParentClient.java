package boets.bts.backend.web;

import boets.bts.backend.domain.AdminKeys;
import boets.bts.backend.service.AdminService;
import okhttp3.Request;

import java.util.concurrent.ConcurrentHashMap;

public class ParentClient {

    protected final AdminService adminService;

    private ConcurrentHashMap<String, String> rapidApiPrincipals;
    private String hostKey = "x-rapidapi-host";
    private String hostPassword = "x-rapidapi-key";

    public ParentClient(AdminService adminService) {
        this.adminService = adminService;
        rapidApiPrincipals = new ConcurrentHashMap<>();
    }

    public Request createRequest(String url) {
        String host;
        String password;
        if(rapidApiPrincipals.containsKey(hostKey)) {
            host = rapidApiPrincipals.get(hostKey);
            password = rapidApiPrincipals.get(hostPassword);
        } else {
            host = adminService.getAdmin(AdminKeys.RAPID_API_HOST).getValue();
            password = adminService.getAdmin(AdminKeys.RAPID_API_PASSWORD).getValue();
            rapidApiPrincipals.put(hostKey, host);
            rapidApiPrincipals.put(hostPassword, password);
        }

        return new Request.Builder()
                .get()
                .url(url)
                .addHeader(hostKey, host)
                .addHeader(hostPassword, password)
                .build();
    }

}
