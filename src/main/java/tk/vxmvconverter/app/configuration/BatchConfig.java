package tk.vxmvconverter.app.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.vxmvconverter.app.batch.jobs.CleanExpiredConversions;
import tk.vxmvconverter.app.batch.jobs.ConvertImages;

@Configuration
@EnableScheduling
public class BatchConfig {

    @Bean
    public CleanExpiredConversions getCleanExpiredConversions() {
        return new CleanExpiredConversions();
    }

    @Bean
    public ConvertImages getConvertImages() {
        return new ConvertImages();
    }
}
