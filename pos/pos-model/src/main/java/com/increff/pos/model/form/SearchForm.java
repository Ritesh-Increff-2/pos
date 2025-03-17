package com.increff.pos.model.form;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class SearchForm {
    private String searchPattern;
    private String searchType;
    private int page;
    private int size;
}