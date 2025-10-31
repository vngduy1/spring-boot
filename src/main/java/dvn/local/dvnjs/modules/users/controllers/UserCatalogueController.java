package dvn.local.dvnjs.modules.users.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dvn.local.dvnjs.modules.users.requests.UserCatalogue.StoreRequest;
import dvn.local.dvnjs.modules.users.services.interfaces.UserCatalogueServiceInterface;
import jakarta.validation.Valid;

@Validated
@RestController // このクラスがREST APIのコントローラーであることを示す
@RequestMapping("api/v1") // すべてのエンドポイントの共通パスのプレフィックスを定義
public class UserCatalogueController {
    
    // ロガーの設定
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserCatalogueServiceInterface userCatalogueService;

    public UserCatalogueController(UserCatalogueServiceInterface userCatalogueService) {
        this.userCatalogueService = userCatalogueService;
    }

    @PostMapping("/user_catalogues") // POSTメソッドで /api/v1/me にアクセスされたときに実行される
    public ResponseEntity<?> create(@Valid @RequestBody StoreRequest request) {
        
        logger.info("success");
        return ResponseEntity.ok("1234");
    }
}
