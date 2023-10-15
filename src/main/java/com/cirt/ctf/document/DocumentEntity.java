package com.cirt.ctf.document;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Data
@Table(name = "tbl_documents")
@NoArgsConstructor
@AllArgsConstructor
public class DocumentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "original_filename")
    private String originalFileName;

    @Column(name = "file_location")
    private String fileLocation;

    @Column(name = "save_filename")
    private String saveFileName;

    @Column(name = "entity_id")
    private Long entityID;

}
