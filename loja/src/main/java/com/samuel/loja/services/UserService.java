package com.samuel.loja.services;

import com.samuel.loja.dto.RoleDTO;
import com.samuel.loja.dto.UserDto;
import com.samuel.loja.dto.UserInsertDTO;
import com.samuel.loja.entities.Role;
import com.samuel.loja.entities.User;
import com.samuel.loja.repository.RoleRepository;
import com.samuel.loja.repository.UserRepository;
import com.samuel.loja.repository.UserRepository;
import com.samuel.loja.services.exceptions.DataBaseException;
import com.samuel.loja.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

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
        User user = new User();
        copyDtoToEntity(userInsertDTO, user);
        user.setPassword(passwordEncoder.encode(userInsertDTO.getPassword()));
        User userSaved = userRepository.save(user);
        return new UserDto(userSaved);
    }

    @Transactional
    public UserDto update(Long id, UserDto userDto) {
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

}
