package boets.bts.backend.web.admin;

import boets.bts.backend.domain.Admin;
import boets.bts.backend.service.admin.AdminService;
import boets.bts.backend.service.LeagueService;
import boets.bts.backend.service.forecast2.ForecastService;
import boets.bts.backend.service.result.ResultService;
import boets.bts.backend.service.standing.StandingService;
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

    private final AdminService adminService;
    private final AdminMapper adminMapper;
    private final ResultService resultService;
    private final StandingService standingService;
    private final LeagueService leagueService;
    private final ForecastService forecastService;

    public AdminResource(AdminService adminService, AdminMapper adminMapper, ResultService resultService,
                         StandingService standingService, LeagueService leagueService, ForecastService forecastService) {
        this.adminService = adminService;
        this.adminMapper = adminMapper;
        this.resultService = resultService;
        this.standingService = standingService;
        this.leagueService = leagueService;
        this.forecastService = forecastService;
    }

    @GetMapping("all")
    public List<AdminDto> getAllAdminInfo() {
        return adminMapper.toAdminDtos(adminService.getAllAdminInfo());
    }

    @GetMapping("currentSeason")
    public int getCurrentSeason() {
        return WebUtils.getCurrentSeason();
    }

    @GetMapping("currentVersion")
    public EnvironmentDto getCurrentVersion() {
        EnvironmentDto environmentDto = new EnvironmentDto();
        environmentDto.setVersion(this.adminService.getCurrentVersion());
        return environmentDto;
    }

    @PostMapping("deleteResults")
    public boolean removeAllResultForLeague(@RequestBody String leagueId) {
        try {
            return resultService.removeAllResultsForLeague(Long.parseLong(leagueId));
        } catch (Exception e) {
            logger.error("Error {} thrown while removing results for league {} ", e.getMessage(), leagueId);
            throw new GeneralException(e.getMessage());
        }
    }

    @PostMapping("deleteStanding")
    public boolean removeAllStandingForLeague(@RequestBody String leagueId) {
        try {
            return standingService.removeAllStandingForLeague(Long.parseLong(leagueId));
        } catch (Exception e) {
            logger.error("Error {} thrown while removing standing for league {} ", e.getMessage(), leagueId);
            throw new GeneralException(e.getMessage());
        }
    }

    @PostMapping("deleteLeague")
    public boolean deleteLeague(@RequestBody String leagueId) {
        try {
            logger.info("Request to delete league id {}", leagueId);
            return leagueService.deleteLeague(Long.parseLong(leagueId));
        } catch (Exception e) {
            logger.error("Error {} thrown while removing league {} ", e.getMessage(), leagueId);
            throw new GeneralException(e.getMessage());
        }
    }

    @PostMapping("deleteForecasts")
    public boolean deleteForecast(@RequestBody Long forecastId) {
        try {
            logger.info("Request to delete forecast for forecast id {} ", forecastId);
            forecastService.deleteForecast(forecastId);
        } catch (Exception e) {
            logger.error("Error {} thrown while removing forecast {}", e.getMessage(), forecastId);
            throw new GeneralException(e.getMessage());
        }
        return true;
    }

    @PutMapping("update")
    public AdminDto updateAdmin(@RequestBody AdminDto adminDto) {
        Admin toBeUpdated = adminMapper.toAdmin(adminDto);
        return adminMapper.toAdminDto(adminService.updateAdmin(toBeUpdated));
    }

}
