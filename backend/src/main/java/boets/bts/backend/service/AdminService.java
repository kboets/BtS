package boets.bts.backend.service;

import boets.bts.backend.domain.Admin;
import boets.bts.backend.domain.AdminKeys;
import boets.bts.backend.repository.admin.AdminRepository;
import boets.bts.backend.web.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AdminService {

    private Logger logger = LoggerFactory.getLogger(AdminService.class);
    private final AdminRepository adminRepository;
    private List<AdminChangeListener> changeListeners;


    public AdminService(AdminRepository adminRepository) {
        this.changeListeners = new ArrayList<>();
        this.adminRepository = adminRepository;
    }

    public void addAdminChangeListener(AdminChangeListener adminChangeListener) {
        changeListeners.add(adminChangeListener);
    }

    public boolean isTodayExecuted(AdminKeys adminKeys) {
        Optional<Admin> optionalAdmin = adminRepository.findById(adminKeys);
        if(optionalAdmin.isPresent()) {
            Admin admin = optionalAdmin.get();
            return (admin.getDate().getDayOfMonth() == (LocalDateTime.now().getDayOfMonth())
                    && admin.getValue().equals("OK"));
        }
        return false;
    }

    public boolean executeAdmin(AdminKeys adminKeys, String value) {
        Admin adminToBeUpdated = getAdmin(adminKeys);
        logger.info("Start updating adminKey " + adminToBeUpdated.getAdminKey().toString());
        adminToBeUpdated.setDate(LocalDateTime.now());
        adminToBeUpdated.setValue(value);
        adminRepository.save(adminToBeUpdated);
        return true;
    }

    public int getCurrentSeason() {
        Admin admin = getAdmin(AdminKeys.SEASON);
        if(admin.getValue() == null || LocalDate.now().getMonthValue() == Month.JULY.getValue()) {
            int season = WebUtils.getCurrentSeason();
            admin.setValue(Integer.toString(season));
            admin.setDate(LocalDateTime.now());
            adminRepository.save(admin);
        }
        return Integer.parseInt(admin.getValue());
    }

    public boolean isHistoricData() {
        int currentSeason = getCurrentSeason();
        return currentSeason != WebUtils.getCurrentSeason();
    }

    public List<Admin> getAllAdminInfo() {
        return adminRepository.findAll();
    }

    public Admin updateAdmin(Admin admin) {
        admin.setDate(LocalDateTime.now());
        admin = adminRepository.save(admin);
        if(admin.getAdminKey().equals(AdminKeys.SEASON)) {
            for (AdminChangeListener adminChangeListener: changeListeners) {
                adminChangeListener.onAdminChanged();
            }
        }
        return admin;
    }

    public Admin getAdmin(AdminKeys adminKeys) {
        Optional<Admin> optionalAdmin = adminRepository.findById(adminKeys);
        return optionalAdmin.orElseGet(() -> new Admin(adminKeys));
    }



}
