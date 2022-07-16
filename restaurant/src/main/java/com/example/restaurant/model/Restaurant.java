package com.example.restaurant.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.util.ProxyUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Access(AccessType.FIELD)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Table(name = "restaurants")
public class Restaurant {

    @Id
    @GeneratedValue(generator = "custom-generator",
            strategy = GenerationType.IDENTITY)
    @GenericGenerator(name = "custom-generator",
            strategy = "com.example.restaurant.model.id.generator.BaseIdentifierGenerator")
    private String id;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    protected Instant createdAt;

    @UpdateTimestamp
    protected Instant modifiedAt;

    @Version
    protected int version;

    String name;

    String cuisine;

    String filename;

    String email;

    String address;

    String phone_number;

    String website;

    public String id() {
        Assert.notNull(id, "Entity must have id");
        return id;
    }

    @JsonIgnore
    public boolean isNew() {
        return id == null;
    }

    public void setEmail(String email) {
        this.email = StringUtils.hasText(email) ? email.toLowerCase() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !getClass().equals(ProxyUtils.getUserClass(o))) {
            return false;
        }
        Restaurant that = (Restaurant) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }
}