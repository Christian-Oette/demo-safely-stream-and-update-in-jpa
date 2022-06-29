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
import org.springframework.transaction.support.TransactionTemplate;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings("ClassCanBeRecord")
@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateService {

    public static final int PAGE_SIZE = 5;

    private final MyDemoEntityRepository myDemoEntityRepository;
    private final TransactionTemplate transactionTemplate;

    // ------------------ Bad example which doesn't work correctly ----------------

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

    // ------------ Better example with offset paging in one transaction -------------

    @Transactional
    public void updateAllEntriesWithPageAndOffset() {
        var pageable = PageRequest.of(0, PAGE_SIZE, Sort.by(MyDemoEntiy.Fields.id));
        var highestId = 0L;
        Page<MyDemoEntiy> currentPage;
        do {
            currentPage = myDemoEntityRepository.findAllByIdOffset(highestId, pageable);
            if (currentPage.hasContent()) {
                List<MyDemoEntiy> content = currentPage.getContent();
                highestId = getMaxId(content);
                content.forEach(this::updateSingleEntity);
            }
        } while (currentPage.hasContent());
    }

    // ------------------ Example with correct transaction handling for huge data sets ----------------


    public void updateWithPageAndOffsetInMultipleTransactions() {
        var pageable = PageRequest.of(0, PAGE_SIZE, Sort.by(MyDemoEntiy.Fields.id));
        var highestId = 0L;
        Page<Long> currentPage;
        do {
            currentPage = myDemoEntityRepository.findIdsWithOffset(highestId, pageable);
            if (currentPage.hasContent()) {
                List<Long> contentIds = currentPage.getContent();
                highestId = getMax(contentIds);
                updateInNewTransaction(contentIds);
            }
        } while (currentPage.hasContent());
    }


    private void updateInNewTransaction(List<Long> contentIds) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            for (Long id: contentIds) {
                MyDemoEntiy entity = myDemoEntityRepository.findById(id);
                updateSingleEntity(entity);
            }
        });
    }

    // ------------------ Helper functions ----------------

    public void updateSingleEntity(MyDemoEntiy myDemoEntiy) {
        log.info("Update entity {}", myDemoEntiy.getId());
        myDemoEntiy.text = myDemoEntiy.text.replaceFirst("replaceMe", "");
    }

    private long getMax(List<Long> ids) {
        Optional<Long> max = ids.stream().max(Long::compareTo);
        return max.orElseThrow(() -> new IllegalStateException("Can't determine maximum id for entity"));
    }

    private long getMaxId(List<MyDemoEntiy> content) {
        Optional<Long> max = content.stream().map(MyDemoEntiy::getId).max(Long::compareTo);
        return max.orElseThrow(() -> new IllegalStateException("Can't determine maximum id for entity"));
    }

    private List<Long> extractIds(List<MyDemoEntiy> content) {
        return content.stream().map(MyDemoEntiy::getId).collect(Collectors.toList());
    }
}
