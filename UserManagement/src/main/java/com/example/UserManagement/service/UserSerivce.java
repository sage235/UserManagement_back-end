package com.example.UserManagement.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.UserManagement.model.Location;
import com.example.UserManagement.model.LocationType;
import com.example.UserManagement.model.User;
import com.example.UserManagement.repository.LocationRepository;
import com.example.UserManagement.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserSerivce {
    private final UserRepository userrepo;
    private final LocationRepository locationRepository;   // added for location validation

    public User register(User user) {
        boolean userExists = userrepo.existsByEmail(user.getEmail());
        if (userExists) {
            throw new RuntimeException("User with email " + user.getEmail() + " already exists");
        }
        // Validate location is a Village
        validateUserLocation(user.getLocation());
        return userrepo.save(user);
    }

    public User login(String email, String password) {
        boolean existByEmail = userrepo.existsByEmail(email);
        boolean existByPassword = userrepo.existsByPassword(password);

        if (existByEmail && existByPassword) {
            return userrepo.findByEmailAndPassword(email, password).get();
        } else if (!existByEmail) {
            throw new RuntimeException("Invalid email");
        } else {
            throw new RuntimeException("Invalid password");
        }
    }

    public List<User> getAll() {
        return userrepo.findAll();
    }

    public User getById(UUID id) {
        return userrepo.findById(id).orElse(null);
    }

    public String deleteById(UUID id) {
        userrepo.deleteById(id);
        return "User deleted successfully";
    }

    public User updateById(UUID id, User user) {
        if (!userrepo.existsById(id)) {
            throw new RuntimeException("User with ID " + id + " does not exist");
        }
        // Validate location is a Village
        validateUserLocation(user.getLocation());
        user.setId(id);
        return userrepo.save(user);
    }

    private void validateUserLocation(Location location) {
        if (location == null) {
            throw new RuntimeException("User must be assigned to a Village location");
        }
        Location managedLocation = locationRepository.findById(location.getId())
                .orElseThrow(() -> new RuntimeException("Location not found with ID: " + location.getId()));
        if (managedLocation.getType() != LocationType.VILLAGE) {
            throw new RuntimeException("User can only be assigned to a Location of type VILLAGE, but got: " + managedLocation.getType());
        }
    }
}