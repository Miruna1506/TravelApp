package org.miru.controller;

import org.miru.model.Preference;
import org.miru.repository.PreferenceRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preferences")
public class PreferenceController {

    private final PreferenceRepository preferenceRepository;

    public PreferenceController(PreferenceRepository preferenceRepository) {
        this.preferenceRepository = preferenceRepository;
    }

    @PostMapping
    public Preference savePreference(@RequestBody Preference preference) {
        return preferenceRepository.save(preference);
    }

    @GetMapping
    public Object getAllPreferences() {
        return preferenceRepository.findAll();
    }
    @DeleteMapping("/{id}")
    public void deletePreference(@PathVariable Long id) {
        preferenceRepository.deleteById(id);
    }
    @PutMapping("/{id}")
    public Preference updatePreference(@PathVariable Long id, @RequestBody Preference updatedPreference) {
        Preference preference = preferenceRepository.findById(id)
                .orElseThrow();

        preference.setDestination(updatedPreference.getDestination());
        preference.setPeriod(updatedPreference.getPeriod());
        preference.setBudget(updatedPreference.getBudget());
        preference.setInterests(updatedPreference.getInterests());

        return preferenceRepository.save(preference);
    }
}