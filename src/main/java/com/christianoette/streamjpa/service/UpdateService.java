package com.christianoette.streamjpa.service;

import com.christianoette.streamjpa.model.MyDemoEntityRepository;
import com.christianoette.streamjpa.model.MyDemoEntiy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("ClassCanBeRecord")
@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateService {

    public static final int PAGE_SIZE = 5;

    private final MyDemoEntityRepository myDemoEntityRepository;

    @Transactional
    public void updateWholeTableWithPagingOnly() {
        Page<MyDemoEntiy> page;
        Pageable pageable = PageRequest.of(0, PAGE_SIZE, Sort.by(MyDemoEntiy.Fields.id));
        do {
            page = myDemoEntityRepository.findWithPaging(pageable);
            page.getContent().forEach(this::updateSingleEntity);
            pageable = pageable.next();
        } while(page.hasContent());
    }

    @Transactional
    public void updateAllEntriesWithPageAndOffset() {
        var pageable = PageRequest.of(0, PAGE_SIZE, Sort.by(MyDemoEntiy.Fields.id));
        var highestId = 0L;
        Page<MyDemoEntiy> currentPage;
        do {
            currentPage = myDemoEntityRepository.findAllByIdOffset(highestId, pageable);
            List<MyDemoEntiy> content = currentPage.getContent();

            highestId = findHighestId(content);
            content.forEach(this::updateSingleEntity);
        } while (currentPage.hasContent());
    }

    public void updateSingleEntity(MyDemoEntiy myDemoEntiy) {
        log.info("Update entity {}", myDemoEntiy.getId());
        myDemoEntiy.text = myDemoEntiy.text.replaceFirst("replaceMe", "");
    }

    private long findHighestId(List<MyDemoEntiy> content) {
        Optional<Long> max = content.stream().map(MyDemoEntiy::getId).max(Long::compareTo);
        return max.orElseThrow(() -> new IllegalStateException("Can't determine maximum id for entity"));
    }
}
