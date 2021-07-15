package ma.emsi.pfa.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Collection;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@DiscriminatorValue("CLT")
public class Client extends User {
    @OneToMany(mappedBy = "client")
    private Collection<Compte> comptes;
}
