package com.example.demodatn.service;

import com.example.demodatn.entity.RoleEntity;

public interface IRoleService extends IGeneralService<RoleEntity> {
    RoleEntity findByName(String name);
}