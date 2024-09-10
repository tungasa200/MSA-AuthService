package com.yjmedia.yvisbig.bizcom.voHeader;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SearchResultVO extends ResponseHeaderVO {
    private int totalCounter;
    private int pageNumber;
    private int pageSize;
    private int totalPage;
    private List<Object> resultList;
}
