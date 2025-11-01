package dvn.local.dvnjs.modules.users.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dvn.local.dvnjs.modules.users.entities.UserCatalogue;
import dvn.local.dvnjs.modules.users.requests.UserCatalogue.StoreRequest;
import dvn.local.dvnjs.modules.users.requests.UserCatalogue.UpdateRequest;
import dvn.local.dvnjs.modules.users.resources.UserCatalogueResource;
import dvn.local.dvnjs.modules.users.services.interfaces.UserCatalogueServiceInterface;
import dvn.local.dvnjs.resources.ApiResource;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.persistence.EntityNotFoundException;


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

        UserCatalogue userCatalogue = userCatalogueService.create(request);

        UserCatalogueResource userCatalogueResource = UserCatalogueResource.builder()
                .id(userCatalogue.getId())
                .name(userCatalogue.getName())
                .publish(userCatalogue.getPublish())
                .build();

        ApiResource<UserCatalogueResource> response = ApiResource.ok(userCatalogueResource, "定義書追加成功されました。");
        logger.info("success");
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/user_catalogues/{id}")
    public ResponseEntity<?> update(
        @PathVariable Long id,
        @Valid @RequestBody UpdateRequest request) {
        //TODO: process PUT request
        try {
            UserCatalogue userCatalogue = userCatalogueService.update(id, request);
            UserCatalogueResource userCatalogueResource = UserCatalogueResource.builder()
                    .id(userCatalogue.getId())
                    .name(userCatalogue.getName())
                    .publish(userCatalogue.getPublish())
                    .build();

            ApiResource<UserCatalogueResource> response = ApiResource.ok(userCatalogueResource, "定義書アップデート成功されました。");
            logger.info("success");
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResource.error(
                    "NOT_FOUND",
                    e.getMessage(),
                    HttpStatus.NOT_FOUND
                )
            );
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResource.error(
                    "INTERNAL_SERVER_ERROR",
                    "Có lỗi xảy ra trong quá trình cập nhật",
                    HttpStatus.INTERNAL_SERVER_ERROR
                )
            );
        }
                
    }
}
