package boets.bts.backend.web.admin;

import boets.bts.backend.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/admin/")
public class AdminResource {

    private Logger logger = LoggerFactory.getLogger(AdminResource.class);

    private AdminService adminService;
    private AdminMapper adminMapper;

    public AdminResource(AdminService adminService, AdminMapper adminMapper) {
        this.adminService = adminService;
        this.adminMapper = adminMapper;
    }

    @GetMapping("all")
    public List<AdminDto> getAllAdminInfo() {
        return adminMapper.toAdminDtos(adminService.getAllAdminInfo());
    }


}
