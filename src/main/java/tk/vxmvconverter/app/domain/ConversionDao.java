package tk.vxmvconverter.app.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversionDao extends CrudRepository<Conversion, String> {
}
