package boets.bts.backend.repository.admin;

import boets.bts.backend.domain.Admin;
import boets.bts.backend.domain.AdminKeys;
import boets.bts.backend.domain.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AdminRepository extends JpaRepository<Admin, AdminKeys>, JpaSpecificationExecutor<Admin> {

}
