package com.rideal.ridealbackend.domain;

import org.springframework.data.annotation.Id;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

import javax.persistence.Column;
import javax.persistence.GenerationType;
import javax.validation.constraints.NotNull;

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Column(columnDefinition = "VARCHAR", length = 100)
    private String title;

    @NotNull
    @Column(columnDefinition = "VARCHAR", length = 100)
    private String author;

    @Column(columnDefinition = "VARCHAR", length = 1000)
    private String blurb;

    private int pages;
}
