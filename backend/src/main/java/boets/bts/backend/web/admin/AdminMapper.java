package boets.bts.backend.web.admin;

import boets.bts.backend.domain.Admin;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AdminMapper {

    AdminDto toAdminDto(Admin admin);

    @InheritInverseConfiguration
    Admin toAdmin(AdminDto adminDto);

    List<AdminDto> toAdminDtos(List<Admin> adminList);

    @InheritInverseConfiguration
    List<Admin> toAdmins(List<AdminDto> adminDtos);


}
