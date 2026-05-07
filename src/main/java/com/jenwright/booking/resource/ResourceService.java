package com.jenwright.booking.resource;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;

    public List<Resource> getAllActive() {
        return resourceRepository.findByActiveTrue();
    }

    public Resource getById(Long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Resource not found: " + id));
    }

    public Resource create(Resource resource) {
        return resourceRepository.save(resource);
    }

    public Resource update(Long id, Resource updated) {
        Resource existing = getById(id);
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setCapacity(updated.getCapacity());
        return resourceRepository.save(existing);
    }

    public void deactivate(Long id) {
        Resource resource = getById(id);
        resource.setActive(false);
        resourceRepository.save(resource);
    }
}
