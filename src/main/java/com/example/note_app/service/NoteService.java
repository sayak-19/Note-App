package com.example.note_app.service;

import com.example.note_app.entity.Note;
import com.example.note_app.repository.NoteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class NoteService {

    private final NoteRepository repository;
    private final AuditLogService auditLogService;

    public Note createNoteForUser(String content, String username){
        Note note = Note.builder()
                .content(content)
                .ownerUsername(username)
                .createdAt(LocalDateTime.now())
                .build();
        note = repository.save(note);
        auditLogService.auditNoteAction("CREATE", note);
        return note;
    }

    public Note updateNoteForUser(Long id, String content){
        Note note = repository.findById(id).orElseThrow(
                ()-> new EntityNotFoundException("Note for Id = "+id+" not found!")
        );
        note.setContent(content);
        note.setCreatedAt(LocalDateTime.now());
        note = repository.save(note);
        auditLogService.auditNoteAction("UPDATE", note);
        return note;
    }

    public void deleteNoteForUser(Long id){
        Note note = repository.findById(id).orElseThrow(
                ()-> new EntityNotFoundException("Note for Id = "+id+" not found!")
        );
        repository.deleteById(id);
        auditLogService.auditNoteAction("DELETE", note);
    }

    public List<Note> getNotesForUser(String username){
        return repository.findByOwnerUsername(username).orElseThrow(
                ()-> new EntityNotFoundException("Note for username = "+username+" not found!")
        );
    }

    public  Note getNoteForUser(Long id){
        return repository.findById(id).orElseThrow(
                ()-> new EntityNotFoundException("Note for Id = "+id+" not found!")
        );
    }
}
