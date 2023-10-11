package com.cirt.ctf.payload;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Top10Graph {
    private List<String> x;
    private List<Integer> y;
    private String name;
    private String type="scatter";
}
