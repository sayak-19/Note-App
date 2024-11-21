package com.example.note_app.repository;

import com.example.note_app.entity.Note;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface NoteRepository extends JpaRepository<Note, Long> {

    Optional<List<Note>> findByOwnerUsername(String ownerUsername);
}
