package com.example.Booking.Security;




import com.example.Booking.Entity.User;
import com.example.Booking.Repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServicecImpl implements UserDetailsService {

    @Autowired
    private UserRepository urepo;


    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        Optional<User> user = urepo.findByEmail(usernameOrEmail);
        if (user.isEmpty()) {
            user = urepo.findByUsername(usernameOrEmail);
        }

        return new Userdetails(user.orElseThrow(
                () -> new UsernameNotFoundException("User not found")));

    }
}


