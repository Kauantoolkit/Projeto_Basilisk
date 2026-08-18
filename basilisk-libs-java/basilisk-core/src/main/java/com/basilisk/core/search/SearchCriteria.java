package com.basilisk.core.search;

import lombok.*;

/**
 * Critério de filtro dinâmico para uso com SearchSpecification.
 *
 * Operadores suportados: "eq", "like", "gt", "lt", "in"
 *
 * Exemplo:
 *   List<SearchCriteria> filters = List.of(
 *       SearchCriteria.builder().field("name").operator("like").value("João").build(),
 *       SearchCriteria.builder().field("age").operator("gt").value(18).build()
 *   );
 *   repository.findAll(new SearchSpecification<>(filters));
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteria {

    private String field;
    /** Operadores aceitos: "eq", "like", "gt", "lt", "in" */
    private String operator;
    private Object value;
}
