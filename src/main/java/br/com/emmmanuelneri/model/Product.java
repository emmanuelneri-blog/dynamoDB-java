package br.com.emmmanuelneri.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class Product {

    @Getter
    private String code;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String description;
}
