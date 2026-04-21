package com.example.UserManagement.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.UserManagement.model.Location;
import com.example.UserManagement.model.LocationType;
import com.example.UserManagement.service.LocationService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/locations")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class LocationController {
    private final LocationService locationService;

    // Create
    @PostMapping
    public ResponseEntity<?> createLocation(@RequestBody Location location) {
        try {
            Location created = locationService.createLocation(location);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Read all
    @GetMapping
    public ResponseEntity<List<Location>> getAllLocations() {
        return ResponseEntity.ok(locationService.getAllLocations());
    }

    // Read one
    @GetMapping("/{id}")
    public ResponseEntity<?> getLocationById(@PathVariable UUID id) {
        try {
            Location location = locationService.getLocationById(id);
            return ResponseEntity.ok(location);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLocation(@PathVariable UUID id, @RequestBody Location location) {
        try {
            Location updated = locationService.updateLocation(id, location);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLocation(@PathVariable UUID id) {
        try {
            locationService.deleteLocation(id);
            return ResponseEntity.ok("Location deleted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Helper: return all possible parent locations for a given type (used by frontend dynamic dropdown)
    @GetMapping("/parent-options")
    public ResponseEntity<?> getParentOptions(@RequestParam LocationType type) {
        List<Location> parents = locationService.getPossibleParentsForType(type);
        return ResponseEntity.ok(parents);
    }

    // Helper: return all location types (for select input)
    @GetMapping("/types")
    public ResponseEntity<LocationType[]> getAllTypes() {
        return ResponseEntity.ok(LocationType.values());
    }
}