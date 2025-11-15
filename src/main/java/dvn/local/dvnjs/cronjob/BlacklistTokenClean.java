package dvn.local.dvnjs.cronjob;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import dvn.local.dvnjs.modules.users.repositories.BlacklistedTokenRepository;
import jakarta.transaction.Transactional;

@Service
public class BlacklistTokenClean {
    
    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;

    @Transactional
    @Scheduled(cron="0 0 0 * * ?")
    public void cleanupExpiredToken() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        int deletedCount = blacklistedTokenRepository.deleteByExpiryDateBefore(currentDateTime);
    }
}
