package com.christianoette.streamjpa.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MyDemoEntityRepository extends Repository<MyDemoEntiy, Long> {

    /**
     * With an offset you can guarantee to modify a record only once
     */
    @Query(value = "from MyDemoEntiy ent where ent.text like '%replaceMe%' and ent.id >:idOffset")
    Page<MyDemoEntiy> findAllByIdOffset(@Param(value = "idOffset") Long idOffset, Pageable pageRequest);

    /**
     * With simple paging you might change the same value twice
     */
    @Query(value = "from MyDemoEntiy ent where ent.text like '%replaceMe%'")
    Page<MyDemoEntiy> findWithUnsafePaging(Pageable pageRequest);


    void save(MyDemoEntiy entity);

    @Query(value = "from MyDemoEntiy ent")
    List<MyDemoEntiy> findPaged(Pageable pageable);
}
