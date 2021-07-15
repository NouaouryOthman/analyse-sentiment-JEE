package ma.emsi.pfa.dao;

import ma.emsi.pfa.entities.Compte;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CompteRepository extends JpaRepository<Compte, Long> {
    @Query("select c from Compte c where c.client.id=:x")
    Page<Compte> listeComptes(@Param("x") Long id, Pageable page);
}
