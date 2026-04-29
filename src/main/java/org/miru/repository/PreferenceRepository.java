package org.miru.repository;

import org.miru.model.Preference;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreferenceRepository extends JpaRepository<Preference, Long> {
}