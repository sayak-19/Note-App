package com.example.note_app.service;

import com.example.note_app.entity.Note;
import com.example.note_app.repository.NoteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class NoteService {

    private final NoteRepository repository;

    public Note createNoteForUser(String content, String username){
        Note note = Note.builder()
                .content(content)
                .ownerUsername(username)
                .build();
        return repository.save(note);
    }

    public Note updateNoteForUser(Long id, String content){
        Note note = repository.findById(id).orElseThrow(
                ()-> new EntityNotFoundException("Note for Id = "+id+" not found!")
        );
        note.setContent(content);
        return repository.save(note);
    }

    public void deleteNoteForUser(Long id){
        repository.deleteById(id);
    }

    public List<Note> getNotesForUser(String username){
        return repository.findByOwnerUsername(username).orElseThrow(
                ()-> new EntityNotFoundException("Note for username = "+username+" not found!")
        );
    }
}
