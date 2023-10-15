package com.cirt.ctf.payload;

import lombok.Data;

@Data

public class CategoryBreakdown {
    public String category;
    public int solved;
    private int failed;
    public CategoryBreakdown(String _category, int _solved){
        this.category=_category;
        this.solved= _solved;
    }

}
