package com.zsq.entity;

import lombok.*;

import java.util.Date;

@Data
public class Tag {
    private Integer tagId;

    private String tagName;

    private Byte isDeleted;

    private Date createTime;

}