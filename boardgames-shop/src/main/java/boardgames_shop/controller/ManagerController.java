package boardgames_shop.controller;

import boardgames_shop.dto.manager.*;
import boardgames_shop.service.ManagerService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/manager")
public class ManagerController {

    private final ManagerService managerService;

    public ManagerController(ManagerService managerService) {
        this.managerService = managerService;
    }

    @GetMapping("/new")
    public List<ManagerOrderResponse> newOrders() {
        return managerService.getNewOrders();
    }

    @PostMapping("/assign/{id}")
    public void assign(@PathVariable Long id) {
        managerService.assignOrder(id);
    }

    @GetMapping("/my")
    public List<ManagerOrderResponse> myOrders() {
        return managerService.getMyOrders();
    }

    @GetMapping("/order/{id}")
    public ManagerOrderResponse getOrder(@PathVariable Long id) {
        return managerService.getOrderById(id);
    }

    @PutMapping("/order/{id}")
    public void update(
            @PathVariable Long id,
            @RequestBody UpdateOrderRequest request
    ) {
        managerService.updateOrder(id, request);
    }

    @GetMapping("/all")
    public List<ManagerOrderResponse> allOrders() {
        return managerService.getAllOrders();
    }

}