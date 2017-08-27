package tk.vxmvconverter.app.batch.jobs;

import org.springframework.beans.factory.annotation.Autowired;
import tk.vxmvconverter.app.domain.Conversion;
import tk.vxmvconverter.app.domain.ConversionDao;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.List;

public class CleanExpiredConversions implements Runnable {

    private ConversionDao conversionDao;

    @Autowired
    public void setConversionDao(ConversionDao conversionDao) {
        this.conversionDao = conversionDao;
    }

    @Override
    @Transactional
    public void run() {
        List<Conversion> byConversionCompletedAfter = conversionDao.findByConversionCompletedAfter(ZonedDateTime.now().minusDays(1));
        conversionDao.delete(byConversionCompletedAfter);
    }
}
