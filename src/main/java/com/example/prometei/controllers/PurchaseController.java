package com.example.prometei.controllers;

import com.example.prometei.dto.PurchaseDtos.CreatePurchaseDto;
import com.example.prometei.dto.PurchaseDtos.PurchaseDto;
import com.example.prometei.models.Purchase;
import com.example.prometei.services.TransformDataService;
import com.example.prometei.services.baseServices.PurchaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.prometei.utils.CipherUtil.decryptId;

@CrossOrigin
@RestController
@RequestMapping("/purchase")
public class PurchaseController {
    private final PurchaseService purchaseService;
    private final TransformDataService transformDataService;

    public PurchaseController(PurchaseService purchaseService, TransformDataService transformDataService){
        this.purchaseService = purchaseService;
        this.transformDataService = transformDataService;
    }

    /**
     * Получает информацию о покупке по её идентификатору.
     *
     * @param purchaseId идентификатор покупки, которую требуется получить (зашифрованный)
     * @return ResponseEntity с объектом PurchaseDto в случае успешного получения информации о покупке, либо ResponseEntity с кодом NO_CONTENT, если покупка не найдена
     */
    @GetMapping("/get")
    public ResponseEntity<PurchaseDto> getPurchase(@RequestParam String purchaseId) {
        Purchase purchase = purchaseService.getById(decryptId(purchaseId));
        return purchase == null
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(transformDataService.transformToPurchaseDto(purchase), HttpStatus.OK);
    }

    /**
     * Получает список всех покупок.
     *
     * @return ResponseEntity со списком PurchaseDto всех покупок в случае успешного получения, иначе пустой список.
     */
    @GetMapping("/all")
    public ResponseEntity<List<PurchaseDto>> getAllPurchases() {
        return new ResponseEntity<>(purchaseService.getAll()
                .stream().map(transformDataService::transformToPurchaseDto).toList(), HttpStatus.OK);
    }

    /**
     * Получает список всех покупок пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя, чьи покупки требуется получить
     * @return ResponseEntity со списком PurchaseDto всех покупок пользователя в случае успешного получения, иначе пустой список.
     */
    @GetMapping("/getByUser")
    public ResponseEntity<List<PurchaseDto>> getPurchasesByUser(@RequestParam String userId) {
        return new ResponseEntity<>(purchaseService.getPurchasesByUser(decryptId(userId))
                .stream().map(transformDataService::transformToPurchaseDto).toList(), HttpStatus.OK);
    }

    /**
     * Создает новую покупку на основе переданных данных.
     *
     * @param createPurchaseDto объект CreatePurchaseDto, содержащий информацию о новой покупке
     */
    @PostMapping("/create")
    public ResponseEntity<String> addPurchase(@RequestBody CreatePurchaseDto createPurchaseDto) {
        return new ResponseEntity<>(purchaseService.createPurchase(transformDataService.transformToPurchase(createPurchaseDto),
                                       transformDataService.decryptTicketIds(createPurchaseDto.getTickets()),
                                       createPurchaseDto.getUser(),
                                       createPurchaseDto.getPassengers() == null ?
                                               null :
                                               transformDataService.listPassengerDtoToUnAuthUser(createPurchaseDto.getPassengers())), HttpStatus.OK);
    }
}
