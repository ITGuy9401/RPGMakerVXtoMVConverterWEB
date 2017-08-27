package tk.vxmvconverter.app.batch.jobs;

import org.springframework.beans.factory.annotation.Autowired;
import tk.vxmvconverter.app.domain.ConversionDao;

public class ConvertImages implements Runnable {

    private ConversionDao conversionDao;

    @Autowired
    public void setConversionDao(ConversionDao conversionDao) {
        this.conversionDao = conversionDao;
    }

    @Override
    public void run() {

    }
}
