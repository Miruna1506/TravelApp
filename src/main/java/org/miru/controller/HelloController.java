package org.miru.controller;

import org.miru.model.Preference;
import org.miru.repository.PreferenceRepository;
import org.springframework.web.bind.annotation.*;

@RestController
public class HelloController {

    private final PreferenceRepository preferenceRepository;

    public HelloController(PreferenceRepository preferenceRepository) {
        this.preferenceRepository = preferenceRepository;
    }

    @PostMapping("/save")
    public Preference save(@RequestBody Preference preference) {
        return preferenceRepository.save(preference);
    }

    @GetMapping("/all")
    public Object getAll() {
        return preferenceRepository.findAll();
    }
}