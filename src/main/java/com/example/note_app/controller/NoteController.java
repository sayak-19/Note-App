package com.example.note_app.controller;

import com.example.note_app.entity.Note;
import com.example.note_app.service.NoteService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService service;

    @GetMapping
    public List<Note> getNotesForUser(@AuthenticationPrincipal UserDetails userdetails){
        return service.getNotesForUser(userdetails.getUsername());
    }

    @GetMapping("/{id}")
    public Note getNoteById(@PathVariable Long id) {
        return service.getNoteForUser(id);
    }

    @PostMapping
    public Note createNoteForUser(@RequestBody String content, @AuthenticationPrincipal UserDetails userDetails){
        return service.createNoteForUser(content, userDetails.getUsername());
    }

    @PutMapping("/{id}")
    public Note updateNote(@RequestBody String content, @PathVariable(value = "id") Long noteId){
        return service.updateNoteForUser(noteId, content);
    }

    @DeleteMapping("/{id}")
    public void deleteNote(@PathVariable(value = "id") Long noteId){
        service.deleteNoteForUser(noteId);
    }
}
