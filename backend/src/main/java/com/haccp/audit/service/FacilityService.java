package com.haccp.audit.service;

import com.haccp.audit.entity.Facility;
import com.haccp.audit.repository.FacilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FacilityService {
    @Autowired
    private FacilityRepository facilityRepository;

    public List<Facility> findAll() {
        return facilityRepository.findAll();
    }

    public Optional<Facility> findById(Long id) {
        return facilityRepository.findById(id);
    }

    public Facility save(Facility facility) {
        return facilityRepository.save(facility);
    }

    public void deleteById(Long id) {
        facilityRepository.deleteById(id);
    }
}
