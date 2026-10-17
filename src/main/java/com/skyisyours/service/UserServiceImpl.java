package com.skyisyours.service;

import com.skyisyours.exceptions.ResourceNotFoundException;
import com.skyisyours.model.AppUser;
import com.skyisyours.payload.UserDTO;
import com.skyisyours.repository.UserRepository;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Data
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserServiceImpl(UserRepository userRepository, @Qualifier("ModelMapperWithSkipNull") ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDTO findById(Long id) {
        AppUser user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public UserDTO findByUsername(String username) {
        AppUser user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        AppUser userToBeAdded = modelMapper.map(userDTO, AppUser.class);
        AppUser addedUser = userRepository.save(userToBeAdded);
        return modelMapper.map(addedUser, UserDTO.class);
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        AppUser user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        modelMapper.map(userDTO, user);
        AppUser updatedUser = userRepository.save(user);
        return modelMapper.map(updatedUser, UserDTO.class);
    }

    @Override
    public UserDTO deleteUser(Long id) {
        AppUser user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        userRepository.delete(user);
        return modelMapper.map(user, UserDTO.class);
    }
}
