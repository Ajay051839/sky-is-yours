package com.skyisyours.service;

import com.skyisyours.exceptions.ResourceNotFoundException;
import com.skyisyours.model.AppUser;
import com.skyisyours.payload.UserDTO;
import com.skyisyours.payload.UserLoginDTO;
import com.skyisyours.repository.UserRepository;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Data
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    public UserServiceImpl(UserRepository userRepository, @Qualifier("ModelMapperWithSkipNull") ModelMapper modelMapper,
                           BCryptPasswordEncoder passwordEncoder, AuthenticationManager authenticationManager,
                           JWTService jwtService) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
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
        userToBeAdded.setPassword(passwordEncoder.encode(userToBeAdded.getPassword()));
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

    @Override
    public UserDTO loginUser(UserLoginDTO userLoginDTO) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLoginDTO.getUsername(), userLoginDTO.getPassword()));
        String token = jwtService.generateToken(userLoginDTO.getUsername());
        // Return the token or a DTO containing the token
        if(authentication.isAuthenticated()) {
            UserDTO userDTO = findByUsername(userLoginDTO.getUsername());
            userDTO.setToken(token); // Assuming UserDTO has a token field
            return userDTO;
        }
        throw new RuntimeException("Authentication failed");
    }
}
