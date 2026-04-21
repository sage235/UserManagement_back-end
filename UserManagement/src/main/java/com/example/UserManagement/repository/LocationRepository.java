package com.example.UserManagement.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.UserManagement.model.Location;
import com.example.UserManagement.model.LocationType;

@Repository
public interface LocationRepository extends JpaRepository<Location, UUID> {
    List<Location> findByType(LocationType type);
    boolean existsByParentIdAndType(UUID parentId, LocationType type);
}