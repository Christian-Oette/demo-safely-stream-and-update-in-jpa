package com.christianoette.streamjpa.model;

import lombok.Getter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;

@Entity
@Getter
@FieldNameConstants
public class MyDemoEntiy implements Identifiable{

    @Id
    @GeneratedValue
    private Long id;

    @Version
    private Integer version;

    @Column
    @Getter
    public String text = "replaceMe";
}
