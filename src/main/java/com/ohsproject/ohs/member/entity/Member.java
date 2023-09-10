package com.ohsproject.ohs.member.entity;

import com.ohsproject.ohs.order.entity.Order;
import lombok.Getter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Table(name = "member")
public class Member {
    @Id @Column(name = "member_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "member")
    private List<Order> orders;

    protected Member() {

    }

    public Member(Long id, String name, List<Order> orders) {
        this.id = id;
        this.name = name;
        this.orders = orders;
    }
}
