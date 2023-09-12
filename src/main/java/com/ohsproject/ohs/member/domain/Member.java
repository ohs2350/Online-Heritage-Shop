package com.ohsproject.ohs.member.domain;

import com.ohsproject.ohs.order.domain.Order;
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

    protected Member() {

    }

    public Member(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
