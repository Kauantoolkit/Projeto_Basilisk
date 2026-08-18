package com.basilisk.core.search;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Implementação de Specification que converte uma lista de SearchCriteria
 * em predicados JPA, permitindo filtros dinâmicos sem query manual.
 *
 * Uso:
 *   repository.findAll(new SearchSpecification<>(criteria), pageable);
 */
public class SearchSpecification<T> implements Specification<T> {

    private final List<SearchCriteria> criteria;

    public SearchSpecification(List<SearchCriteria> criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        for (SearchCriteria c : criteria) {
            switch (c.getOperator()) {
                case "eq"   -> predicates.add(cb.equal(root.get(c.getField()), c.getValue()));
                case "like" -> predicates.add(cb.like(cb.lower(root.get(c.getField())),
                                    "%" + c.getValue().toString().toLowerCase() + "%"));
                case "gt"   -> predicates.add(cb.greaterThan(root.get(c.getField()),
                                    (Comparable) c.getValue()));
                case "lt"   -> predicates.add(cb.lessThan(root.get(c.getField()),
                                    (Comparable) c.getValue()));
                case "in"   -> predicates.add(root.get(c.getField()).in((Collection<?>) c.getValue()));
            }
        }
        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
