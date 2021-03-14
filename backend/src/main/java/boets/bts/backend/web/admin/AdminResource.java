package boets.bts.backend.web.admin;

import boets.bts.backend.service.AdminService;
import boets.bts.backend.service.result.ResultService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/admin/")
public class AdminResource {

    private Logger logger = LoggerFactory.getLogger(AdminResource.class);

    private AdminService adminService;
    private AdminMapper adminMapper;
    private ResultService resultService;

    public AdminResource(AdminService adminService, AdminMapper adminMapper, ResultService resultService) {
        this.adminService = adminService;
        this.adminMapper = adminMapper;
        this.resultService = resultService;
    }

    @GetMapping("all")
    public List<AdminDto> getAllAdminInfo() {
        return adminMapper.toAdminDtos(adminService.getAllAdminInfo());
    }

    @PostMapping("/deleteResults")
    public boolean removeAllResultForLeague(@RequestBody String leagueId) {
        return resultService.removeAllResultsForLeague(Long.parseLong(leagueId));
    }
}
