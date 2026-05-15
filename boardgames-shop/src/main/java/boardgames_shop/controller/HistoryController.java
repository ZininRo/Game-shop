package boardgames_shop.controller;

import boardgames_shop.dto.history.OrderHistoryResponse;
import boardgames_shop.service.HistoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class HistoryController {

    private final HistoryService historyService;

    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping
    public List<OrderHistoryResponse> getHistory() {
        return historyService.getHistory();
    }
}