package boets.bts.backend.web.admin;

import boets.bts.backend.domain.Admin;
import boets.bts.backend.service.AdminService;
import boets.bts.backend.service.result.ResultService;
import boets.bts.backend.web.WebUtils;
import boets.bts.backend.web.exception.GeneralException;
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

    @GetMapping("currentSeason")
    public int getCurrentSeason() {
        return WebUtils.getCurrentSeason();
    }

    @PostMapping("deleteResults")
    public boolean removeAllResultForLeague(@RequestBody String leagueId) {
        try {
            return resultService.removeAllResultsForLeague(Long.parseLong(leagueId));
        } catch (Exception e) {
            logger.error("Something went wrong while removing results {} ", e.getMessage());
            throw new GeneralException(e.getMessage());
        }
    }

    @PutMapping("update")
    public AdminDto updateAdmin(@RequestBody AdminDto adminDto) {
        Admin toBeUpdated = adminMapper.toAdmin(adminDto);
        return adminMapper.toAdminDto(adminService.updateAdmin(toBeUpdated));
    }

}
