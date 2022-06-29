package com.christianoette.streamjpa.controller;

import com.christianoette.streamjpa.model.MyDemoEntityRepository;
import com.christianoette.streamjpa.model.MyDemoEntiy;
import com.christianoette.streamjpa.service.UpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@SuppressWarnings("ClassCanBeRecord")
@RestController
@RequiredArgsConstructor
@Slf4j
public class ApiController {

    public static final int DEFAULT_PAGE_SIZE = 20;

    @GetMapping(value = "/")
    public RedirectView redirectToSwagger() {
        return new RedirectView("/swagger-ui/index.html");
    }

    private final MyDemoEntityRepository myDemoEntityRepository;
    private final UpdateService updateService;

    @EventListener(ApplicationReadyEvent.class)
    public void createDemoData() {
        log.info("Save demo entries");
        for (int i = 0; i< DEFAULT_PAGE_SIZE; i++) {
            myDemoEntityRepository.save(new MyDemoEntiy());
        }
    }

    @PostMapping(value = "/api/update-whole-table-with-offset")
    public void updateWholeTableWithOffset() {
        updateService.updateAllEntriesWithPageAndOffset();
    }

    @PostMapping(value = "/api/update-whole-table-with-paging-only")
    public void updateWholeTableWithPaging() {
        updateService.updateWholeTableWithPagingOnly();
    }

    @GetMapping(value = "/api/get-data")
    public List<MyDemoEntiy> getData() {
        Pageable pageable = PageRequest.of(0, DEFAULT_PAGE_SIZE);
        return myDemoEntityRepository.findPaged(pageable);
    }

}
