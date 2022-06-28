package com.christianoette.streamjpa.service;

import com.christianoette.streamjpa.model.MyDemoEntityRepository;
import com.christianoette.streamjpa.model.MyDemoEntiy;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("ClassCanBeRecord")
@Service
@RequiredArgsConstructor
public class UpdateService {

    public static final int PAGE_SIZE = 5;

    private final MyDemoEntityRepository myDemoEntityRepository;

    @Transactional
    public void updateWholeTableWithPagingOnly() {
        var pageNumber = 0;
        var pageable = PageRequest.of(pageNumber, PAGE_SIZE, Sort.by(MyDemoEntiy.Fields.id));
        Page<MyDemoEntiy> page;
        do {
            page = myDemoEntityRepository.findWithUnsafePaging(pageable);
            page.getContent().forEach(this::updateSingleEntity);
            pageNumber++;
        } while(!page.isEmpty());
    }

    @Transactional
    public void updateAllEntriesWithOffset() {
        var pageable = PageRequest.of(0, PAGE_SIZE, Sort.by(MyDemoEntiy.Fields.id));

        var hasMoreEntries = false;
        var highestId = 0L;
        do {
            Page<MyDemoEntiy> currentPage = myDemoEntityRepository.findAllByIdOffset(highestId, pageable);
            hasMoreEntries = currentPage.hasNext();
            List<MyDemoEntiy> content = currentPage.getContent();

            highestId = findHighestId(content);
            content.forEach(this::updateSingleEntity);
        } while (hasMoreEntries);
    }

    public void updateSingleEntity(MyDemoEntiy myDemoEntiy) {
        myDemoEntiy.text = myDemoEntiy.text.replaceFirst("replaceMe", "").trim();
    }

    private long findHighestId(List<MyDemoEntiy> content) {
        Optional<Long> max = content.stream().map(MyDemoEntiy::getId).max(Long::compareTo);
        return max.orElseThrow(() -> new IllegalStateException("Can't determine maximum id for entity"));
    }
}
