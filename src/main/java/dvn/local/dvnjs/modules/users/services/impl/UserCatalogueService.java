package dvn.local.dvnjs.modules.users.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dvn.local.dvnjs.modules.users.entities.UserCatalogue;
import dvn.local.dvnjs.modules.users.repositories.UserCatalogueRepository;
import dvn.local.dvnjs.modules.users.requests.UserCatalogue.StoreRequest;
import dvn.local.dvnjs.modules.users.requests.UserCatalogue.UpdateRequest;
import dvn.local.dvnjs.modules.users.services.interfaces.UserCatalogueServiceInterface;
import dvn.local.dvnjs.services.BaseService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class UserCatalogueService extends BaseService implements UserCatalogueServiceInterface {

    @Autowired
    private UserCatalogueRepository userCatalogueRepository;
    
    private static final Logger logger = LoggerFactory.getLogger(UserCatalogueService.class);

    @Override
    @Transactional
    public UserCatalogue create(StoreRequest request) {

        try {
            UserCatalogue payload = UserCatalogue.builder()
                    .name(request.getName())
                    .publish(request.getPublish())
                    .build();

            return userCatalogueRepository.save(payload);
        } catch (Exception e) {
            logger.error("error {}", e.getMessage());
            throw new RuntimeException("Transaction failed" + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public UserCatalogue update(Long id, UpdateRequest request) {

        UserCatalogue userCatalogue = userCatalogueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("khong ton tai"));
            
        UserCatalogue payload = userCatalogue.toBuilder().name(request.getName()).publish(request.getPublish()).build();

        return userCatalogueRepository.save(payload);
    }
}
