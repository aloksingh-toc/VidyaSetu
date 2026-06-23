package in.vidyasetu.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    // Cache name constants — use these in @Cacheable annotations
    public static final String CACHE_DASHBOARD   = "dashboard";
    public static final String CACHE_DEFAULTERS  = "defaulters";
    public static final String CACHE_PLAN        = "plan";
    public static final String CACHE_HOLIDAYS    = "holidays";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager(
                CACHE_DASHBOARD,
                CACHE_DEFAULTERS,
                CACHE_PLAN,
                CACHE_HOLIDAYS
        );
        manager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .recordStats());
        return manager;
    }
}
