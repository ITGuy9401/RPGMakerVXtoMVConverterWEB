package tk.vxmvconverter.app.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface ConversionDao extends CrudRepository<Conversion, String> {

    Conversion findFirstByStatusOrderByConversionRequestAsc(Status status);

    List<Conversion> findByConversionCompletedAfter(ZonedDateTime conversionCompleted);
}
