package com.studentjobfinder.security;

import com.studentjobfinder.entity.Student;
import com.studentjobfinder.repository.StudentRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final StudentRepository studentRepository;

    public CustomUserDetailsService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Student not found with email: " + email));

        String roleName = student.getRole() != null ? student.getRole().name() : "ROLE_STUDENT";

        return new User(
                student.getEmail(),
                student.getPasswordHash(),
                Collections.singletonList(new SimpleGrantedAuthority(roleName))
        );
    }
}
