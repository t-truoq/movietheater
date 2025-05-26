package org.example.movie.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "MOVIETHEATER_TYPE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Type {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TYPE_ID")
    private Long typeId;

    @Column(name = "TYPE_NAME")
    private String typeName;

    @OneToMany(mappedBy = "type")
    @JsonIgnore
    private List<MovieType> movieTypes;
}
