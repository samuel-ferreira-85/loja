package com.samuel.loja.services;

import com.samuel.loja.dto.RoleDTO;
import com.samuel.loja.dto.UserDto;
import com.samuel.loja.dto.UserInsertDTO;
import com.samuel.loja.entities.Role;
import com.samuel.loja.entities.User;
import com.samuel.loja.projections.UserDetailsProjection;
import com.samuel.loja.repository.RoleRepository;
import com.samuel.loja.repository.UserRepository;
import com.samuel.loja.services.exceptions.DataBaseException;
import com.samuel.loja.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class UserService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public Page<UserDto> findAllPaged(Pageable pageable) {
        Page<User> list = userRepository.findAll(pageable);

        return list.map(c -> new UserDto(c));
    }

    @Transactional(readOnly = true)
    public UserDto findById(Long id) {
        User user = getUser(id);
        return new UserDto(user);
    }

    @Transactional
    public UserDto insert(UserInsertDTO userInsertDTO) {
        validarInsertEmail(userInsertDTO);
        User user = new User();
        copyDtoToEntity(userInsertDTO, user);
        user.setPassword(userInsertDTO.getPassword());
        User userSaved = userRepository.save(user);
        return new UserDto(userSaved);
    }

    @Transactional
    public UserDto update(Long id, UserDto userDto) {
        validarUpdateEmail(id, userDto);
        try {
            User user = userRepository.getReferenceById(id);
//        BeanUtils.copyProperties(userDto, user, "id");
            copyDtoToEntity(userDto, user);
            return new UserDto(user);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id not found " + id);
        }
    }

    public User getUser(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Não há recurso para o id: " + id));
    }

    public void delete(Long id) {
        try {
            if (userRepository.existsById(id)) {
                userRepository.deleteById(id);
            } else {
                throw new ResourceNotFoundException("Resource not found.");
            }
        } catch (DataIntegrityViolationException e) {
            throw new DataBaseException("Integrity violation.");
        }
    }


    private void copyDtoToEntity(UserDto userDto, User user) {
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());

        user.getRoles().clear();
        for (RoleDTO roleDTO : userDto.getRoles()) {
            Role role = roleRepository.getReferenceById(roleDTO.getId());
            user.getRoles().add(role);
        }
    }

    private void validarUpdateEmail(Long id, UserDto dto) {
        User user = userRepository.getReferenceById(id); // usuario que quero cadastrar
        var userEmail = userRepository.findByEmail(dto.getEmail()); // ver se o usário tem email cadastrado

        if (userEmail.isPresent() && user.getId() != userEmail.get().getId() ) {
            throw new IllegalArgumentException("Email já cadastrado.");
        }
    }
    private void validarInsertEmail(UserDto dto) {
        var userEmail = userRepository.findByEmail(dto.getEmail()); // ver se o usário tem email cadastrado

        if (userEmail.isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado.");
        }
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserDetailsProjection> result = userRepository.searchUserAndRolesByEmail(username);
        if (result.size() == 0) {
            throw new UsernameNotFoundException("Email not found");
        }
        User user = new User();
        user.setEmail(username);
        user.setPassword(result.get(0).getPassword());
        for (UserDetailsProjection p: result) {
            user.addRole(new Role(p.getRoleId(), p.getAuthority()));
        }
        return user;
    }

    protected User authenticated() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();
            String username = jwtPrincipal.getClaim("username");

            return userRepository.findByEmail(username).get();
        } catch (Exception e) {
            throw new UsernameNotFoundException("Email not found");
        }
    }

    @Transactional(readOnly = true)
    public UserDto getMe(){
        var user = authenticated();
        return new UserDto(user);
    }
}
