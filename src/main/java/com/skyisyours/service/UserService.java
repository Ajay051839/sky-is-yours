package com.skyisyours.service;

import com.skyisyours.payload.UserDTO;

public interface UserService {
    UserDTO findById(Long id);
    UserDTO findByUsername(String username);
    UserDTO createUser(UserDTO userDTO);
    UserDTO updateUser(Long id, UserDTO userDTO);
    UserDTO deleteUser(Long id);
}
