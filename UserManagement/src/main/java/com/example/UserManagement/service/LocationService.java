package com.example.UserManagement.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.UserManagement.model.Location;
import com.example.UserManagement.model.LocationType;
import com.example.UserManagement.repository.LocationRepository;
import com.example.UserManagement.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LocationService {
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;

    public Location createLocation(Location location) {
        validateParentConsistency(location);
        return locationRepository.save(location);
    }

    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    public Location getLocationById(UUID id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + id));
    }

    public Location updateLocation(UUID id, Location updated) {
        Location existing = getLocationById(id);
        existing.setName(updated.getName());
        existing.setType(updated.getType());
        existing.setParent(updated.getParent());
        validateParentConsistency(existing);
        return locationRepository.save(existing);
    }

    public void deleteLocation(UUID id) {
        Location location = getLocationById(id);
        if (location.getType() == LocationType.VILLAGE && userRepository.existsByLocationId(id)) {
            throw new RuntimeException("Cannot delete Village location because it is assigned to one or more users.");
        }
        if (!location.getChildren().isEmpty()) {
            throw new RuntimeException("Cannot delete location because it has child locations. Delete children first.");
        }
        locationRepository.deleteById(id);
    }

    public List<Location> getPossibleParentsForType(LocationType childType) {
        LocationType requiredParentType = getRequiredParentType(childType);
        if (requiredParentType == null) {
            return List.of();
        }
        return locationRepository.findByType(requiredParentType);
    }

    private void validateParentConsistency(Location location) {
        LocationType type = location.getType();
        Location parent = location.getParent();

        if (type == LocationType.PROVINCE) {
            if (parent != null) {
                throw new RuntimeException("Province cannot have a parent.");
            }
            return;
        }

        LocationType requiredParentType = getRequiredParentType(type);
        if (parent == null) {
            throw new RuntimeException(type + " must have a parent of type " + requiredParentType);
        }

        // Fetch full parent from DB to get its type
        Location fullParent = locationRepository.findById(parent.getId())
                .orElseThrow(() -> new RuntimeException("Parent location not found with id: " + parent.getId()));

        if (fullParent.getType() != requiredParentType) {
            throw new RuntimeException("Parent of " + type + " must be " + requiredParentType + ", but was " + fullParent.getType());
        }
    }

    private LocationType getRequiredParentType(LocationType childType) {
        switch (childType) {
            case DISTRICT: return LocationType.PROVINCE;
            case SECTOR:   return LocationType.DISTRICT;
            case CELL:     return LocationType.SECTOR;
            case VILLAGE:  return LocationType.CELL;
            default:       return null;
        }
    }
}