package tk.vxmvconverter.app.batch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import tk.vxmvconverter.app.batch.jobs.CleanExpiredConversions;
import tk.vxmvconverter.app.batch.jobs.ConvertImages;

public class Processor {

    private CleanExpiredConversions cleanExpiredConversions;
    private ConvertImages convertImages;

    @Autowired
    public void setCleanExpiredConversions(CleanExpiredConversions cleanExpiredConversions) {
        this.cleanExpiredConversions = cleanExpiredConversions;
    }

    @Autowired
    public void setConvertImages(ConvertImages convertImages) {
        this.convertImages = convertImages;
    }

    @Scheduled(fixedRateString = "${batchjobs.milliseconds}")
    public void executeTasks() {
        cleanExpiredConversions.run();
        convertImages.run();
    }
}
